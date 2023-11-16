package com.github.rmheuer.azalea.render.camera;

import com.github.rmheuer.azalea.math.Transform;
import org.joml.Matrix4f;

public final class Camera {
    private Projection projection;
    private Transform transform;

    public Camera(Projection projection) {
        this(projection, new Transform());
    }

    public Camera(Projection projection, Transform transform) {
        this.projection = projection;
        this.transform = transform;
    }

    public Matrix4f getProjectionMatrix(float viewportW, float viewportH) {
        return projection.getMatrix(viewportW, viewportH);
    }

    public Matrix4f getViewMatrix() {
        return transform.getInverseMatrix();
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
