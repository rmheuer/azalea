package com.github.rmheuer.azalea.render2d;

import org.joml.Vector2f;

/**
 * A rectangular region.
 */
public final class Rectangle {
    private final Vector2f min;
    private final Vector2f max;

    /**
     * Creates a new rectangle from the top-left corner and its size.
     *
     * @param x top left x position
     * @param y top left y position
     * @param w width of the rectangle
     * @param h height of the rectangle
     * @return created rectangle
     */
    public static Rectangle fromXYSizes(float x, float y, float w, float h) {
        return new Rectangle(x, y, x + w, y + h);
    }

    /**
     * Creates a new rectangle from the center and its size.
     *
     * @param cx center x position
     * @param cy center y position
     * @param w width of the rectangle
     * @param h height of the rectangle
     * @return created rectangle
     */
    public static Rectangle fromCenterSizes(float cx, float cy, float w, float h) {
        return new Rectangle(cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2);
    }

    /**
     * @param minX minimum x position
     * @param minY minimum y position
     * @param maxX maximum x position
     * @param maxY maximum y position
     */
    public Rectangle(float minX, float minY, float maxX, float maxY) {
        min = new Vector2f(minX, minY);
        max = new Vector2f(maxX, maxY);
    }

    /**
     * @param min minimum position
     * @param max maximum position
     */
    public Rectangle(Vector2f min, Vector2f max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Copy constructor.
     * @param r rectangle to copy
     */
    public Rectangle(Rectangle r) {
        min = new Vector2f(r.min);
        max = new Vector2f(r.max);
    }

    /**
     * Gets the minimum position of the region.
     *
     * @return minimum position
     */
    public Vector2f getMin() {
        return min;
    }

    /**
     * Gets the maximum position of the region.
     *
     * @return maximum position
     */
    public Vector2f getMax() {
        return max;
    }

    /**
     * Gets the width of the rectangle.
     *
     * @return width
     */
    public float getWidth() {
        return max.x - min.x;
    }

    /**
     * Gets the height of the rectangle.
     *
     * @return height
     */
    public float getHeight() {
        return max.y - min.y;
    }

    /**
     * Gets the size of the rectangle.
     *
     * @return size
     */
    public Vector2f getSize() {
        return new Vector2f(max).sub(min);
    }

    /**
     * Gets whether a point is inside the rectangle.
     *
     * @param point point to check
     * @return whether it is inside
     */
    public boolean containsPoint(Vector2f point) {
        return point.x >= min.x && point.x <= max.x
                && point.y >= min.y && point.y <= max.y;
    }

    /**
     * Shrinks the rectangle inward towards the center on both axes.
     *
     * @param amount amount to shrink on both axes
     * @return shrunk rectangle
     */
    public Rectangle shrink(float amount) {
        return shrink(amount, amount);
    }

    /**
     * Shrinks the rectangle inward towards the center.
     *
     * @param amountX amount to shrink on the X axis
     * @param amountY amount to shrink on the Y axis
     * @return shrunk rectangle
     */
    public Rectangle shrink(float amountX, float amountY) {
        float halfX = amountX / 2.0f;
        float halfY = amountY / 2.0f;

        return new Rectangle(
                new Vector2f(min).add(halfX, halfY),
                new Vector2f(max).sub(halfX, halfY)
        );
    }

    /**
     * Expands the rectangle outward from the center on both axes.
     *
     * @param amount amount to expand on both axes
     * @return expanded rectangle
     */
    public Rectangle expand(float amount) {
        return expand(amount, amount);
    }

    /**
     * Expands the rectangle outward from the center.
     *
     * @param amountX amount to expand on the X axis
     * @param amountY amount to expand on the Y axis
     * @return expanded rectangle
     */
    public Rectangle expand(float amountX, float amountY) {
        return shrink(-amountX, -amountY);
    }

    /**
     * Gets the midpoint (center) of the rectangle.
     *
     * @return midpoint
     */
    public Vector2f getMidpoint() {
        return new Vector2f((min.x + max.x) / 2.0f, (min.y + max.y) / 2.0f);
    }

    /**
     * Moves the top-left corner of the rectangle to the specified position,
     * keeping the size the same.
     *
     * @param x new top-left corner X
     * @param y new top-left corner Y
     */
    public void moveTo(float x, float y) {
        max.sub(min).add(x, y);
        min.set(x, y);
    }

    /**
     * Resizes the rectangle without changing the position of the top-left
     * corner.
     *
     * @param w new width
     * @param h new height
     */
    public void resize(float w, float h) {
        min.add(w, h, max);
    }

    /**
     * Gets the rectangle that represents the intersection between this
     * rectangle and another.
     *
     * @param r rectangle to intersect with
     * @return intersected rectangle, or {@code null} if they don't intersect
     */
    public Rectangle intersect(Rectangle r) {
        float minX = Math.max(min.x, r.min.x);
        float minY = Math.max(min.y, r.min.y);
        float maxX = Math.min(max.x, r.max.x);
        float maxY = Math.min(max.y, r.max.y);

        if (maxX > minX && maxY > minY)
            return new Rectangle(minX, minY, maxX, maxY);
        else
            return null;
    }

    /**
     * Gets whether the rectangle is correct (the minimum position is less
     * than the maximum position).
     *
     * @return whether the rectangle is valid
     */
    public boolean isValid() {
        return max.x > min.x && max.y > min.y;
    }
}
