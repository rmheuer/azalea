package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

/**
 * A perspective projection in which objects far away appear smaller.
 */
public final class PerspectiveProjection implements Projection {
    private float fov;
    private float nearPlane;
    private float farPlane;

    /**
     * Creates a new perspective projection with default clipping planes.
     * The near plane is at 0.01, and the far plane is at 1000.
     *
     * @param fov field of view angle in radians
     */
    public PerspectiveProjection(float fov) {
        this(fov, 0.01f, 1000f);
    }

    /**
     * @param fov field of view angle in radians
     * @param nearPlane near clipping plane distance, should be positive
     * @param farPlane far clipping plane distance, should be positive
     */
    public PerspectiveProjection(float fov, float nearPlane, float farPlane) {
        this.fov = fov;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    @Override
    public Matrix4f getMatrix(float viewportW, float viewportH) {
        return new Matrix4f().setPerspective(fov, viewportW / viewportH, nearPlane, farPlane);
    }

    /**
     * Gets the current field of view.
     *
     * @return field of view angle in radians
     */
    public float getFov() {
        return fov;
    }

    /**
     * Sets the field of view.
     *
     * @param fov new field of view angle in radians
     */
    public void setFov(float fov) {
        this.fov = fov;
    }

    /**
     * Gets the distance to the near clipping plane.
     *
     * @return near clipping plane distance
     */
    public float getNearPlane() {
        return nearPlane;
    }

    /**
     * Sets the distance to the near clipping plane.
     *
     * @param nearPlane new near clipping plane distance
     */
    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    /**
     * Gets the distance to the far clipping plane.
     *
     * @return farPlane far clipping plane distance
     */
    public float getFarPlane() {
        return farPlane;
    }

    /**
     * Sets the distance to the far clipping plane.
     *
     * @param farPlane new far clipping plane distance
     */
    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }
}
