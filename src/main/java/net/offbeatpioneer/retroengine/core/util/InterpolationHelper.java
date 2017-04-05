package net.offbeatpioneer.retroengine.core.util;

/**
 * Helper class for interpolating values
 */
public class InterpolationHelper {

    public static float[] linear(float x1, float x2, int N) {
        float[] values = new float[N];
        for (int i = 0; i < N; i++) {
            double v = i / (N * 1.0f);
            values[i] = (float) (x1 * (1f - v) + x2 * v);
        }
        return values;
    }

    public static float linearPointBetween(float x1, float x2, int i, int N) {
        double v = i / (N * 1.0f);
        float value = (float) (x1 * (1.0f - v) + x2 * v);
        return value;
    }

    public static int[] linear(int x1, int x2, int N) {
        int[] values = new int[N];
        for (int i = 0; i < N; i++) {
            double v = i / (N * 1.0);
            values[i] = (int) (x1 * (1 - v) + x2 * v);
        }
        return values;
    }
}