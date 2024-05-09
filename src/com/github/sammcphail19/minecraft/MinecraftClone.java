package com.github.sammcphail19.minecraft;

import com.github.sammcphail19.engine.Application;
import com.github.sammcphail19.engine.core.Input;
import com.github.sammcphail19.engine.core.Texture;
import com.github.sammcphail19.engine.core.Transform;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.engine.vector.Vector3I;
import com.github.sammcphail19.minecraft.graphics.Quad;
import com.github.sammcphail19.minecraft.graphics.texture.TextureAtlas;
import com.github.sammcphail19.minecraft.world.Direction;
import com.github.sammcphail19.minecraft.world.World;
import com.github.sammcphail19.minecraft.world.WorldGenerator;
import java.util.Objects;
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
        this.camera = player.getCamera();

        TextureAtlas.initialize();

        this.world = new World(new WorldGenerator(), player, shader, null);
        world.generate();
        world.getChunks().values().stream()
            .filter(chunk -> chunk != null && chunk.getMesh() != null)
            .forEach(chunk -> renderer.submitPerspectiveMesh(chunk.getMesh()));

        Vector3I currentChunksChunkCoord = new Vector3I();
        while (world.getChunks().get(currentChunksChunkCoord) == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted");
                System.exit(1);
            }
        }
        int height = world.getHeightAtPos(player.getPos());
        Vector3 newPos = new Vector3(player.getPos().getX(), height + 1, player.getPos().getZ());
        player.setPos(newPos);
        System.out.println("Player pos:" + newPos);
    }

    @Override
    protected void updateApplication() {
        // TODO: need to also handle cases where game is running too slow
        // can run multiple ticks or start skipping ticks

        long currentTime = System.currentTimeMillis();
        long timeBetweenTicks = currentTime - lastTickTime;

        if (timeBetweenTicks > MS_BETWEEN_TICKS) {
            lastTickTime = currentTime;

            clearMeshes();
            world.update();
            world.getVisibleChunks().stream()
                .filter(chunk -> chunk != null && chunk.getMesh() != null)
                .forEach(chunk -> renderer.submitPerspectiveMesh(chunk.getMesh()));
            Transform crosshairTransform = Transform.builder()
                .translation(new Vector3(-0.5, -0.5, 0))
                .scale(new Vector3(9d/16d, 1, 1).multiply(0.05))
                .build();
            Quad crosshair = Quad.quad(crosshairTransform, Texture.load("res/texture/crosshair.png"));
            renderer.submitOrthoMesh(crosshair);

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
