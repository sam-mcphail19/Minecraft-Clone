package com.github.sammcphail19.minecraft.graphics.texture;

import com.github.sammcphail19.engine.vector.Vector2;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;

public class TextureAtlas {
    public static final int TEXTURE_SIZE = 16;

    private static int texturesPerRow;
    private static final Map<String, Vector2> textureMap = new HashMap<>();

    @SneakyThrows
    public static void initialize() {
        List<Path> paths = Files.walk(Paths.get("res/texture"))
            .filter(Files::isRegularFile)
            .toList();

        texturesPerRow = (int) Math.sqrt(paths.size()) + 1;
        int width = texturesPerRow * TEXTURE_SIZE;
        int height = texturesPerRow * TEXTURE_SIZE;

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();

        int x = 0;
        int y = 0;
        for (Path path : paths) {
            System.out.println("Adding " + path + " to position: (" + x + ", " + y + ")");
            BufferedImage bufferedImage = ImageIO.read(path.toFile());
            g.drawImage(bufferedImage, x, y, null);

            double atlasX = (double) x / TEXTURE_SIZE / texturesPerRow;
            double atlasY = (double) y / TEXTURE_SIZE / texturesPerRow;
            textureMap.put(pathToTextureName(path), new Vector2(atlasX, atlasY));
            x += TEXTURE_SIZE;
            if (x > result.getWidth() - 1) {
                x = 0;
                y += TEXTURE_SIZE;
            }
        }

        ImageIO.write(result, "png", new File("res/textureAtlas.png"));

        getTexture();
    }

    public static com.github.sammcphail19.engine.core.Texture getTexture() {
        return com.github.sammcphail19.engine.core.Texture.load("res/textureAtlas.png");
    }

    public static Vector2 getTextureAtlasCoords(Texture texture) {
        if (texture == null) {
            return textureMap.get("PLACEHOLDER");
        }

        Vector2 pos = textureMap.get(texture.name());
        return pos == null ? textureMap.get("PLACEHOLDER") : pos;
    }

    public static int getSize() {
        return TEXTURE_SIZE * texturesPerRow;
    }

    private static String pathToTextureName(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.')).toUpperCase();
    }
}
