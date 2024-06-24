package com.github.rmheuer.azalea.math;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

/**
 * A transformation stack.
 */
public final class PoseStack {
    /**
     * The maximum number of times the stack can be pushed.
     */
    // TODO: Don't have this limitation
    public static final int MAX_STACK_SIZE = 32;

    // FIXME: not public
    public final Matrix4fStack stack;

    /**
     * Creates a new {@code PoseStack} representing the identity
     * transformation.
     */
    public PoseStack() {
        stack = new Matrix4fStack(MAX_STACK_SIZE);
    }

    /**
     * Pushes the transformation stack.
     */
    public void push() {
        stack.pushMatrix();
    }

    /**
     * Applies a transform to the stack.
     *
     * @param transform transform to apply
     */
    public void applyTransform(Transform transform) {
        // TODO: Check if this should be mul
        stack.mulLocal(transform.getMatrix());
    }

    /**
     * Pops the transformation stack. This restores the transform to the state
     * it was when {@link #push()} was called.
     */
    public void pop() {
        stack.popMatrix();
    }

    /**
     * Gets the 4x4 transformation matrix of the current pose.
     *
     * @return matrix
     */
    public Matrix4f getMatrix() {
        return stack;
    }

    /**
     * Gets the origin position after the transformation has been applied.
     *
     * @return position
     */
    public Vector3f getPosition() {
        return stack.transformPosition(new Vector3f(0, 0, 0));
    }

    /**
     * Gets the local forward direction after the transformation has been
     * applied.
     *
     * @return forward vector
     */
    public Vector3f getForward() {
        return stack.transformDirection(new Vector3f(0, 0, -1));
    }

    /**
     * Gets the local up direction after the transformation has been applied.
     *
     * @return up vector
     */
    public Vector3f getUp() {
        return stack.transformDirection(new Vector3f(0, 1, 0));
    }

    /**
     * Gets the local right direction after the transformation has been
     * applied.
     *
     * @return right vector
     */
    public Vector3f getRight() {
        return stack.transformDirection(new Vector3f(1, 0, 0));
    }
}
