package com.github.rmheuer.azalea.math;

public final class MathUtil {
    public static int clamp(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    public static float lerp(float a, float b, float f) {
        return a + (b - a) * f;
    }

    public static float map(float val, float min, float max, float newMin, float newMax) {
        return lerp(newMin, newMax, (val - min) / (max - min));
    }

    public static int nextPowerOf2(int i) {
        return i == 1 ? 1 : Integer.highestOneBit(i - 1) * 2;
    }

    public static int ceilLog2(int i) {
        if (i == 0)
            return 0;

        return 32 - Integer.numberOfLeadingZeros(i - 1);
    }

    private MathUtil() {
        throw new AssertionError();
    }
}
