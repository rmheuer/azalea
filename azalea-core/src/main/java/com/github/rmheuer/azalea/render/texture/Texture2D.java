package com.github.rmheuer.azalea.render.texture;

import org.joml.Vector2f;

import java.nio.ByteBuffer;

/**
 * A 2D texture on the GPU.
 */
// TODO: Docs for mip-maps
public interface Texture2D extends Texture, Texture2DRegion {
    /**
     * How sampling the texture should work when UV coordinates extend outside
     * the range [0, 1].
     */
    enum WrappingMode {
        /** Texture is repeated, as if tiled in a grid. */
        REPEAT,
        /** Same as {@code REPEAT}, but every other tile is mirrored. */
        REPEAT_MIRRORED,
        /** UV coordinates are clamped to the range [0, 1]. */
        CLAMP_TO_EDGE
    }

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
    default void setData(BitmapRegion data) {
        setMipMapData(0, data);
    }

    void setMipMapData(int mipLevel, BitmapRegion data);

    /**
     * Uploads a set of bitmap data to the GPU.
     *
     * @param data raw bitmap data to upload, will not be freed
     * @param width width of the bitmap
     * @param height height of the bitmap
     * @param colorFormat format of the bitmap data
     */
    default void setData(ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        setMipMapData(0, data, width, height, colorFormat);
    }

    void setMipMapData(int mipLevel, ByteBuffer data, int width, int height, ColorFormat colorFormat);

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
    default void setSubData(BitmapRegion data, int x, int y) {
        setMipMapSubData(0, data, x, y);
    }

    void setMipMapSubData(int mipLevel, BitmapRegion data, int x, int y);

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
    default void setSubData(ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        setMipMapSubData(0, data, width, height, colorFormat, x, y);
    }

    void setMipMapSubData(int mipLevel, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y);

    /**
     * Generates all mip-map levels down to 1x1.
     */
    void generateAllMipMaps();

    /**
     * Sets the wrapping mode for the U coordinate (horizontal).
     *
     * @param mode wrapping mode to set
     */
    void setWrappingModeU(WrappingMode mode);

    /**
     * Sets the wrapping mode for the V coordinate (vertical).
     *
     * @param mode wrapping mode to set
     */
    void setWrappingModeV(WrappingMode mode);

    /**
     * Sets the wrapping mode for both the U and V coordinates.
     *
     * @param mode wrapping mode to set
     */
    default void setWrappingModes(WrappingMode mode) {
        setWrappingModeU(mode);
        setWrappingModeV(mode);
    }

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
