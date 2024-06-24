package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.render.texture.Texture2DRegion;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * The intermediate representation of vertices in a {@link DrawList2D} before
 * being batched for rendering.
 */
final class DrawVertex {
    private final Vector3f pos;
    private final float u, v;
    private final int color; // RGBA
    private final Texture2DRegion tex;

    public DrawVertex(Vector3f pos, float u, float v, int color, Texture2DRegion tex) {
        this.pos = pos;
        this.u = u;
        this.v = v;
        this.color = color;
        this.tex = tex;
    }

    public Vector3f getPos() {
        return pos;
    }

    public float getU() {
        return u;
    }

    public float getV() {
        return v;
    }

    public int getColor() {
        return color;
    }

    public Texture2DRegion getTex() {
        return tex;
    }
}
