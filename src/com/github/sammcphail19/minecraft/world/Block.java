package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.vector.Vector3I;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Block {
    private final Vector3I pos;
    private final BlockType blockType;
}
