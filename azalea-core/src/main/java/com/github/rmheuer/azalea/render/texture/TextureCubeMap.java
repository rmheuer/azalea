package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.math.CubeFace;

/**
 * A cube-map texture on the GPU. This consists of six 2D textures, one for
 * each cube face.
 */
public interface TextureCubeMap extends Texture {
    /**
     * Uploads a set of bitmap data for one face to the GPU.
     *
     * @param face cube face data is for
     * @param data data to upload
     */
    void setFaceData(CubeFace face, BitmapRegion data);

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
}
