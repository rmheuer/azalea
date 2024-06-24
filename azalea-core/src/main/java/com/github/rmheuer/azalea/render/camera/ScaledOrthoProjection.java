package com.github.rmheuer.azalea.render.camera;

import org.joml.Matrix4f;

/**
 * An orthographic projection that scales to fill the screen. The origin is
 * projected to the center of the screen.
 */
public final class ScaledOrthoProjection implements Projection {
    /** The method in which the projection is scaled. */
    public enum ScaleMode {
        /**
         * Stretches the scene to fill the entire screen. This will distort the
         * scene if the screen has a different aspect ratio to the view area.
         */
        STRETCH,
        /**
         * Scales the scene as large as possible while still showing the entire
         * scene on the screen. This will not distort the aspect ratio of the
         * scene.
         */
        FIT
    }

    /**
     * Creates a new projection with the {@link ScaleMode#STRETCH} scale mode.
     *
     * @param width width of the view area in the scene
     * @param height height of the view area in the scene
     * @param near Z coordinate of the near clipping plane
     * @param far Z coordinate of the far clipping plane
     * @return {@code ScaledOrthoProjection} with the specified bounds
     */
    public static ScaledOrthoProjection stretch(float width, float height, float near, float far) {
        return new ScaledOrthoProjection(ScaleMode.STRETCH, width, height, near, far);
    }

    /**
     * Creates a new projection with the {@link ScaleMode#FIT} scale mode.
     *
     * @param width width of the view area in the scene
     * @param height height of the view area in the scene
     * @param near Z coordinate of the near clipping plane
     * @param far Z coordinate of the far clipping plane
     * @return {@code ScaledOrthoProjection} with the specified bounds
     */
    public static ScaledOrthoProjection fit(float width, float height, float near, float far) {
        return new ScaledOrthoProjection(ScaleMode.FIT, width, height, near, far);
    }

    private ScaleMode mode;
    private float width;
    private float height;
    private float near;
    private float far;

    /**
     * @param mode scale mode to use to fill the viewport
     * @param width width of the view area in the scene
     * @param height height of the view area in the scene
     * @param near Z coordinate of the near clipping plane
     * @param far Z coordinate of the far clipping plane
     */
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

    /**
     * Gets the current scale mode used to fill the screen.
     *
     * @return current scale mode
     */
    public ScaleMode getMode() {
        return mode;
    }

    /**
     * Sets the scale mode used to fill the screen.
     *
     * @param mode new scale mode
     */
    public void setMode(ScaleMode mode) {
        this.mode = mode;
    }

    /**
     * Gets the current width of the view area.
     *
     * @return view area width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of the view area.
     *
     * @param width new view area width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Gets the current height of the view area.
     *
     * @return height view area height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of the view area.
     *
     * @param height new view area height
     */
    public void setHeight(float height) {
        this.height = height;
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
