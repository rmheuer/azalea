package com.github.rmheuer.azalea.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents an affine 3D transformation.
 */
public final class Transform {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    /**
     * Creates a new {@code Transform} representing the identity
     * transformation.
     */
    public Transform() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
    }

    /**
     * Gets the 4x4 matrix representing this transformation.
     *
     * @return transform matrix
     */
    public Matrix4f getMatrix() {
        return new Matrix4f()
                .translate(position)
                .rotateYXZ(rotation)
                .scale(scale);
    }

    /**
     * Gets the inverse matrix of this transformation.
     *
     * @return inverse transform matrix
     */
    public Matrix4f getInverseMatrix() {
        return getMatrix().invertAffine();
    }

    /**
     * Gets the forward vector after this transform has been applied.
     *
     * @return forward vector
     */
    public Vector3f getForward() {
        return new Vector3f(0, 0, -1).rotateZ(rotation.z).rotateX(rotation.x).rotateY(rotation.y);
    }

    /**
     * Gets the up vector after this transform has been applied.
     *
     * @return up vector
     */
    public Vector3f getUp() {
        return new Vector3f(0, 1, 0).rotateZ(rotation.z).rotateX(rotation.x).rotateY(rotation.y);
    }

    /**
     * Gets the right vector after this transform has been applied.
     *
     * @return right vector
     */
    public Vector3f getRight() {
        return new Vector3f(1, 0, 0).rotateZ(rotation.z).rotateX(rotation.x).rotateY(rotation.y);
    }

    /**
     * Gets the translation component of this transformation.
     *
     * @return position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Sets the translation component of this transformation.
     *
     * @param position new position
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * Gets the rotation component of this transformation.
     *
     * @return rotation
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation component of this transformation.
     *
     * @param rotation new rotation
     */
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    /**
     * Gets the scale component of this transformation.
     *
     * @return scale
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * Sets the scale component of this transformation.
     *
     * @param scale new scale
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}
