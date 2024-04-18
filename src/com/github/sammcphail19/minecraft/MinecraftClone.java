package com.github.sammcphail19.minecraft;

import com.github.sammcphail19.engine.Application;
import com.github.sammcphail19.engine.core.Input;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.world.Chunk;
import com.github.sammcphail19.minecraft.world.World;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

public class MinecraftClone extends Application {
    private final static int TPS = 60;
    private final static double MS_BETWEEN_TICKS = 1000d / TPS;
    private final World world;
    private final Player player;

    private long lastTickTime = 0;
    private long lastTpsLogTime = 0;
    private int ticks = 0;
    @Getter
    private static boolean isDebugMode = false;

    public MinecraftClone() {
        super("Minecraft clone");

        this.player = new Player(new Vector3(8, 65, 8));

        setCamera(player.getCamera());

        this.world = new World(player);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Chunk chunk = world.generateChunk(new Vector3I(x * Chunk.CHUNK_SIZE, 0, z * Chunk.CHUNK_SIZE));
                submitMesh(chunk.getMesh());
            }
        }
    }

    @Override
    protected void updateApplication() {
        // TODO: need to also handle cases where game is running too slow
        // can run multiple ticks or start skipping ticks

        long currentTime = System.currentTimeMillis();
        long timeBetweenTicks = currentTime - lastTickTime;

        if (timeBetweenTicks > MS_BETWEEN_TICKS) {
            lastTickTime = currentTime;
            world.update();
            ticks++;
        }

        if (currentTime - lastTpsLogTime > 1000) {
            System.out.println("TPS: " + ticks);
            lastTpsLogTime = currentTime;
            ticks = 0;
        }

        if (Input.isKeyReleased(GLFW.GLFW_KEY_F3)) {
            isDebugMode = !isDebugMode;
        }
    }
}
