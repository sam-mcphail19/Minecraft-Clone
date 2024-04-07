package com.github.sammcphail19.minecraft.graphics;

import com.github.sammcphail19.engine.core.Mesh;
import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.core.Vertex;

import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import com.github.sammcphail19.minecraft.graphics.texture.TextureAtlasUtils;
import java.util.ArrayList;
import java.util.List;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;

public class Cube extends Mesh {
    private static final int VERTEX_COUNT = 24;
    private static final double[] vertexPositions = new double[]{
        // Front Face
        -0.5, 0.5, 0.5, // V0
        -0.5, -0.5, 0.5, // V1
        0.5, -0.5, 0.5, // V2
        0.5, 0.5, 0.5, // V3

        // Top Face
        -0.5, 0.5, -0.5, // V4
        0.5, 0.5, -0.5, // V5
        -0.5, 0.5, 0.5, // V6
        0.5, 0.5, 0.5, // V7

        // Right Face
        0.5, 0.5, 0.5, // V8
        0.5, -0.5, 0.5, // V9
        0.5, -0.5, -0.5, // V10
        0.5, 0.5, -0.5, // V11

        // Left Face
        -0.5, 0.5, 0.5, // V12
        -0.5, 0.5, -0.5, // V13
        -0.5, -0.5, -0.5, // V14
        -0.5, -0.5, 0.5, // V15

        // Bottom Face
        -0.5, -0.5, -0.5, // V16
        -0.5, -0.5, 0.5, // V17
        0.5, -0.5, -0.5, // V18
        0.5, -0.5, 0.5, // V19

        // Back Face
        -0.5, 0.5, -0.5, // V20
        0.5, 0.5, -0.5, // V21
        -0.5, -0.5, -0.5, // V22
        0.5, -0.5, -0.5, // V23
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
        super(createVertices(texture), indices, transform, TextureAtlasUtils.getTexture());
    }

    private static List<Vertex> createVertices(Texture texture) {
        List<Vertex> vertices = new ArrayList<>();

        for (int i = 0; i < VERTEX_COUNT; i++) {
            Vector3 vertexPos = new Vector3(vertexPositions[i * 3], vertexPositions[i * 3 + 1], vertexPositions[i * 3 + 2]);
            Vector2 textureCoords = TextureAtlasUtils.getTextureAtlasCoords(texture);
            double uvX = uvs[i * 2] * TextureAtlasUtils.NORMALIZED_TEXTURE_SIZE + textureCoords.getX();
            double uvY = uvs[i * 2 + 1] * TextureAtlasUtils.NORMALIZED_TEXTURE_SIZE + textureCoords.getY();
            Vector2 uvCoord = new Vector2(uvX, uvY);
            vertices.add(new Vertex(vertexPos, uvCoord));
        }

        return vertices;
    }
}
