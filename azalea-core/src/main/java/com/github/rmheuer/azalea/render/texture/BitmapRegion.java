package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.render.ColorRGBA;
import org.joml.Vector2i;

/**
 * Represents a section of a bitmap. Pixel coordinates have the origin in the
 * top-left corner, with +x to the right and +y down.
 */
public interface BitmapRegion {
    /**
     * Gets the color of a pixel.
     *
     * @param x x coordinate of the pixel
     * @param y y coordinate of the pixel
     * @return color of the pixel
     */
    ColorRGBA getPixel(int x, int y);

    /**
     * Gets the color of a pixel.
     *
     * @param pos coordinates of the pixel
     * @return color of the pixel
     */
    default ColorRGBA getPixel(Vector2i pos) {
        return getPixel(pos.x, pos.y);
    }

    /**
     * Sets the color of a pixel.
     *
     * @param x x coordinate of the pixel
     * @param y y coordinate of the pixel
     * @param color new color for the pixel
     */
    void setPixel(int x, int y, ColorRGBA color);

    /**
     * Sets the color of a pixel.
     *
     * @param pos coordinates of the pixel
     * @param color new color for the pixel
     */
    default void setPixel(Vector2i pos, ColorRGBA color) {
        setPixel(pos.x, pos.y, color);
    }

    /**
     * Copies the contents of another {@code BitmapRegion} into this bitmap.
     *
     * @param src bitmap to copy
     * @param x x coordinate the source's origin should be placed in this bitmap
     * @param y y coordinate the source's origin should be placed in this bitmap
     */
    void blit(BitmapRegion src, int x, int y);

    /**
     * Copies the contents of another {@code BitmapRegion} into this bitmap.
     *
     * @param src bitmap to copy
     * @param pos position the source's origin should be placed in this bitmap
     */
    default void blit(BitmapRegion src, Vector2i pos) {
        blit(src, pos.x, pos.y);
    }

    /**
     * Gets the width of this region in pixels.
     *
     * @return width
     */
    int getWidth();

    /**
     * Gets the height of this region in pixels.
     *
     * @return height
     */
    int getHeight();

    /**
     * Gets the packed RGBA data of this region.
     *
     * @return rgba data
     */
    int[] getRgbaData();

    /**
     * Gets the {@code Bitmap} the actual image data is stored in.
     *
     * @return source bitmap
     */
    Bitmap getSourceBitmap();

    /**
     * Gets the offset of from the source bitmap's origin on the X axis.
     *
     * @return source offset x
     */
    int getSourceOffsetX();

    /**
     * Gets the offset of from the source bitmap's origin on the Y axis.
     *
     * @return source offset y
     */
    int getSourceOffsetY();

    /**
     * Gets a sub-region of this region.
     *
     * @param x x coordinate of the top left corner of the sub-region
     * @param y y coordinate of the top left corner of the sub-region
     * @param w width of the sub-region
     * @param h height of the sub-region
     * @return the sub-region
     */
    default BitmapRegion getSubRegion(int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > getWidth() || y + h > getHeight())
            throw new IndexOutOfBoundsException(x + ", " + y + " size: " + w + "x" + h);

        return new SubBitmap(getSourceBitmap(), x, y, w, h);
    }
}
