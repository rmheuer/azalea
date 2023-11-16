package com.github.rmheuer.azalea.math;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public final class PoseStack {
    // FIXME: not public
    public final Matrix4fStack stack;

    public PoseStack() {
        stack = new Matrix4fStack(32);
    }

    public void push() {
        stack.pushMatrix();
    }

    public void applyTransform(Transform transform) {
        // TODO: Check if this should be mul
        stack.mulLocal(transform.getMatrix());
    }

    public void pop() {
        stack.popMatrix();
    }

    public Matrix4f getMatrix() {
        return stack;
    }

    public Vector3f getPosition() {
        return stack.transformPosition(new Vector3f(0, 0, 0));
    }

    public Vector3f getForward() {
        return stack.transformDirection(new Vector3f(0, 0, -1));
    }

    public Vector3f getUp() {
        return stack.transformDirection(new Vector3f(0, 1, 0));
    }

    public Vector3f getRight() {
        return stack.transformDirection(new Vector3f(1, 0, 0));
    }
}
