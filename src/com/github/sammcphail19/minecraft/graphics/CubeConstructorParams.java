package com.github.sammcphail19.minecraft.graphics;

import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.vector.Vector3I;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CubeConstructorParams {
    private final Transform transform;
    private final Vector3I selectedBlock;
}
