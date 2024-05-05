package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlockType {
    AIR(0, null),
    STONE(1, Texture.STONE),
    DIRT(2, Texture.DIRT),
    GRASS(3, Texture.GRASS),
    BEDROCK(4, Texture.BEDROCK);

    private final int id;
    private final Texture texture;
}
