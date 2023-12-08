package world;

public class World {
    public static final int WORLD_HEIGHT = 128;

    public BlockType getBlockType(int x, int y, int z) {
        if (y > WORLD_HEIGHT / 2.0) {
            return BlockType.AIR;
        }

        if (y == WORLD_HEIGHT / 2.0) {
            return BlockType.DIRT;
        }

        return BlockType.STONE;
    }
}
