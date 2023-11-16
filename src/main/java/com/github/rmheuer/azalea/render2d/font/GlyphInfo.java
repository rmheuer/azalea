package com.github.rmheuer.azalea.render2d.font;

import com.github.rmheuer.azalea.render.texture.Texture2D;
import org.joml.Vector2f;

public final class GlyphInfo {
    private final Texture2D tex;
    private final Vector2f size;
    private final Vector2f offset;
    private final float xAdvance;
    private final Vector2f uvMin;
    private final Vector2f uvMax;

    public GlyphInfo(Texture2D tex, Vector2f size, Vector2f offset, float xAdvance, Vector2f uvMin, Vector2f uvMax) {
        this.tex = tex;
        this.size = size;
        this.offset = offset;
        this.xAdvance = xAdvance;
        this.uvMin = uvMin;
        this.uvMax = uvMax;
    }

    public Texture2D getTexture() {
        return tex;
    }

    public Vector2f getSize() {
        return size;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public float getXAdvance() {
        return xAdvance;
    }

    public Vector2f getUVMin() {
        return uvMin;
    }

    public Vector2f getUVMax() {
        return uvMax;
    }
}
