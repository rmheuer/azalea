package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

public final class PerspectiveProjection implements Projection {
    private float fov;
    private float nearPlane;
    private float farPlane;

    public PerspectiveProjection(float fov, float nearPlane, float farPlane) {
        this.fov = fov;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    @Override
    public Matrix4f getMatrix(float viewportW, float viewportH) {
        return new Matrix4f().setPerspective(fov, viewportW / viewportH, nearPlane, farPlane);
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    public float getFarPlane() {
        return farPlane;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }
}
