package com.github.sammcphail19.minecraft.world;

public class SimplexNoise {

    // Returns a value between 0 and 1
    public static double noise2(int x, int y, double scale, double lacunarity, double persistence, int octaves, long seed) {
        double sum = 0f;

        for (int i = 0; i < octaves; i++) {
            double frequency = Math.pow(lacunarity, i);
            double amplitude = Math.pow(persistence, octaves - i);

            double noise = OpenSimplex2S.noise2(
                seed,
                x * scale / frequency,
                y * scale / frequency
            );

            sum += noise * amplitude;
        }

        return (sum + 1) / 2;
    }
}
