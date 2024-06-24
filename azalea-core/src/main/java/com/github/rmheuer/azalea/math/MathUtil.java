package com.github.rmheuer.azalea.math;

public final class MathUtil {
    /**
     * Constrains a value within a range.
     *
     * @param v value to constrain
     * @param min range minimum
     * @param max range maximum
     * @return constrained value
     */
    public static int clamp(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * Constrains a value within a range.
     *
     * @param v value to constrain
     * @param min range minimum
     * @param max range maximum
     * @return constrained value
     */
    public static float clamp(float v, float min, float max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * Linearly interpolates between two values.
     *
     * @param a beginning value
     * @param b end value
     * @param f fraction between the values
     * @return interpolated value
     */
    public static float lerp(float a, float b, float f) {
        return a + (b - a) * f;
    }

    /**
     * Linearly interpolates between two values.
     *
     * @param a beginning value
     * @param b end value
     * @param f fraction between the values
     * @return interpolated value
     */
    public static int lerpInt(int a, int b, float f) {
        return (int) (a + (b - a) * f);
    }

    /**
     * Maps a value from one range into another.
     *
     * @param val value to map
     * @param min source range minimum
     * @param max source range maximum
     * @param newMin target range minimum
     * @param newMax target range maximum
     * @return mapped value
     */
    public static float map(float val, float min, float max, float newMin, float newMax) {
        return lerp(newMin, newMax, (val - min) / (max - min));
    }

    /**
     * Gets the next power of two above a number.
     *
     * @param i number
     * @return next power of two
     */
    public static int nextPowerOf2(int i) {
        return i == 1 ? 1 : Integer.highestOneBit(i - 1) * 2;
    }

    /**
     * Computes {@code ceil(log2(i))}.
     *
     * @param i input value
     * @return {@code ceil(log2(i))}
     */
    public static int ceilLog2(int i) {
        if (i == 0)
            return 0;

        return 32 - Integer.numberOfLeadingZeros(i - 1);
    }

    /**
     * Squares a number.
     *
     * @param v value to square
     * @return {@code v * v}
     */
    public static double square(double v) {
        return v * v;
    }

    private MathUtil() {
        throw new AssertionError();
    }
}
