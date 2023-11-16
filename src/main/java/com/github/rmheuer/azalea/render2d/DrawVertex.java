package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.render.texture.Texture2DRegion;
import org.joml.Vector3f;
import org.joml.Vector4f;

// Note: Not an actual vertex type!
//       This is the intermediate representation of the vertices
//       before being batched.
public final class DrawVertex {
    private final Vector3f pos;
    private final float u, v;
    private final Vector4f color;
    private final Texture2DRegion tex;

    public DrawVertex(Vector3f pos, float u, float v, Vector4f color, Texture2DRegion tex) {
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

    public Vector4f getColor() {
        return color;
    }

    public Texture2DRegion getTex() {
        return tex;
    }
}
