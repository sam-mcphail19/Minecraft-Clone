package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.minecraft.graphics.Cube;
import com.github.sammcphail19.minecraft.graphics.CubeConstructorParams;
import com.github.sammcphail19.minecraft.graphics.texture.Texture;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlockType {
    AIR(0, null),
    STONE(1, (params) -> Cube.singleTextureCube(params, Texture.STONE)),
    DIRT(2, (params) -> Cube.singleTextureCube(params, Texture.DIRT)),
    GRASS(3, (params) -> Cube.multiTextureCube(params, Texture.GRASS_SIDE, Texture.GRASS_SIDE, Texture.GRASS_SIDE, Texture.GRASS_SIDE, Texture.GRASS, Texture.DIRT)),
    BEDROCK(4, (params) -> Cube.singleTextureCube(params, Texture.BEDROCK));

    private final int id;
    private final Function<CubeConstructorParams, Cube> cubeConstructor;

    private final static Map<Integer, BlockType> blockTypeMap = Arrays.stream(values())
        .collect(Collectors.toMap(BlockType::getId, Function.identity()));

    public static BlockType getBlockType(int id) {
        return blockTypeMap.get(id);
    }
}
