package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.minecraft.Player;

public class World {
    public static final int WORLD_HEIGHT = 128;

    public void update() {

    }

    public BlockType getBlockType(int x, int y, int z) {
        if (y > WORLD_HEIGHT / 2.0) {
            return BlockType.AIR;
        }

        if (y == WORLD_HEIGHT / 2.0) {
            return BlockType.DIRT;
        }

        return BlockType.STONE;
    }

    public Chunk generateChunk(Vector3 origin) {
        Chunk chunk = new Chunk(origin);

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                for (int y = 0; y < World.WORLD_HEIGHT; y++) {
                    chunk.putBlock(x, y, z, getBlockType(x, y, z));
                }
            }
        }

        chunk.updateMesh();
        return chunk;
    }

    public boolean playerIsColliding(Player player) {
        return false;
    }
}
