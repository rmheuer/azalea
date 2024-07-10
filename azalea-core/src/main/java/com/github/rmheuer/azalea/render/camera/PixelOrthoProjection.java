package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

/**
 * Projects one unit to one pixel, with the origin in the top left, the +X axis
 * to the right, and the +Y axis down.
 */
public final class PixelOrthoProjection implements Projection {
    private float near;
    private float far;

    public PixelOrthoProjection(float near, float far) {
        this.near = near;
        this.far = far;
    }

    @Override
    public Matrix4f getMatrix(float viewportW, float viewportH) {
        return new Matrix4f()
                .ortho(0, viewportW, viewportH, 0, near, far);
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }
}
