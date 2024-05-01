package com.github.sammcphail19.minecraft;

import com.github.sammcphail19.engine.Application;
import com.github.sammcphail19.engine.core.Input;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.world.Chunk;
import com.github.sammcphail19.minecraft.world.World;
import com.github.sammcphail19.minecraft.world.WorldGenerator;
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

        this.world = new World(new WorldGenerator(), player);
        world.generate();
        world.getChunks().values().forEach(chunk -> submitMesh(chunk.getMesh()));

        int height = world.getHeightAtPos(player.getPos());
        Vector3 newPos = new Vector3(player.getPos().getX(), height + 1, player.getPos().getZ());
        player.setPos(newPos);

        Chunk chunk = world.getChunks().get(new Vector3I());
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
