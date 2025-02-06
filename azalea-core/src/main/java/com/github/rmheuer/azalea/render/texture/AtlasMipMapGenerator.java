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
            return (int) average(a, b, c, d);
        } else {
            int aa = RGBA.getAlpha(a);
            int ab = RGBA.getAlpha(b);
            int ac = RGBA.getAlpha(c);
            int ad = RGBA.getAlpha(d);
            float alpha = average(aa, ab, ac, ad);

            return RGBA.fromInts(
                    (int) (average(RGBA.getRed(a) * aa, RGBA.getRed(b) * ab, RGBA.getRed(c) * ac, RGBA.getRed(d) * ad) / alpha),
                    (int) (average(RGBA.getGreen(a) * aa, RGBA.getGreen(b) * ab, RGBA.getGreen(c) * ac, RGBA.getGreen(d) * ad) / alpha),
                    (int) (average(RGBA.getBlue(a) * aa, RGBA.getBlue(b) * ab, RGBA.getBlue(c) * ac, RGBA.getBlue(d) * ad) / alpha),
                    (int) alpha
            );
        }
    }

    private static float average(int a, int b, int c, int d) {
        return (a + b + c + d) / 4.0f;
    }
}
