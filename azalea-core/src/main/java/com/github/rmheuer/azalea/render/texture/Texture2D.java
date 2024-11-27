package com.github.rmheuer.azalea.render.texture;

import org.joml.Vector2f;

import java.nio.ByteBuffer;

/**
 * A 2D texture on the GPU.
 */
public interface Texture2D extends Texture, Texture2DRegion {
    /**
     * Allocates GPU memory for the specified texture size, without uploading
     * pixel data. Any previous texture data will be discarded.
     *
     * @param width width of the allocated texture data
     * @param height height of the allocated texture data
     * @param colorFormat format that the texture data should be stored in
     */
    void setSize(int width, int height, ColorFormat colorFormat);

    /**
     * Uploads a set of bitmap data to the GPU.
     *
     * @param data data to upload
     */
    void setData(BitmapRegion data);

    /**
     * Uploads a set of bitmap data to the GPU.
     *
     * @param data raw bitmap data to upload, will not be freed
     * @param width width of the bitmap
     * @param height height of the bitmap
     * @param colorFormat format of the bitmap data
     */
    void setData(ByteBuffer data, int width, int height, ColorFormat colorFormat);

    /**
     * Sets a section of the texture data on GPU, leaving the rest the same.
     * Must be called after either {@link #setData} or {@link #setSize}. The
     * same channel mapping will be used from the previously set data. The
     * color format of the data must match the previously set data.
     *
     * @param data data to upload
     * @param x x coordinate to upload into
     * @param y y coordinate to upload into
     */
    void setSubData(BitmapRegion data, int x, int y);

    /**
     * Sets a section of the texture data on GPU, leaving the rest the same.
     * Must be called after either {@link #setData} or {@link #setSize}. The
     * same channel mapping will be used from the previously set data. The
     * color format of the data must match the previously set data.
     *
     * @param data raw bitmap data to upload, will not be freed
     * @param width width of the bitmap
     * @param height height of the bitmap
     * @param colorFormat format of the bitmap data
     * @param x x coordinate to upload into
     * @param y y coordinate to upload into
     */
    void setSubData(ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y);

    @Override
    default Texture2D getSourceTexture() {
        return this;
    }

    @Override
    default Vector2f getRegionTopLeftUV() {
        return new Vector2f(0, 0);
    }

    @Override
    default Vector2f getRegionBottomRightUV() {
        return new Vector2f(1, 1);
    }
}
