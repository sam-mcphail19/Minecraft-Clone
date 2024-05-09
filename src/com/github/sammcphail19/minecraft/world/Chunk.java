package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.core.Mesh;
import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.core.Vertex;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.graphics.Cube;
import com.github.sammcphail19.minecraft.graphics.CubeConstructorParams;
import com.github.sammcphail19.minecraft.graphics.Quad;
import com.github.sammcphail19.minecraft.graphics.texture.TextureAtlas;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Chunk {
    public static final int CHUNK_SIZE = 16;
    private static final Vector3I CHUNK_HALF = new Vector3I(CHUNK_SIZE / 2, 0, CHUNK_SIZE / 2);

    private final Vector3I origin;
    private final World world;
    private final int[] blocks = new int[CHUNK_SIZE * CHUNK_SIZE * World.WORLD_HEIGHT];

    private Mesh mesh;
    private Map<Vector3I, List<Integer>> blockToVerticesMap = new HashMap<>();

    public void updateMesh() {
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        blockToVerticesMap = new HashMap<>();

        for (int i = 0; i < blocks.length; i++) {
            Vector3I blockPos = to3DIndex(i);
            BlockType block = getBlock(blockPos);

            if (block == BlockType.AIR) {
                continue;
            }

            Transform transform = Transform.builder()
                .translation(blockPos.add(origin).toVector3())
                .build();
            CubeConstructorParams params = new CubeConstructorParams(transform, world.getSelectedBlock());
            Cube cube = block.getCubeConstructor().apply(params);

            for (Map.Entry<Direction, Quad> face : cube.getFaces().entrySet()) {
                if (!faceIsVisible(blockPos, face.getKey())) {
                    continue;
                }

                for (int index : face.getValue().getIndices()) {
                    indices.add(index + vertices.size());
                }
                for (Vertex vertex : face.getValue().getVertices()) {
                    Vector3 vertexPos = vertex.getPos().add(blockPos);
                    Vertex newVertex = new Vertex(vertexPos, vertex.getNormal(), vertex.getTexCoord(), 0);
                    addToBlockToVerticesMap(blockPos, vertices.size());
                    vertices.add(newVertex);
                }
            }
        }

        Transform transform = Transform.builder()
            .translation(origin.toVector3())
            .build();
        this.mesh = new Mesh(vertices, indices.stream().mapToInt(i -> i).toArray(), transform, TextureAtlas.getTexture());
    }

    public void highlightSelectedBlock() {
        Vector3 selectedBlockPos = world.worldPosToLocalPos(world.getSelectedBlock());

        List<Integer> indices = blockToVerticesMap.get(new Vector3I(selectedBlockPos));

        if (indices == null) {
            return;
        }

        indices.forEach(i -> mesh.getVertices().get(i).setFlags(1));
        mesh.updateIntVbo();
    }

    public void unhighlightSelectedBlock(Vector3I blockPos) {
        List<Integer> indices = blockToVerticesMap.get(blockPos);
        if (indices == null) {
            return;
        }

        indices.forEach(i -> mesh.getVertices().get(i).setFlags(0));
        mesh.updateIntVbo();
    }

    public Vector3I getChunkCoord() {
        return new Vector3I(origin.getX() / CHUNK_SIZE, 0, origin.getZ() / CHUNK_SIZE);
    }

    public void putBlock(int x, int y, int z, int blockType) {
        this.blocks[to1DIndex(x, y, z)] = blockType;
    }

    public void putBlock(int i, int blockType) {
        this.blocks[i] = blockType;
    }

    public BlockType getBlock(Vector3I blockPos) {
        if (blockPos.getY() < 0 || blockPos.getY() > World.WORLD_HEIGHT - 1) {
            return BlockType.AIR;
        }
        try {
            return BlockType.getBlockType(this.blocks[to1DIndex(blockPos.getX(), blockPos.getY(), blockPos.getZ())]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ArrayIndexOutOfBoundsException for " + "(" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ() + ")");
            throw e;
        }
    }

    public BlockType getBlock(Vector3 blockPos) {
        return getBlock(new Vector3I(blockPos));
    }

    public int getHeightAtPos(Vector2 pos) {
        int x = (int) pos.getX();
        int z = (int) pos.getY();

        for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
            if (getBlock(new Vector3I(x, y, z)) != BlockType.AIR) {
                return y;
            }
        }

        return -1;
    }

    public Vector3I getCenter() {
        return origin.add(CHUNK_HALF);
    }

    public static int to1DIndex(int x, int y, int z) {
        return x + (CHUNK_SIZE * y) + (z * CHUNK_SIZE * World.WORLD_HEIGHT);
    }

    public static Vector3I to3DIndex(int i) {
        int z = i / (CHUNK_SIZE * World.WORLD_HEIGHT);
        i -= (z * CHUNK_SIZE * World.WORLD_HEIGHT);
        int y = i / CHUNK_SIZE;
        int x = i % CHUNK_SIZE;
        return new Vector3I(x, y, z);
    }

    private void addToBlockToVerticesMap(Vector3I blockPos, int i) {
        if (blockToVerticesMap.containsKey(blockPos)) {
            blockToVerticesMap.get(blockPos).add(i);
        } else {
            blockToVerticesMap.put(blockPos, new LinkedList<>(List.of(i)));
        }
    }

    private boolean faceIsVisible(Vector3I pos, Direction direction) {
        Vector3I neighbourPos = pos.add(new Vector3I(direction.getNormal()));
        boolean neighbourIsInThisChunk = neighbourPos.getX() >= 0 &&
            neighbourPos.getX() < CHUNK_SIZE &&
            neighbourPos.getZ() >= 0 &&
            neighbourPos.getZ() < CHUNK_SIZE &&
            neighbourPos.getY() >= 0 &&
            neighbourPos.getY() < World.WORLD_HEIGHT;

        BlockType neighbour = neighbourIsInThisChunk ? getBlock(neighbourPos) : world.getBlock(neighbourPos.add(origin));
        return neighbour == null || BlockType.AIR.equals(neighbour);
    }
}
