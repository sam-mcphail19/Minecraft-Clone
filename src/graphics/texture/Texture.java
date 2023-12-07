package graphics.texture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Texture {
    STONE(1),
    DIRT(2);

    @Getter
    private final int id;
}
