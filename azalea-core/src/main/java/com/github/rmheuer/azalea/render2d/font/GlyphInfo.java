package com.github.rmheuer.azalea.render2d.font;

import com.github.rmheuer.azalea.render.texture.Texture2D;
import org.joml.Vector2f;

/**
 * The information about one glyph of a font.
 */
public final class GlyphInfo {
    private final Texture2D tex;
    private final Vector2f size;
    private final Vector2f offset;
    private final float xAdvance;
    private final Vector2f uvMin;
    private final Vector2f uvMax;

    /**
     * @param tex the texture containing the rasterized glyph
     * @param size the size of the rectangle to render
     * @param offset the offset from the character origin to render the rectangle
     * @param xAdvance how far to move along the X axis after rendering this glyph
     * @param uvMin top-left UV coordinates of the texture region
     * @param uvMax bottom-right UV coordinates of the texture region
     */
    public GlyphInfo(Texture2D tex, Vector2f size, Vector2f offset, float xAdvance, Vector2f uvMin, Vector2f uvMax) {
        this.tex = tex;
        this.size = size;
        this.offset = offset;
        this.xAdvance = xAdvance;
        this.uvMin = uvMin;
        this.uvMax = uvMax;
    }

    /**
     * Gets the texture containing the rasterized glyph.
     *
     * @return glyph texture
     */
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
