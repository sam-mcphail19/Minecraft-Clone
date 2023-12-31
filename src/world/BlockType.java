package world;

import graphics.texture.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlockType {
    AIR(0, null),
    STONE(1, Texture.STONE),
    DIRT(2, Texture.DIRT);

    private final int id;
    private final Texture texture;
}
