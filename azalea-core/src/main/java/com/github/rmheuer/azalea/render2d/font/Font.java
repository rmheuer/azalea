package com.github.rmheuer.azalea.render2d.font;

import com.github.rmheuer.azalea.render2d.DrawList2D;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector4f;

public abstract class Font implements SafeCloseable {
    protected FontMetrics metrics = null;

    protected abstract GlyphInfo getGlyph(char c);

    public void draw(DrawList2D r, String text, float x, float y, Vector4f color) {
        for (char c : text.toCharArray()) {
            GlyphInfo glyph = getGlyph(c);
            if (glyph == null) {
                continue;
            }

            r.drawImage(
                    x + glyph.getOffset().x, y + glyph.getOffset().y,
                    glyph.getSize(),
                    glyph.getTexture(),
                    color,
                    glyph.getUVMin().x, glyph.getUVMin().y,
                    glyph.getUVMax().x, glyph.getUVMax().y
            );

            x += glyph.getXAdvance();
        }
    }

    public float textWidth(String text) {
        float x = 0;
        for (char c : text.toCharArray()) {
            GlyphInfo glyph = getGlyph(c);
            if (glyph == null) {
                continue;
            }
            x += glyph.getXAdvance();
        }
        return x;
    }

    public FontMetrics getMetrics() {
        return metrics;
    }

    @Override
    public void close() {}
}
