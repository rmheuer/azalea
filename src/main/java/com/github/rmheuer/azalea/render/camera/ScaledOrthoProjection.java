package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

public final class ScaledOrthoProjection implements Projection {
    public enum ScaleMode {
        STRETCH,
        FIT
    }

    public static ScaledOrthoProjection stretch(float width, float height, float near, float far) {
        return new ScaledOrthoProjection(ScaleMode.STRETCH, width, height, near, far);
    }

    public static ScaledOrthoProjection fit(float width, float height, float near, float far) {
        return new ScaledOrthoProjection(ScaleMode.FIT, width, height, near, far);
    }

    private ScaleMode mode;
    private float width;
    private float height;
    private float near;
    private float far;

    public ScaledOrthoProjection(ScaleMode mode, float width, float height, float near, float far) {
        this.mode = mode;
        this.width = width;
        this.height = height;
        this.near = near;
        this.far = far;
    }

    @Override
    public Matrix4f getMatrix(float viewportW, float viewportH) {
        float halfW = width / 2.0f;
        float halfH = height / 2.0f;

        if (mode == ScaleMode.FIT) {
            float relW = viewportW / width;
            float relH = viewportH / height;

            if (relW > relH) {
                halfW *= relW / relH;
            } else {
                halfH *= relH / relW;
            }
        }

        return new Matrix4f().setOrtho(-halfW, halfW, -halfH, halfH, near, far);
    }

    public ScaleMode getMode() {
        return mode;
    }

    public void setMode(ScaleMode mode) {
        this.mode = mode;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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
