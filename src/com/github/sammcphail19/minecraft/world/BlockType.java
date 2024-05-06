package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.minecraft.graphics.Cube;
import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlockType {
    AIR(0, null),
    STONE(1, (transform) -> new Cube(transform, Texture.STONE)),
    DIRT(2, (transform) -> new Cube(transform, Texture.DIRT)),
    GRASS(3, (transform) -> new Cube(transform, Texture.GRASS_SIDE, Texture.GRASS_SIDE, Texture.GRASS_SIDE, Texture.GRASS_SIDE, Texture.GRASS, Texture.DIRT)),
    BEDROCK(4, (transform) -> new Cube(transform, Texture.BEDROCK));

    private final int id;
    private final Function<Transform, Cube> cubeConstructor;
}
