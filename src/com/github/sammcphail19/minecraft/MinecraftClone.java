package com.github.sammcphail19.minecraft;

import com.github.sammcphail19.engine.Application;
import com.github.sammcphail19.engine.vector.Vector3;
import com.github.sammcphail19.minecraft.world.Chunk;
import com.github.sammcphail19.minecraft.world.World;

public class MinecraftClone extends Application {
    private final World world;
    private final Player player;

    public MinecraftClone() {
        super("Minecraft clone");

        this.world = new World();
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Chunk chunk = world.generateChunk(new Vector3(x * Chunk.CHUNK_SIZE, 0, z * Chunk.CHUNK_SIZE));
                submitMesh(chunk.getMesh());
            }
        }

        this.player = new Player();
        player.setPos(new Vector3(8, 66, 8));

        setCamera(player.getCamera());
    }

    @Override
    protected void updateApplication() {
        player.update();
        world.update();
    }
}
