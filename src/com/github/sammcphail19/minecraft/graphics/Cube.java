package com.github.sammcphail19.minecraft.graphics;

import com.github.sammcphail19.engine.core.Mesh;
import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.core.Vertex;

import com.github.sammcphail19.engine.util.ListUtil;
import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import com.github.sammcphail19.minecraft.graphics.texture.TextureAtlas;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Cube extends Mesh {
    private static final int VERTEX_COUNT = 24;
    private static final double[] vertexPositions = new double[]{
        // Front Face
        0, 1, 1, // V0
        0, 0, 1, // V1
        1, 0, 1, // V2
        1, 1, 1, // V3

        // Top Face
        0, 1, 0, // V4
        1, 1, 0, // V5
        0, 1, 1, // V6
        1, 1, 1, // V7

        // Right Face
        1, 1, 1, // V8
        1, 0, 1, // V9
        1, 0, 0, // V10
        1, 1, 0, // V11

        // Left Face
        0, 1, 1, // V12
        0, 1, 0, // V13
        0, 0, 0, // V14
        0, 0, 1, // V15

        // Bottom Face
        0, 0, 0, // V16
        0, 0, 1, // V17
        1, 0, 0, // V18
        1, 0, 1, // V19

        // Back Face
        0, 1, 0, // V20
        1, 1, 0, // V21
        0, 0, 0, // V22
        1, 0, 0, // V23
    };

    private static final int[] indices = new int[]{
        0, 1, 3, 3, 1, 2,  // Front Face
        4, 6, 5, 5, 6, 7,  // Top Face
        8, 9, 11, 11, 9, 10, // Right Face
        13, 14, 12, 12, 14, 15, // Left Face
        17, 16, 19, 19, 16, 18, // Bottom Face
        21, 23, 20, 20, 23, 22  // Back Face
    };

    private static final double[] uvs = new double[]{
        // Front Face
        0, 0,
        0, 1,
        1, 1,
        1, 0,

        // Top Face
        0, 0,
        1, 0,
        0, 1,
        1, 1,

        // Right Face
        0, 0,
        0, 1,
        1, 1,
        1, 0,

        // Left Face
        1, 0,
        0, 0,
        0, 1,
        1, 1,

        // Bottom Face
        0, 1,
        0, 0,
        1, 1,
        1, 0,

        // Back Face
        1, 0,
        0, 0,
        1, 1,
        0, 1,
    };

    public Cube(Transform transform, Texture texture) {
        super(createCubeVertices(texture), indices, transform, TextureAtlas.getTexture());
    }

    public Cube(
        Transform transform,
        Texture frontTexture,
        Texture backTexture,
        Texture leftTexture,
        Texture rightTexture,
        Texture topTexture,
        Texture bottomTexture
    ) {
        super(
            ListUtil.flattenLists(
                createQuadVertices(frontTexture, Direction.FRONT),
                createQuadVertices(topTexture, Direction.TOP),
                createQuadVertices(rightTexture, Direction.RIGHT),
                createQuadVertices(leftTexture, Direction.LEFT),
                createQuadVertices(bottomTexture, Direction.BOTTOM),
                createQuadVertices(backTexture, Direction.BACK)
            ),
            ListUtil.flattenLists(
                createQuadIndices(Direction.FRONT),
                createQuadIndices(Direction.TOP),
                createQuadIndices(Direction.RIGHT),
                createQuadIndices(Direction.LEFT),
                createQuadIndices(Direction.BOTTOM),
                createQuadIndices(Direction.BACK)
            ).stream().mapToInt(i -> i).toArray(),
            transform,
            TextureAtlas.getTexture()
        );
    }

    private static List<Vertex> createQuadVertices(Texture texture, Direction direction) {
        List<Vertex> vertices = new ArrayList<>();

        int startingVertex = direction.getStartingVertex();
        for (int i = startingVertex; i < startingVertex + 4; i++) {
            addVertex(vertices, texture, i);
        }

        return vertices;
    }

    private static List<Vertex> createCubeVertices(Texture texture) {
        List<Vertex> vertices = new ArrayList<>();

        for (int i = 0; i < VERTEX_COUNT; i++) {
            addVertex(vertices, texture, i);
        }

        return vertices;
    }

    private static List<Integer> createQuadIndices(Direction direction) {
        return Arrays.stream(Arrays.copyOfRange(indices, direction.getStartingIndex(), direction.getStartingIndex() + 6))
            .boxed()
            .toList();
    }

    private static void addVertex(List<Vertex> vertices, Texture texture, int i) {
        Vector3 vertexPos = new Vector3(vertexPositions[i * 3], vertexPositions[i * 3 + 1], vertexPositions[i * 3 + 2]);
        Vector2 textureCoords = TextureAtlas.getTextureAtlasCoords(texture);
        double textureSize = (double) TextureAtlas.TEXTURE_SIZE / TextureAtlas.getSize();
        double uvX = uvs[i * 2] * textureSize + textureCoords.getX();
        double uvY = uvs[i * 2 + 1] * textureSize + textureCoords.getY();
        Vector2 uvCoord = new Vector2(uvX, uvY);
        vertices.add(new Vertex(vertexPos, uvCoord));
    }

    @Getter
    @RequiredArgsConstructor
    private enum Direction {
        FRONT(0, 0),
        TOP(4, 6),
        RIGHT(8, 12),
        LEFT(12, 18),
        BOTTOM(16, 24),
        BACK(20, 30);

        private final int startingVertex;
        private final int startingIndex;
    }
}
