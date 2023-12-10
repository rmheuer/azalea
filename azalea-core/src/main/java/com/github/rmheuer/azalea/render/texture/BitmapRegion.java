package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.render.ColorRGBA;
import org.joml.Vector2i;

public interface BitmapRegion {
    ColorRGBA getPixel(int x, int y);
    default ColorRGBA getPixel(Vector2i pos) {
        return getPixel(pos.x, pos.y);
    }

    void setPixel(int x, int y, ColorRGBA color);
    default void setPixel(Vector2i pos, ColorRGBA color) {
        setPixel(pos.x, pos.y, color);
    }

    void blit(BitmapRegion src, int x, int y);
    default void blit(BitmapRegion src, Vector2i pos) {
        blit(src, pos.x, pos.y);
    }

    int getWidth();
    int getHeight();

    int[] getRgbaData();

    Bitmap getSourceBitmap();
    int getSourceOffsetX();
    int getSourceOffsetY();

    default BitmapRegion getSubRegion(int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > getWidth() || y + h > getHeight())
            throw new IndexOutOfBoundsException(x + ", " + y + " size: " + w + "x" + h);

        return new SubBitmap(getSourceBitmap(), x, y, w, h);
    }
}
