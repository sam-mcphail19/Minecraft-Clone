package com.github.sammcphail19.minecraft.graphics.texture;

import com.github.sammcphail19.engine.vector.Vector2;

public class TextureAtlasUtils {
    public static final int TEXTURE_ATLAS_SIZE = 256;
    public static final int TEXTURE_SIZE = 16;
    public static final int TEXTURES_PER_ROW = TEXTURE_ATLAS_SIZE / TEXTURE_SIZE;
    public static final double NORMALIZED_TEXTURE_SIZE = 1d / TEXTURE_SIZE;

    public static com.github.sammcphail19.engine.core.Texture getTexture() {
        return com.github.sammcphail19.engine.core.Texture.load("res/textureAtlas.png");
    }

    public static Vector2 getTextureAtlasCoords(Texture texture) {
        if (texture == null) {
            return new Vector2();
        }
        int x = texture.getId() % TEXTURES_PER_ROW;
        int y = texture.getId() / TEXTURES_PER_ROW;

        return new Vector2(x / (float) TEXTURE_SIZE, y / (float) TEXTURE_SIZE);
    }
}
