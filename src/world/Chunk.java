package world;

import core.Mesh;
import core.Transform;
import core.Vertex;
import graphics.Cube;
import graphics.texture.TextureAtlasUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import math.vector.Vector3;

public class Chunk {
    private static final int CHUNK_SIZE = 16;
    private Vector3 origin;
    private BlockType[] blocks;
    @Getter
    private Mesh mesh;

    public Chunk(Vector3 origin, World world) {
        this.origin = origin;
        this.blocks = new BlockType[CHUNK_SIZE * CHUNK_SIZE * World.WORLD_HEIGHT];

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < World.WORLD_HEIGHT; y++) {
                    putBlock(x, y, z, world.getBlockType(x, y, z));
                }
            }
        }

        updateMesh();
    }

    private void updateMesh() {
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < World.WORLD_HEIGHT; y++) {
                    BlockType block = getBlock(x, y, z);
                    if (block == BlockType.AIR) {
                        continue;
                    }
                    Vector3 blockPos = new Vector3(x, y, z);
                    Transform transform = Transform.builder()
                        .translation(blockPos.add(origin))
                        .build();
                    Cube cube = new Cube(transform, block.getTexture());

                    Arrays.stream(cube.getIndices())
                        .map(i -> i + vertices.size())
                        .forEach(indices::add);
                    cube.getVertices().stream()
                        .map(vertex -> new Vertex(vertex.getPos().add(blockPos), vertex.getTexCoord()))
                        .forEach(vertices::add);
                }
            }
        }
        Transform transform = Transform.builder()
            .translation(origin)
            .build();
        this.mesh = new Mesh(vertices, indices.stream().mapToInt(i -> i).toArray(), transform, TextureAtlasUtils.getTexture());
    }

    private void putBlock(int x, int y, int z, BlockType blockType) {
        this.blocks[to1DIndex(x, y, z)] = blockType;
    }

    private BlockType getBlock(int x, int y, int z) {
        return this.blocks[to1DIndex(x, y, z)];
    }

    private int to1DIndex(int x, int y, int z) {
        return x + (CHUNK_SIZE * y) + (z * CHUNK_SIZE * World.WORLD_HEIGHT);
    }

    private Vector3 to3DIndex(int i) {
        int z = i / (CHUNK_SIZE * World.WORLD_HEIGHT);
        i -= (z * CHUNK_SIZE * World.WORLD_HEIGHT);
        int y = i / CHUNK_SIZE;
        int x = i % CHUNK_SIZE;
        return new Vector3(x, y, z);
    }
}
