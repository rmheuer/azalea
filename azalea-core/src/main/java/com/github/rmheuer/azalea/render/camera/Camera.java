package com.github.rmheuer.azalea.render.camera;

import com.github.rmheuer.azalea.math.Transform;
import org.joml.Matrix4f;

/**
 * Represents the viewpoint of a scene.
 */
public final class Camera {
    private Projection projection;
    private Transform transform;

    /**
     * Creates a new camera with the given projection, positioned at the
     * origin, facing towards -Z with +Y up.
     *
     * @param projection projection onto the screen
     */
    public Camera(Projection projection) {
        this(projection, new Transform());
    }

    /**
     * Creates a new camera with the given projection and transform.
     *
     * @param projection projection onto the screen
     * @param transform transform of the camera
     */
    public Camera(Projection projection, Transform transform) {
        this.projection = projection;
        this.transform = transform;
    }

    /**
     * Gets the projection matrix for the given viewport size.
     *
     * @param viewportW width of the viewport
     * @param viewportH height of the viewport
     * @return 4x4 projection matrix
     */
    public Matrix4f getProjectionMatrix(float viewportW, float viewportH) {
        return projection.getMatrix(viewportW, viewportH);
    }

    /**
     * Gets the view matrix for the camera.
     *
     * @return view matrix
     */
    public Matrix4f getViewMatrix() {
        return transform.getInverseMatrix();
    }

    /**
     * Gets the current projection of the camera.
     *
     * @return current projection
     */
    public Projection getProjection() {
        return projection;
    }

    /**
     * Sets the current projection of the camera.
     *
     * @param projection new projection to use
     */
    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    /**
     * Gets the transform of the camera.
     *
     * @return transform
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Sets the transform of the camera.
     *
     * @param transform new transform
     */
    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
