package com.github.rmheuer.azalea.render.texture;

import org.joml.Vector2f;

/**
 * A 2D texture on the GPU.
 */
public interface Texture2D extends Texture, Texture2DRegion {
    /**
     * Uploads a set of bitmap data to the GPU.
     *
     * @param data data to upload
     */
    void setData(BitmapRegion data);

    /**
     * Sets a section of the texture data on GPU, leaving the rest the same.
     *
     * @param data data to upload
     * @param x x coordinate to upload into
     * @param y y coordinate to upload into
     */
    void setSubData(BitmapRegion data, int x, int y);

    /**
     * Allocates GPU memory for the specified texture size, without uploading
     * pixel data. Any previous texture data will be discarded.
     *
     * @param width width of the allocated texture data
     * @param height height of the allocated texture data
     */
    void setSize(int width, int height);

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
