package com.github.sammcphail19.minecraft.graphics;

import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import com.github.sammcphail19.minecraft.world.Direction;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Cube {
    private final Map<Direction, Quad> faces;

    public static Cube multiTextureCube(
        CubeConstructorParams params,
        Texture frontTexture,
        Texture backTexture,
        Texture leftTexture,
        Texture rightTexture,
        Texture topTexture,
        Texture bottomTexture
    ) {
        Map<Direction, Quad> faces = Map.of(
            Direction.FRONT, Quad.blockQuad(params, Direction.FRONT, frontTexture),
            Direction.BACK, Quad.blockQuad(params, Direction.BACK, backTexture),
            Direction.LEFT, Quad.blockQuad(params, Direction.LEFT, leftTexture),
            Direction.RIGHT, Quad.blockQuad(params, Direction.RIGHT, rightTexture),
            Direction.TOP, Quad.blockQuad(params, Direction.TOP, topTexture),
            Direction.BOTTOM, Quad.blockQuad(params, Direction.BOTTOM, bottomTexture)
        );

        return new Cube(faces);
    }

    public static Cube singleTextureCube(CubeConstructorParams params, Texture texture) {
        return multiTextureCube(params, texture, texture, texture, texture, texture, texture);
    }
}
