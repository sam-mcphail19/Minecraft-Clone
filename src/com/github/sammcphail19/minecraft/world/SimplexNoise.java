package com.github.sammcphail19.minecraft.world;

import com.github.sammcphail19.engine.vector.Vector2;

public class SimplexNoise {

    // Returns a value between 0 and 1
    public static double noise2(Vector2 position, double scale, double lacunarity, double persistence, int octaves, long seed) {
        scale = Math.max(scale, 0.00001);

        double sum = 0f;

        for (int i = 0; i < octaves; i++) {
            double frequency = Math.pow(lacunarity, i);
            double amplitude = Math.pow(persistence, octaves - i);

            double noise = OpenSimplex2S.noise2(
                seed,
                position.getX() * scale / frequency,
                position.getY() * scale / frequency
            );

            sum += noise * amplitude;
        }

        return (sum + 1) / 2;
    }
}
