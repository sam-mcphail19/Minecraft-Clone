package com.github.sammcphail19.minecraft.graphics;

import com.github.sammcphail19.engine.core.Mesh;
import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.core.Vertex;

import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import com.github.sammcphail19.minecraft.graphics.texture.TextureAtlas;
import java.util.ArrayList;
import java.util.List;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;

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
        0, 1,
        0, 0,
        1, 0,
        1, 1,

        // Top Face
        0, 1,
        1, 1,
        0, 0,
        1, 0,

        // Right Face
        0, 1,
        0, 0,
        1, 0,
        1, 1,

        // Left Face
        1, 1,
        0, 1,
        0, 0,
        1, 0,

        // Bottom Face
        0, 0,
        0, 1,
        1, 0,
        1, 1,

        // Back Face
        1, 1,
        0, 1,
        1, 0,
        0, 0,
    };

    public Cube(Transform transform, Texture texture) {
        super(createVertices(texture), indices, transform, TextureAtlas.getTexture());
    }

    private static List<Vertex> createVertices(Texture texture) {
        List<Vertex> vertices = new ArrayList<>();

        for (int i = 0; i < VERTEX_COUNT; i++) {
            Vector3 vertexPos = new Vector3(vertexPositions[i * 3], vertexPositions[i * 3 + 1], vertexPositions[i * 3 + 2]);
            Vector2 textureCoords = TextureAtlas.getTextureAtlasCoords(texture);
            double textureSize = (double) TextureAtlas.TEXTURE_SIZE / TextureAtlas.getWidth();
            double uvX = uvs[i * 2] * textureSize + textureCoords.getX();
            double uvY = uvs[i * 2 + 1] * textureSize + textureCoords.getY();
            Vector2 uvCoord = new Vector2(uvX, uvY);
            vertices.add(new Vertex(vertexPos, uvCoord));
        }

        return vertices;
    }
}
