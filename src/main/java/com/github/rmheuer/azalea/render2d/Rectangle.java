package com.github.rmheuer.azalea.render2d;

import org.joml.Vector2f;

public final class Rectangle {
    private final Vector2f min;
    private final Vector2f max;

    public static Rectangle fromXYSizes(float x, float y, float w, float h) {
        return new Rectangle(x, y, x + w, y + h);
    }

    public static Rectangle fromCenterSizes(float cx, float cy, float w, float h) {
        return new Rectangle(cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2);
    }

    public Rectangle(float minX, float minY, float maxX, float maxY) {
        min = new Vector2f(minX, minY);
        max = new Vector2f(maxX, maxY);
    }

    public Rectangle(Vector2f min, Vector2f max) {
        this.min = min;
        this.max = max;
    }

    public Rectangle(Rectangle r) {
        min = new Vector2f(r.min);
        max = new Vector2f(r.max);
    }

    public Vector2f getMin() {
        return min;
    }

    public Vector2f getMax() {
        return max;
    }

    public float getWidth() {
        return max.x - min.x;
    }

    public float getHeight() {
        return max.y - min.y;
    }

    public Vector2f getSize() {
        return new Vector2f(max).sub(min);
    }

    public boolean containsPoint(Vector2f point) {
        return point.x >= min.x && point.x <= max.x
                && point.y >= min.y && point.y <= max.y;
    }

    public Rectangle shrink(float amount) {
        return shrink(amount, amount);
    }

    public Rectangle shrink(float amountX, float amountY) {
        float halfX = amountX / 2.0f;
        float halfY = amountY / 2.0f;

        return new Rectangle(
                new Vector2f(min).add(halfX, halfY),
                new Vector2f(max).sub(halfX, halfY)
        );
    }

    public Rectangle expand(float amount) {
        return expand(amount, amount);
    }

    public Rectangle expand(float amountX, float amountY) {
        return shrink(-amountX, -amountY);
    }

    public Vector2f getMidpoint() {
        return new Vector2f((min.x + max.x) / 2.0f, (min.y + max.y) / 2.0f);
    }

    public void move(float x, float y) {
        max.sub(min).add(x, y);
        min.set(x, y);
    }

    public void resize(float w, float h) {
        min.add(w, h, max);
    }

    public Rectangle intersect(Rectangle r) {
        float minX = Math.max(min.x, r.min.x);
        float minY = Math.max(min.y, r.min.y);
        float maxX = Math.min(max.x, r.max.x);
        float maxY = Math.min(max.y, r.max.y);
        return new Rectangle(minX, minY, maxX, maxY);
    }

    public boolean isValid() {
        return max.x > min.x && max.y > min.y;
    }
}
