package com.github.rmheuer.azalea.render;

import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * Represents an RGBA color with {@code float} components.
 */
public final class ColorRGBA extends Vector4f {
    public static ColorRGBA transparent() { return new ColorRGBA(0, 0, 0, 0); }
    public static ColorRGBA black() { return new ColorRGBA(0, 0, 0); }
    public static ColorRGBA red() { return new ColorRGBA(1, 0, 0); }
    public static ColorRGBA green() { return new ColorRGBA(0, 1, 0); }
    public static ColorRGBA yellow() { return new ColorRGBA(1, 1, 0); }
    public static ColorRGBA blue() { return new ColorRGBA(0, 0, 1); }
    public static ColorRGBA magenta() { return new ColorRGBA(1, 0, 1); }
    public static ColorRGBA cyan() { return new ColorRGBA(0, 1, 1); }
    public static ColorRGBA white() { return new ColorRGBA(1, 1, 1); }

    /**
     * Helper to create a color from integer components. This creates a color
     * that is fully opaque.
     *
     * @param r red value from 0 to 255
     * @param g green value from 0 to 255
     * @param b blue value from 0 to 255
     * @return color
     */
    public static ColorRGBA rgba(int r, int g, int b) {
        return rgba(r, g, b, 255);
    }

    /**
     * Helper to create a color from integer components.
     *
     * @param r red value from 0 to 255
     * @param g green value from 0 to 255
     * @param b blue value from 0 to 255
     * @param a alpha value from 0 to 255. 0 is fully transparent, 255 is fully
     *          opaque
     * @return color
     */
    public static ColorRGBA rgba(int r, int g, int b, int a) {
        return new ColorRGBA(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    /**
     * Creates a new RGBA color with all components set to 0.
     */
    public ColorRGBA() {
    }

    /**
     * Creates a new RGBA color with given components from 0 to 1. This creates
     * a color with full opacity.
     *
     * @param r red component
     * @param g green component
     * @param b blue component
     */
    public ColorRGBA(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    /**
     * Creates a new RGBA color with given components from 0 to 1.
     *
     * @param r red component
     * @param g green component
     * @param b blue component
     * @param a alpha component
     */
    public ColorRGBA(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    /**
     * Creates a new RGBA color with components stored in a vector. This
     * creates a color with full opacity.
     *
     * @param v vector of components. X is red, Y is green, and Z is blue
     */
    public ColorRGBA(Vector3fc v) {
        this(v.x(), v.y(), v.z(), 1.0f);
    }

    /**
     * Creates a new RGBA color with components stored in a vector.
     *
     * @param v vector of components. X is red, Y is green, Z is blue, and W is
     *          alpha
     */
    public ColorRGBA(Vector4fc v) {
        super(v);
    }

    /**
     * Gets the red component of this color.
     *
     * @return red component from 0 to 1
     */
    public float getRed() {
        return x;
    }

    /**
     * Gets the green component of this color.
     *
     * @return green component from 0 to 1
     */
    public float getGreen() {
        return y;
    }

    /**
     * Gets the blue component of this color.
     *
     * @return blue component from 0 to 1
     */
    public float getBlue() {
        return z;
    }

    /**
     * Gets the alpha component of this color.
     *
     * @return alpha component from 0 to 1
     */
    public float getAlpha() {
        return w;
    }

    /**
     * Sets the red component of this color.
     *
     * @param r red component from 0 to 1
     * @return this
     */
    public ColorRGBA setRed(float r) {
        x = r;
        return this;
    }

    /**
     * Sets the green component of this color.
     *
     * @param g green component from 0 to 1
     * @return this
     */
    public ColorRGBA setGreen(float g) {
        y = g;
        return this;
    }

    /**
     * Sets the blue component of this color.
     *
     * @param b blue component from 0 to 1
     * @return this
     */
    public ColorRGBA setBlue(float b) {
        z = b;
        return this;
    }

    /**
     * Sets the alpha component of this color.
     *
     * @param a alpha component from 0 to 1
     * @return this
     */
    public ColorRGBA setAlpha(float a) {
        w = a;
        return this;
    }
}
