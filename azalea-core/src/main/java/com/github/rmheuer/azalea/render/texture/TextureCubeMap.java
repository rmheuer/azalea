package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.math.CubeFace;

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A cube-map texture on the GPU. This consists of six 2D textures, one for
 * each cube face.
 */
// TODO: Checks for face image being square and all same size
// TODO: Support mip-maps
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
     * Loads the bitmap data for one face from an {@code InputStream} and
     * uploads it to the GPU. The stream is decoded using
     * {@link Bitmap#decode(InputStream)}.
     *
     * @param face cube face data is for
     * @param in input stream to read image data from
     * @throws IOException if an IO error occurs while decoding the image data
     */
    default void setFaceData(CubeFace face, InputStream in) throws IOException {
        try (Bitmap bitmap = Bitmap.decode(in)) {
            setFaceData(face, bitmap);
        }
    }

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
