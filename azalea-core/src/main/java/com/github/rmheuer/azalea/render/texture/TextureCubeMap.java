package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.math.CubeFace;

import java.nio.ByteBuffer;

/**
 * A cube-map texture on the GPU. This consists of six 2D textures, one for
 * each cube face.
 */
// TODO: Checks for face image being square and all same size
public interface TextureCubeMap extends Texture {
    /**
     * Allocates GPU memory for the specified texture size, without uploading
     * pixel data. Any previous texture data for the face will be discarded.
     *
     * @param face cube face to allocate
     * @param width width of the allocated texture data
     * @param height height of the allocated texture data
     * @param colorFormat format that the texture data should be stored in
     */
    void setFaceSize(CubeFace face, int width, int height, ColorFormat colorFormat);

    /**
     * Uploads a set of bitmap data for one face to the GPU.
     *
     * @param face cube face data is for
     * @param data data to upload
     */
    void setFaceData(CubeFace face, BitmapRegion data);

    /**
     * Uploads a set of bitmap data for one face to the GPU.
     *
     * @param face cube face data is for
     * @param data raw bitmap data to upload, will not be freed
     * @param width width of the bitmap
     * @param height height of the bitmap
     * @param colorFormat format of the bitmap data
     */
    void setFaceData(CubeFace face, ByteBuffer data, int width, int height, ColorFormat colorFormat);

    /**
     * Sets a section of the texture data of one face on the GPU, leaving the
     * rest the same.
     *
     * @param face cube face data is for
     * @param data data to upload
     * @param x x coordinate of the face texture to upload into
     * @param y y coordinate of the face texture to upload into
     */
    void setFaceSubData(CubeFace face, BitmapRegion data, int x, int y);

    /**
     * Sets a section of the texture data of one face on the GPU, leaving the
     * rest the same.
     *
     * @param face cube face data is for
     * @param data raw bitmap data to upload, will not be freed
     * @param width width of the bitmap
     * @param height height of the bitmap
     * @param colorFormat format of the bitmap data
     * @param x x coordinate of the face texture to upload into
     * @param y y coordinate of the face texture to upload into
     */
    void setFaceSubData(CubeFace face, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y);
}
