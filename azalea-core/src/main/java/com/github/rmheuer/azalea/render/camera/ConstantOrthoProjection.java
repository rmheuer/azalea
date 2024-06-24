package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

/**
 * An orthographic projection where the scene is always rendered at a constant
 * size on the screen. The origin is projected to the center of the screen.
 */
public final class ConstantOrthoProjection implements Projection {
    private float pixelsPerUnit;
    private float near;
    private float far;

    /**
     * @param pixelsPerUnit number of screen pixels per world unit
     * @param near near clipping plane on the Z axis
     * @param far far clipping plane on the Z axis
     */
    public ConstantOrthoProjection(float pixelsPerUnit, float near, float far) {
        this.pixelsPerUnit = pixelsPerUnit;
        this.near = near;
        this.far = far;
    }

    @Override
    public Matrix4f getMatrix(float viewportW, float viewportH) {
        float halfW = viewportW / pixelsPerUnit / 2;
        float halfH = viewportH / pixelsPerUnit / 2;
        return new Matrix4f().setOrtho(-halfW, halfW, -halfH, halfH, near, far);
    }

    /**
     * Gets the current number of screen pixels per world unit.
     *
     * @return pixels per unit
     */
    public float getPixelsPerUnit() {
        return pixelsPerUnit;
    }

    /**
     * Sets the number of screen pixels per world unit.
     *
     * @param pixelsPerUnit new pixels per unit
     */
    public void setPixelsPerUnit(float pixelsPerUnit) {
        this.pixelsPerUnit = pixelsPerUnit;
    }

    /**
     * Gets the Z coordinate of the near clipping plane.
     *
     * @return near clipping plane
     */
    public float getNear() {
        return near;
    }

    /**
     * Sets the Z coordinate of the near clipping plane
     *
     * @param near new near clipping plane
     */
    public void setNear(float near) {
        this.near = near;
    }

    /**
     * Gets the Z component of the far clipping plane.
     *
     * @return far clipping plane
     */
    public float getFar() {
        return far;
    }

    /**
     * Sets the Z component of the far clipping plane.
     *
     * @param far new far clipping plane
     */
    public void setFar(float far) {
        this.far = far;
    }
}
