package com.github.rmheuer.azalea.render.texture;

import org.joml.Vector2f;

// TODO: Add method for directly uploading data as ByteBuffer
//  TrueTypeFont currently has to convert to Bitmap and back again, which
//  causes data to be copied twice (native -> heap -> native)
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
     * @param mapping mapping from bitmap channels to texture channels
     */
    void setSize(int width, int height, ChannelMapping mapping);

    default void setSize(int width, int height) {
        setSize(width, height, ChannelMapping.DIRECT_RGBA);
    }

    /**
     * Uploads a set of bitmap data to the GPU.
     *
     * @param data data to upload
     * @param mapping mapping from bitmap channels to texture channels
     */
    void setData(BitmapRegion data, ChannelMapping mapping);

    default void setData(BitmapRegion data) {
        setData(data, ChannelMapping.DIRECT_RGBA);
    }

    /**
     * Sets a section of the texture data on GPU, leaving the rest the same.
     * Must be called after either {@link #setData} or {@link #setSize}. The
     * same channel mapping will be used from the previously set data.
     *
     * @param data data to upload
     * @param x x coordinate to upload into
     * @param y y coordinate to upload into
     */
    void setSubData(BitmapRegion data, int x, int y);

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
