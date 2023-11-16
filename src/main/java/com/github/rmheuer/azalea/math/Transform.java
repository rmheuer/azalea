package com.github.rmheuer.azalea.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Transform {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public Transform() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
    }

    public Matrix4f getMatrix() {
        return new Matrix4f()
                .translate(position)
                .rotateYXZ(rotation)
                .scale(scale);
    }

    public Matrix4f getInverseMatrix() {
        return getMatrix().invertAffine();
    }

    public Vector3f getForward() {
        return new Vector3f(0, 0, -1).rotateZ(rotation.z).rotateX(rotation.x).rotateY(rotation.y);
    }

    public Vector3f getUp() {
        return new Vector3f(0, 1, 0).rotateZ(rotation.z).rotateX(rotation.x).rotateY(rotation.y);
    }

    public Vector3f getRight() {
        return new Vector3f(1, 0, 0).rotateZ(rotation.z).rotateX(rotation.x).rotateY(rotation.y);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}
