package com.github.rmheuer.azalea.render;

import com.github.rmheuer.azalea.math.MathUtil;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;

/**
 * Helpers to work with colors represented as {@code int}s.
 * Int components are from 0 to 255, and float components are from 0 to 1.
 */
public final class Colors {
    private static final int SHIFT_RED   = 0;
    private static final int SHIFT_GREEN = 8;
    private static final int SHIFT_BLUE  = 16;
    private static final int SHIFT_ALPHA = 24;

    private static final int MASK_RED   = 0xFF << SHIFT_RED;
    private static final int MASK_GREEN = 0xFF << SHIFT_GREEN;
    private static final int MASK_BLUE  = 0xFF << SHIFT_BLUE;
    private static final int MASK_ALPHA = 0xFF << SHIFT_ALPHA;
    private static final int MASK_RGB = MASK_RED | MASK_GREEN | MASK_BLUE;

    private static abstract class Common {
        /** @return the red component of the color */
        public static int getRed(int rgba) { return (rgba & MASK_RED) >>> SHIFT_RED; }

        /** @return the green component of the color */
        public static int getGreen(int rgba) { return (rgba & MASK_GREEN) >>> SHIFT_GREEN; }

        /** @return the blue component of the color */
        public static int getBlue(int rgba) { return (rgba & MASK_BLUE) >>> SHIFT_BLUE; }

        public static int setRed(int rgba, int red) { return (rgba & ~MASK_RED) | (red << SHIFT_RED); }
        public static int setGreen(int rgba, int green) { return (rgba & ~MASK_GREEN) | (green << SHIFT_GREEN); }
        public static int setBlue(int rgba, int blue) { return (rgba & ~MASK_BLUE) | (blue << SHIFT_BLUE); }
    }

    /**
     * Helpers to work with RGB colors.
     */
    public static final class RGB extends Common {
        public static final int BLACK   = fromInts(  0,   0,   0);
        public static final int RED     = fromInts(255,   0,   0);
        public static final int GREEN   = fromInts(  0, 255,   0);
        public static final int YELLOW  = fromInts(255, 255,   0);
        public static final int BLUE    = fromInts(  0,   0, 255);
        public static final int MAGENTA = fromInts(255,   0, 255);
        public static final int CYAN    = fromInts(  0, 255, 255);
        public static final int WHITE   = fromInts(255, 255, 255);

        public static int fromFloats(Vector3f rgb) { return fromFloats(rgb.x, rgb.y, rgb.z); }
        public static int fromFloats(float r, float g, float b) {
            return fromInts((int) (r * 255), (int) (g * 255), (int) (b * 255));
        }

        public static int fromInts(Vector3i rgb) { return fromInts(rgb.x, rgb.y, rgb.z); }
        public static int fromInts(int r, int g, int b) {
            r = MathUtil.clamp(r, 0, 255);
            g = MathUtil.clamp(g, 0, 255);
            b = MathUtil.clamp(b, 0, 255);

            return r << SHIFT_RED | g << SHIFT_GREEN | b << SHIFT_BLUE;
        }

        public static Vector3f toFloats(int rgb) { return toFloats(rgb, new Vector3f()); }
        public static Vector3f toFloats(int rgb, Vector3f dest) {
            float r = getRed(rgb) / 255.0f;
            float g = getGreen(rgb) / 255.0f;
            float b = getBlue(rgb) / 255.0f;

            dest.set(r, g, b);
            return dest;
        }

        public static Vector3i toInts(int rgb) { return toInts(rgb, new Vector3i()); }
        public static Vector3i toInts(int rgb, Vector3i dest) {
            int r = getRed(rgb);
            int g = getGreen(rgb);
            int b = getBlue(rgb);

            dest.set(r, g, b);
            return dest;
        }

        /**
         * Creates an RGBA color with the specified RGB and alpha values.
         * @param rgb RGB components
         * @param a alpha component
         * @return RGBA color with the specified components
         */
        public static int withAlpha(int rgb, int a) {
            a = MathUtil.clamp(a, 0, 255);
            return rgb | (a << SHIFT_ALPHA);
        }

        /**
         * Linearly interpolates between two colors.
         *
         * @param rgb1 beginning color
         * @param rgb2 end color
         * @param factor fraction between the colors
         * @return interpolated color
         */
        public static int lerp(int rgb1, int rgb2, float factor) {
            if (factor == 0) return rgb1;
            if (factor == 1) return rgb2;

            return fromInts(
                    MathUtil.lerpInt(getRed(rgb1), getRed(rgb2), factor),
                    MathUtil.lerpInt(getGreen(rgb1), getGreen(rgb2), factor),
                    MathUtil.lerpInt(getBlue(rgb1), getBlue(rgb2), factor)
            );
        }
    }

    public static final class RGBA extends Common {
        public static final int TRANSPARENT = fromInts(0, 0, 0, 0);
        public static final int BLACK = RGB.withAlpha(RGB.BLACK, 255);
        public static final int RED = RGB.withAlpha(RGB.RED, 255);
        public static final int GREEN = RGB.withAlpha(RGB.GREEN, 255);
        public static final int YELLOW = RGB.withAlpha(RGB.YELLOW , 255);
        public static final int BLUE = RGB.withAlpha(RGB.BLUE, 255);
        public static final int MAGENTA = RGB.withAlpha(RGB.MAGENTA, 255);
        public static final int CYAN = RGB.withAlpha(RGB.CYAN, 255);
        public static final int WHITE = RGB.withAlpha(RGB.WHITE, 255);

        public static int fromFloats(Vector3f rgb) { return fromFloats(rgb.x, rgb.y, rgb.z, 1); }
        public static int fromFloats(Vector4f rgba) { return fromFloats(rgba.x, rgba.y, rgba.z, rgba.w); }
        public static int fromFloats(float r, float g, float b) { return fromFloats(r, g, b, 1); }
        public static int fromFloats(float r, float g, float b, float a) {
            return fromInts((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
        }

        public static int fromInts(Vector3i rgb) { return fromInts(rgb.x, rgb.y, rgb.z, 255); }
        public static int fromInts(Vector4i rgba) { return fromInts(rgba.x, rgba.y, rgba.z, rgba.w); }
        public static int fromInts(int r, int g, int b) { return fromInts(r, g, b, 255); }
        public static int fromInts(int r, int g, int b, int a) {
            r = MathUtil.clamp(r, 0, 255);
            g = MathUtil.clamp(g, 0, 255);
            b = MathUtil.clamp(b, 0, 255);
            a = MathUtil.clamp(a, 0, 255);

            return r << SHIFT_RED | g << SHIFT_GREEN | b << SHIFT_BLUE | a << SHIFT_ALPHA;
        }

        public static int fromRGB(int rgb) {
            return RGB.withAlpha(rgb, 255);
        }

        public static Vector4f toFloats(int rgba) { return toFloats(rgba, new Vector4f()); }
        public static Vector4f toFloats(int rgba, Vector4f dest) {
            float r = getRed(rgba) / 255.0f;
            float g = getGreen(rgba) / 255.0f;
            float b = getBlue(rgba) / 255.0f;
            float a = getAlpha(rgba) / 255.0f;

            dest.set(r, g, b, a);
            return dest;
        }
        
        public static Vector4i toInts(int rgba) { return toInts(rgba, new Vector4i()); }
        public static Vector4i toInts(int rgba, Vector4i dest) {
            int r = getRed(rgba);
            int g = getGreen(rgba);
            int b = getBlue(rgba);
            int a = getAlpha(rgba);

            dest.set(r, g, b, a);
            return dest;
        }

        /** @return the alpha component of the color */
        public static int getAlpha(int rgba) { return (rgba & MASK_ALPHA) >>> SHIFT_ALPHA; }

        public static int setAlpha(int rgba, int alpha) { return (rgba & ~MASK_ALPHA) | (alpha << SHIFT_ALPHA); }

        /** @return the rgb components of the color */
        public static int getRGB(int rgba) {
            return rgba & MASK_RGB;
        }

        /**
         * Linearly interpolates between two colors.
         *
         * @param rgba1 beginning color
         * @param rgba2 end color
         * @param factor fraction between the colors
         * @return interpolated color
         */
        public static int lerp(int rgba1, int rgba2, float factor) {
            if (factor == 0) return rgba1;
            if (factor == 1) return rgba2;

            return fromInts(
                    MathUtil.lerpInt(getRed(rgba1), getRed(rgba2), factor),
                    MathUtil.lerpInt(getGreen(rgba1), getGreen(rgba2), factor),
                    MathUtil.lerpInt(getBlue(rgba1), getBlue(rgba2), factor),
                    MathUtil.lerpInt(getAlpha(rgba1), getAlpha(rgba2), factor)
            );
        }
    }
}
