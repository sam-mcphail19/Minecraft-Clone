package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.core.Mesh;
import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.core.Vertex;

import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.graphics.Cube;
import com.github.sammcphail19.minecraft.graphics.texture.TextureAtlasUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.github.sammcphail19.engine.vector.Vector3;

@RequiredArgsConstructor
public class Chunk {
    public static final int CHUNK_SIZE = 16;
    private final Vector3I origin;
    private final BlockType[] blocks = new BlockType[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * World.WORLD_HEIGHT];
    @Getter
    private Mesh mesh;

    public void updateMesh() {
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < World.WORLD_HEIGHT; y++) {
                    BlockType block = getBlock(x, y, z);
                    // should be based on if face is visible, not block
                    // this likely requires adding normals to the vertices, then we can check what the block is in that direction
                    if (block == BlockType.AIR || !blockIsVisible(x, y, z)) {
                        continue;
                    }
                    Vector3 blockPos = new Vector3(x, y, z);
                    Transform transform = Transform.builder()
                        .translation(blockPos.add(origin.toVector3()))
                        .build();
                    Cube cube = new Cube(transform, block.getTexture());

                    for (int index : cube.getIndices()) {
                        indices.add(index + vertices.size());
                    }
                    for (Vertex vertex : cube.getVertices()) {
                        vertices.add(new Vertex(vertex.getPos().add(blockPos), vertex.getTexCoord()));
                    }
                }
            }
        }
        Transform transform = Transform.builder()
            .translation(origin.toVector3())
            .build();
        this.mesh = new Mesh(vertices, indices.stream().mapToInt(i -> i).toArray(), transform, TextureAtlasUtils.getTexture());
    }

    public void putBlock(int x, int y, int z, BlockType blockType) {
        this.blocks[to1DIndex(x, y, z)] = blockType;
    }

    public BlockType getBlock(int x, int y, int z) {
        try {
            return this.blocks[to1DIndex(x, y, z)];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ArrayIndexOutOfBoundsException for " + "("+ x + "," + y + ","+ z + ")");
            throw e;
        }
    }

    public BlockType getBlock(Vector3 pos) {
        int x = (int) pos.getX();
        int y = (int) pos.getY();
        int z = (int) pos.getZ();
        return getBlock(x, y, z);
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

    private boolean blockIsVisible(int x, int y, int z) {
        return blockIsOnChunkBorder(x, z) || getBlockNeighbours(x, y, z).contains(BlockType.AIR);
    }

    private boolean blockIsOnChunkBorder(int x, int z) {
        return x == 0 || x == CHUNK_SIZE - 1 || z == 0 || z == CHUNK_SIZE - 1;
    }

    private Set<BlockType> getBlockNeighbours(int x, int y, int z) {
        Set<BlockType> neighbours = new HashSet<>();
        if (x >= 0) {
            neighbours.add(getBlock(x - 1, y, z));
        }
        if (x < CHUNK_SIZE) {
            neighbours.add(getBlock(x + 1, y, z));
        }
        if (z >= 0) {
            neighbours.add(getBlock(x, y, z - 1));
        }
        if (z < CHUNK_SIZE) {
            neighbours.add(getBlock(x, y, z + 1));
        }
        if (y >= 0) {
            neighbours.add(getBlock(x, y - 1, z));
        }
        if (y < World.WORLD_HEIGHT) {
            neighbours.add(getBlock(x, y + 1, z));
        }

        return neighbours;
    }
}
