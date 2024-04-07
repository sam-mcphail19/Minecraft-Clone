package com.github.sammcphail19.minecraft;

import com.github.sammcphail19.engine.Application;

public class Main {

    public static void main(String[] args) {
        Application application = new MinecraftClone();

        while (!application.shouldClose()) {
            application.update();
        }

        application.exit();
    }
}
