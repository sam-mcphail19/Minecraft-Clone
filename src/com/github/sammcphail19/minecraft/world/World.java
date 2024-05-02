package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.physics.BoxCollider;
import com.github.sammcphail19.engine.physics.Collision;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.Player;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class World {
    public static final int WORLD_HEIGHT = 128;
    private final int CHUNK_RENDER_DISTANCE = 3;

    @Getter
    private final Map<Vector3I, Chunk> chunks = new HashMap<>();

    private WorldGenerator worldGenerator;
    private Player player;

    public void generate() {
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                generateChunk(new Vector3I(x, 0, z));
            }
        }
    }

    public void update() {
        player.update();

        // TODO: Do this on another thread
        Vector3I currentChunkChunkCoord = getChunkCoord(new Vector3I(player.getPos()));
        for (int x = -CHUNK_RENDER_DISTANCE; x < CHUNK_RENDER_DISTANCE; x++) {
            for (int z = -CHUNK_RENDER_DISTANCE; z < CHUNK_RENDER_DISTANCE; z++) {
                Vector3I pos = currentChunkChunkCoord.add(new Vector3I(x, 0, z));
                if (!chunks.containsKey(pos)) {
                    generateChunk(pos);
                }
            }
        }

        if (player.isInCreativeMode()) {
            player.setPos(player.getPos().add(player.getVelocity()));
            return;
        }

        if (!player.isJumping() && playerIsFloating(player)) {
            player.setJumping(true);
        }

        List<Collision> collisions = checkPlayerCollisions(player);
        if (!collisions.isEmpty()) {
            Collision collision = collisions.stream().sorted().findFirst().get();

            Vector3 adjustedVelocity = collision.getAdjustedVelocity(player.getVelocity());
            Vector3 offset = collision.getNormal().multiply(1e-6);

            Vector3 newPos = (player.getPos().add(adjustedVelocity)).add(offset);
            player.setPos(newPos);

            if (collision.getNormal().getX() != 0) {
                player.getVelocity().setX(0);
            }
            if (collision.getNormal().getY() != 0) {
                player.getVelocity().setY(0);
                if (collision.getNormal().getY() > 0) {
                    player.setJumping(false);
                }
            }
            if (collision.getNormal().getZ() != 0) {
                player.getVelocity().setZ(0);
            }
        } else {
            player.setPos(player.getPos().add(player.getVelocity()));
        }
    }

    public List<Chunk> getVisibleChunks() {
        Vector3 playerPos = new Vector3(player.getPos().getX(), 0, player.getPos().getZ());
        return chunks.values().stream()
            .filter(chunk -> playerPos.subtract(chunk.getCenter().toVector3()).magnitude() < CHUNK_RENDER_DISTANCE * Chunk.CHUNK_SIZE)
            .toList();
    }

    public List<Collision> checkPlayerCollisions(Player player) {
        Vector3 playerVelocity = player.getVelocity();
        double playerSpeed = Math.abs(playerVelocity.getX()) + Math.abs(playerVelocity.getY()) + Math.abs(playerVelocity.getZ());
        if (playerSpeed < 1e-7) {
            return Collections.emptyList();
        }

        List<Collision> collisions = new LinkedList<>();

        for (Vector3I blockPos : getBlocksNearPlayer(player)) {
            BoxCollider collider = BoxCollider.cube(blockPos.toVector3());
            Collision collision = player.getCollider().dynamicBoxVsStaticBox(player.getVelocity(), collider);

            if (collision != null) {
                collisions.add(collision);
            }
        }

        return collisions;
    }

    public int getHeightAtPos(Vector3 pos) {
        Chunk chunk = getChunk(pos);
        if (chunk == null) {
            return -1;
        }

        Vector3 localPos = worldPosToLocalPos(pos);
        return chunk.getHeightAtPos(new Vector2(localPos.getX(), localPos.getZ()));
    }

    private void generateChunk(Vector3I chunkCoord) {
        System.out.println("Generating chunk at " + chunkCoord);
        Chunk chunk = worldGenerator.generateChunk(chunkCoord);
        chunks.put(chunk.getChunkCoord(), chunk);
    }

    private Set<Vector3I> getBlocksNearPlayer(Player player) {
        Set<Vector3I> blocks = new HashSet<>();
        Vector3I playerPos = new Vector3I(player.getPos());

        for (int x = -2; x <= 2; x++) {
            for (int y = -3; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    blocks.add(playerPos.add(new Vector3I(x, y, z)));
                }
            }
        }

        return blocks.stream()
            .filter(block -> {
                Chunk chunk = getChunk(block);
                return chunk != null && !BlockType.AIR.equals(chunk.getBlock(worldPosToLocalPos(block)));
            })
            .collect(Collectors.toSet());
    }

    // Get the chunk that contains the given world coordinate
    private Chunk getChunk(Vector3I pos) {
        return chunks.get(getChunkCoord(pos));
    }

    private Chunk getChunk(Vector3 pos) {
        return getChunk(new Vector3I(pos));
    }

    // Get the chunk coord of the chunk that contains the given world coordinate
    private Vector3I getChunkCoord(Vector3I pos) {
        int x = Math.floorDiv(pos.getX(), Chunk.CHUNK_SIZE);
        int z = Math.floorDiv(pos.getZ(), Chunk.CHUNK_SIZE);

        return new Vector3I(x, 0, z);
    }

    private Vector3 worldPosToLocalPos(Vector3 worldPos) {
        double x = worldPos.getX() % Chunk.CHUNK_SIZE;
        double z = worldPos.getZ() % Chunk.CHUNK_SIZE;

        return new Vector3(x, worldPos.getY(), z);
    }

    private Vector3 worldPosToLocalPos(Vector3I worldPos) {
        return worldPosToLocalPos(worldPos.toVector3());
    }

    private boolean playerIsFloating(Player player) {
        Vector3 playerPos = player.getPos();

        for (int y = Math.min((int) playerPos.getY(), WORLD_HEIGHT - 1); y > 0; y--) {
            List<Vector3I> blockPositions = List.of(
                new Vector3I((int) playerPos.getX(), y, (int) playerPos.getZ()),
                new Vector3I((int) playerPos.getX(), y, (int) playerPos.getZ() + 1),
                new Vector3I((int) playerPos.getX() + 1, y, (int) playerPos.getZ()),
                new Vector3I((int) playerPos.getX() + 1, y, (int) playerPos.getZ() + 1)
            );

            for (Vector3I blockPos : blockPositions) {
                Vector3 localPos = worldPosToLocalPos(blockPos);
                BlockType block = getChunk(blockPos).getBlock(localPos);
                if (!BlockType.AIR.equals(block)) {
                    return true;
                }
            }
        }

        return false;
    }
}
