package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.vector.Vector3I;
import java.util.HashMap;
import java.util.Map;

public class WorldGenerator {
    private static final int BLOCKS_PER_CHUNK = Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * World.WORLD_HEIGHT;
    private final Map<Integer, Integer> heightMap = new HashMap<>();

    public Chunk generateChunkData(World world, Vector3I chunkCoord) {
        chunkCoord.setY(0);
        Vector3I chunkOrigin = chunkCoord.multiply(Chunk.CHUNK_SIZE);
        Chunk chunk = new Chunk(chunkOrigin, world);

        for (int i = 0; i < BLOCKS_PER_CHUNK; i++) {
            chunk.putBlock(i, getBlockType(Chunk.to3DIndex(i).add(chunk.getOrigin())));
        }

        return chunk;
    }

    private int getBlockType(Vector3I blockPos) {
        int y = blockPos.getY();
        if (y < 4) {
            return BlockType.BEDROCK.getId();
        }

        int height = getHeight(blockPos.getX(), blockPos.getZ());

        if (y > height) {
            return BlockType.AIR.getId();
        }

        if (y == height) {
            return BlockType.GRASS.getId();
        }

        if (y > height - 3) {
            return BlockType.DIRT.getId();
        }

        return BlockType.STONE.getId();
    }

    private int getHeight(int x, int y) {
        Integer key = getHeightMapHash(x, y);
        if (heightMap.containsKey(key)) {
            return heightMap.get(key);
        }

        double noise = SimplexNoise.noise2(x, y, 0.05, 2, 0.5, 3, 0);

        int height = (int) (noise * World.WORLD_HEIGHT / 1.5);
        heightMap.put(key, height);
        return height;
    }

    private Integer getHeightMapHash(int x, int y) {
        return BLOCKS_PER_CHUNK * x + y;
    }
}
