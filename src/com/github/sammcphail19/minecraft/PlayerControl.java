package com.github.sammcphail19.minecraft;

import lombok.Builder;
import lombok.Data;
import com.github.sammcphail19.engine.vector.Vector3;

@Data
@Builder
public class PlayerControl {
    private final double pitch;
    private final double yaw;
    private final Vector3 movement;
}
