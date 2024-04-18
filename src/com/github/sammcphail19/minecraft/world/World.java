package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.physics.BoxCollider;
import com.github.sammcphail19.engine.physics.Collision;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.Player;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class World {
    public static final int WORLD_HEIGHT = 128;

    private final Map<Vector3I, Chunk> chunks = new HashMap<>();

    private Player player;

    public void update() {
        player.update();

        if (getBlockPosBelowPlayer(player) == null) {
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

    public BlockType getBlockType(Vector3 blockPos) {
        if (blockPos.getX() == 12) {
            return BlockType.DIRT;
        }
        if (blockPos.getY() > WORLD_HEIGHT / 2.0) {
            return BlockType.AIR;
        }

        if (blockPos.getY() == WORLD_HEIGHT / 2.0) {
            return BlockType.DIRT;
        }

        return BlockType.STONE;
    }

    public Chunk generateChunk(Vector3I origin) {
        origin.setY(0);
        Chunk chunk = new Chunk(origin);

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                for (int y = 0; y < World.WORLD_HEIGHT; y++) {
                    chunk.putBlock(x, y, z, getBlockType(new Vector3(x, y, z)));
                }
            }
        }

        chunk.updateMesh();
        chunks.put(getChunkOrigin(origin), chunk);

        return chunk;
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

            if (!Objects.isNull(collision)) {
                System.out.println("PlayerPos: " + player.getPos() + ", PlayerV: " + player.getVelocity() + ", BlockPos: " + blockPos + ", " + collision);
                collisions.add(collision);
            }
        }

        return collisions;
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
        return chunks.get(getChunkOrigin(pos));
    }

    private Chunk getChunk(Vector3 pos) {
        return getChunk(new Vector3I(pos));
    }

    // Get the origin of the chunk that contains the given world coordinate
    private Vector3I getChunkOrigin(Vector3I pos) {
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

    private Vector3I getBlockPosBelowPlayer(Player player) {
        Chunk chunk = getChunk(player.getPos());
        if (Objects.isNull(chunk)) {
            return null;
        }

        for (int y = (int) player.getPos().getY(); y > 0; y--) {
            Vector3 blockPos = worldPosToLocalPos(new Vector3(player.getPos().getX(), y, player.getPos().getZ()));
            BlockType block = chunk.getBlock(blockPos);
            if (!BlockType.AIR.equals(block)) {
                return new Vector3I((int) player.getPos().getX(), y, (int) player.getPos().getZ());
            }
        }

        return null;
    }
}
