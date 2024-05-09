package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.vector.Vector3;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Direction {
    FRONT(new Vector3(0, 0, 1)),
    TOP(new Vector3(0, 1, 0)),
    RIGHT(new Vector3(1, 0, 0)),
    LEFT(new Vector3(-1, 0, 0)),
    BOTTOM(new Vector3(0, -1, 0)),
    BACK(new Vector3(0, 0, -1));

    private final Vector3 normal;
}