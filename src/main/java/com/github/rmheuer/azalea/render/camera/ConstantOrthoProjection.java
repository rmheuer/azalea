package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

public final class ConstantOrthoProjection implements Projection {
    private float pixelsPerUnit;
    private float near;
    private float far;

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

    public float getPixelsPerUnit() {
        return pixelsPerUnit;
    }

    public void setPixelsPerUnit(float pixelsPerUnit) {
        this.pixelsPerUnit = pixelsPerUnit;
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
