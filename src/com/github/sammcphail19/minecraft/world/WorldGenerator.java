package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3I;

import static com.github.sammcphail19.minecraft.world.World.WORLD_HEIGHT;

public class WorldGenerator {

    public Chunk generateChunkData(World world, Vector3I chunkCoord) {
        chunkCoord.setY(0);
        Vector3I chunkOrigin = chunkCoord.multiply(Chunk.CHUNK_SIZE);
        Chunk chunk = new Chunk(chunkOrigin, world);

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                for (int y = 0; y < World.WORLD_HEIGHT; y++) {
                    chunk.putBlock(x, y, z, getBlockType(chunkOrigin.add(x, y, z)));
                }
            }
        }

        return chunk;
    }

    public BlockType getBlockType(Vector3I blockPos) {
        if (blockPos.getY() < 4) {
            return BlockType.BEDROCK;
        }

        double noise = SimplexNoise.noise2(new Vector2(blockPos.getX(), blockPos.getZ()), 0.05, 2, 0.5, 3, 0);
        double height = noise * WORLD_HEIGHT / 1.5;

        if (blockPos.getY() > height) {
            return BlockType.AIR;
        }

        if (blockPos.getY() > height - 1) {
            return BlockType.GRASS;
        }

        if (blockPos.getY() > height - 3) {
            return BlockType.DIRT;
        }

        return BlockType.STONE;
    }
}
