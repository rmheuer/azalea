package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.render.Colors.RGBA;
import com.github.rmheuer.azalea.render.Renderer;

public final class AtlasMipMapGenerator {
    public static Texture2D createMipped(Renderer renderer, Bitmap baseImg, int levels) {
        Texture2D tex = renderer.createTexture2D();

        tex.setData(baseImg);

        Bitmap downscaled = null;
        for (int level = 1; level <= levels; level++) {
            Bitmap smaller = downscale(downscaled == null ? baseImg : downscaled);
            if (downscaled != null)
                downscaled.close();
            downscaled = smaller;

            tex.setMipMapData(level, downscaled);
        }
        tex.setMipMapRange(0, levels);

        if (downscaled != null)
            downscaled.close();

        return tex;
    }

    public static Bitmap downscale(Bitmap img) {
        int w = Math.max(1, img.getWidth() / 2);
        int h = Math.max(1, img.getHeight() / 2);
        ColorFormat format = img.getColorFormat();
        Bitmap scaled = new Bitmap(w, h, format);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int sx = x * 2;
                int sy = y * 2;

                int a = img.getPixel(sx, sy);
                int b = img.getPixel(sx + 1, sy);
                int c = img.getPixel(sx, sy + 1);
                int d = img.getPixel(sx + 1, sy + 1);
                int avg = averageColors(format, a, b, c, d);

                scaled.setPixel(x, y, avg);
            }
        }

        return scaled;
    }

    private static int averageColors(ColorFormat format, int a, int b, int c, int d) {
        if (format == ColorFormat.GRAYSCALE) {
            return average(a, b, c, d);
        } else {
            return RGBA.fromInts(
                    average(RGBA.getRed(a), RGBA.getRed(b), RGBA.getRed(c), RGBA.getRed(d)),
                    average(RGBA.getGreen(a), RGBA.getGreen(b), RGBA.getGreen(c), RGBA.getGreen(d)),
                    average(RGBA.getBlue(a), RGBA.getBlue(b), RGBA.getBlue(c), RGBA.getBlue(d)),
                    average(RGBA.getAlpha(a), RGBA.getAlpha(b), RGBA.getAlpha(c), RGBA.getAlpha(d))
            );
        }
    }

    private static int average(int a, int b, int c, int d) {
        return (a + b + c + d) / 4;
    }
}
