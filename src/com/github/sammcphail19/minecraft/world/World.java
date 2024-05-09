package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.core.Input;
import com.github.sammcphail19.engine.physics.BoxCollider;
import com.github.sammcphail19.engine.physics.Collision;
import com.github.sammcphail19.engine.shader.Shader;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.Player;
import com.google.common.collect.ImmutableSet;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFW;

@AllArgsConstructor
public class World {
    public static final int WORLD_HEIGHT = 128;
    private static final int CHUNK_RENDER_DISTANCE = 4;

    @Getter
    private final Map<Vector3I, Chunk> chunks = new HashMap<>();

    private WorldGenerator worldGenerator;
    private Player player;
    private Shader shader;
    @Getter
    private Vector3I selectedBlock;

    public void generate() {
        generateChunk(new Vector3I(0, 0, 0));
        generateChunk(new Vector3I(1, 0, 0));
    }

    public void update() {
        player.update();

        Vector3I currentChunkChunkCoord = getChunkCoord(new Vector3I(player.getPos()));
        for (int x = -CHUNK_RENDER_DISTANCE; x < CHUNK_RENDER_DISTANCE; x++) {
            for (int z = -CHUNK_RENDER_DISTANCE; z < CHUNK_RENDER_DISTANCE; z++) {
                Vector3I pos = currentChunkChunkCoord.add(new Vector3I(x, 0, z));
                if (!chunks.containsKey(pos)) {
                    generateChunk(pos);
                }
            }
        }
        Block blockLookingAt = getBlockLookingAt(player);
        Vector3I oldSelectedBlock = selectedBlock;
        selectedBlock = blockLookingAt == null ? null : blockLookingAt.getPos();
        if (oldSelectedBlock != null) {
            getChunk(oldSelectedBlock).unhighlightSelectedBlock(new Vector3I(worldPosToLocalPos(oldSelectedBlock)));
        }
        if (selectedBlock != null) {
            getChunk(selectedBlock).highlightSelectedBlock();
        }

        if (!player.affectedByCollision()) {
            return;
        }

        if (!player.isInCreativeMode() && !player.isJumping() && playerIsFloating(player)) {
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
            .filter(Objects::nonNull)
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

    public BlockType getBlock(Vector3I blockPos) {
        Chunk chunk = getChunk(blockPos);
        if (chunk == null) {
            return null;
        }

        return chunk.getBlock(worldPosToLocalPos(blockPos));
    }

    private void generateChunk(Vector3I chunkCoord) {
        Chunk chunk = worldGenerator.generateChunkData(this, chunkCoord);
        chunks.put(chunkCoord, chunk);
        new ChunkGenerator(chunk).start();
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

    public Vector3 worldPosToLocalPos(Vector3 worldPos) {
        double x = worldPos.getX();
        double z = worldPos.getZ();
        while (x < 0) {
            x += Chunk.CHUNK_SIZE;
        }
        while (z < 0) {
            z += Chunk.CHUNK_SIZE;
        }

        x %= Chunk.CHUNK_SIZE;
        z %= Chunk.CHUNK_SIZE;

        return new Vector3(x, worldPos.getY(), z);
    }

    public Vector3 worldPosToLocalPos(Vector3I worldPos) {
        return worldPosToLocalPos(worldPos.toVector3());
    }

    private boolean playerIsFloating(Player player) {
        Set<Vector3> positions = ImmutableSet.of(
            player.getPos(),
            player.getPos().add(new Vector3(player.getSize().getX(), 0, 0)),
            player.getPos().add(new Vector3(0, 0, player.getSize().getZ())),
            player.getPos().add(new Vector3(player.getSize().getX(), 0, player.getSize().getZ()))
        );
        return positions.stream()
            .map(pos -> raycast(player.getPos(), new Vector3(0, -1, 0), 1, 2))
            .filter(Objects::nonNull)
            .anyMatch(block -> isSolid(block.getBlockType()));
    }

    private Block getBlockLookingAt(Player player) {
        return raycast(player.getCamera().getPos(), player.getCamera().getViewDirection(), 6, 20);
    }

    // Return the of the first block that intersects with the ray
    private Block raycast(Vector3 startPosition, Vector3 rayDirection, int maxDistance, int iterationsPerBlock) {
        Vector3 ray = rayDirection.normalize().multiply(1d / iterationsPerBlock);
        Vector3 position = new Vector3(startPosition);

        Set<Vector3I> alreadyCheckedBlocks = new HashSet<>();

        do {
            int x = (int) position.getX();
            int y = (int) position.getY();
            int z = (int) position.getZ();
            Vector3I blockPos = new Vector3I(x, y, z);

            if (alreadyCheckedBlocks.contains(blockPos)) {
                position = position.add(ray);
                continue;
            }

            alreadyCheckedBlocks.add(blockPos);

            BlockType blockType = getBlock(blockPos);
            if (isSolid(blockType)) {
                return Block.builder()
                    .pos(blockPos)
                    .blockType(blockType)
                    .build();
            }

            position = position.add(ray);

            if (position.getY() < 0) {
                break;
            }
        } while (position.subtract(startPosition).magnitude() < maxDistance);

        return null;
    }

    private boolean isSolid(BlockType blockType) {
        return blockType != null && !BlockType.AIR.equals(blockType);
    }

    @RequiredArgsConstructor
    private class ChunkGenerator extends Thread {
        private final Chunk chunk;

        @Override
        public void run() {
            System.out.println("Generating chunk at " + chunk.getChunkCoord());
            chunk.updateMesh();
        }
    }
}
