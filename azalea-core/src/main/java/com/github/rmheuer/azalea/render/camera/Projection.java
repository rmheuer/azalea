package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

/**
 * A transformation that projects a point in camera space to a point in screen
 * space.
 */
public interface Projection {
    /**
     * Gets the 4x4 matrix that applies the projection.
     *
     * @param viewportW width of the viewport in pixels
     * @param viewportH height of the viewport in pixels
     * @return projection matrix
     */
    Matrix4f getMatrix(float viewportW, float viewportH);
}
