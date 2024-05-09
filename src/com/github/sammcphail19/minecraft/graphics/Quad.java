package com.github.sammcphail19.minecraft.graphics;

import com.github.sammcphail19.engine.core.Mesh;
import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.core.Vertex;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import com.github.sammcphail19.minecraft.graphics.texture.TextureAtlas;
import com.github.sammcphail19.minecraft.world.Direction;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Quad extends Mesh {
    private static final int VERTEX_COUNT = 4;
    private static final double[] vertexPositions = new double[]{
        // Front Face
        0, 1, 1,
        0, 0, 1,
        1, 0, 1,
        1, 1, 1,

        // Top Face
        0, 1, 0,
        0, 1, 1,
        1, 1, 1,
        1, 1, 0,

        // Right Face
        1, 1, 1,
        1, 0, 1,
        1, 0, 0,
        1, 1, 0,

        // Left Face
        0, 1, 1,
        0, 1, 0,
        0, 0, 0,
        0, 0, 1,

        // Bottom Face
        0, 0, 0,
        0, 0, 1,
        1, 0, 1,
        1, 0, 0,

        // Back Face
        1, 0, 0,
        0, 0, 0,
        0, 1, 0,
        1, 1, 0,
    };

    private static final int[] indices = new int[]{
        0, 1, 3, 3, 1, 2
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
        1, 1,
        0, 1,

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
        1, 1,
        0, 1,
        0, 0,
        1, 0,
    };

    private static final Map<Direction, Integer> vertexPositionIndexMap = ImmutableMap.of(
        Direction.FRONT, 0,
        Direction.TOP, 12,
        Direction.RIGHT, 24,
        Direction.LEFT, 36,
        Direction.BOTTOM, 48,
        Direction.BACK, 60
    );

    private static final Map<Direction, Integer> uvIndexMap = ImmutableMap.of(
        Direction.FRONT, 0,
        Direction.TOP, 8,
        Direction.RIGHT, 16,
        Direction.LEFT, 24,
        Direction.BOTTOM, 32,
        Direction.BACK, 40
    );

    public Quad(List<Vertex> vertices, int[] indices, Transform transform, com.github.sammcphail19.engine.core.Texture texture) {
        super(vertices, indices, transform, texture);
    }

    public static Quad quad(Transform transform, com.github.sammcphail19.engine.core.Texture texture) {
        return new Quad(createVertices(), indices, transform, texture);
    }

    public static Quad blockQuad(CubeConstructorParams params, Direction direction, Texture texture) {
        return new Quad(createVertices(params, direction, texture), indices, params.getTransform(), TextureAtlas.getTexture());
    }

    private static List<Vertex> createVertices() {
        List<Vertex> vertices = new LinkedList<>();

        int startingIndex = vertexPositionIndexMap.get(Direction.FRONT);
        int uvIndex = uvIndexMap.get(Direction.FRONT);

        for (int i = 0; i < VERTEX_COUNT; i++) {
            Vector3 vertexPos = new Vector3(vertexPositions[startingIndex + i * 3], vertexPositions[startingIndex + i * 3 + 1], vertexPositions[startingIndex + i * 3 + 2]);
            double uvX = uvs[uvIndex + i * 2];
            double uvY = uvs[uvIndex + i * 2 + 1];
            Vector2 uvCoord = new Vector2(uvX, uvY);
            vertices.add(new Vertex(vertexPos, Direction.FRONT.getNormal(), uvCoord, 0));
        }

        return vertices;
    }

    private static List<Vertex> createVertices(CubeConstructorParams params, Direction direction, Texture texture) {
        List<Vertex> vertices = new LinkedList<>();

        int startingIndex = vertexPositionIndexMap.get(direction);
        int uvIndex = uvIndexMap.get(direction);

        for (int i = 0; i < VERTEX_COUNT; i++) {
            Vector3 vertexPos = new Vector3(vertexPositions[startingIndex + i * 3], vertexPositions[startingIndex + i * 3 + 1], vertexPositions[startingIndex + i * 3 + 2]);
            Vector2 textureCoords = TextureAtlas.getTextureAtlasCoords(texture);
            double textureSize = (double) TextureAtlas.TEXTURE_SIZE / TextureAtlas.getSize();
            double uvX = uvs[uvIndex + i * 2] * textureSize + textureCoords.getX();
            double uvY = uvs[uvIndex + i * 2 + 1] * textureSize + textureCoords.getY();
            Vector2 uvCoord = new Vector2(uvX, uvY);
            vertices.add(new Vertex(vertexPos, direction.getNormal(), uvCoord, 0));
        }

        return vertices;
    }
}
