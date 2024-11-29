package com.github.rmheuer.azalea.render2d.font;

import com.github.rmheuer.azalea.render2d.DrawList2D;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * A font that can be used to render text.
 */
public abstract class Font implements SafeCloseable {
    protected FontMetrics metrics = null;

    /**
     * Gets the glyph info for a specific character.
     *
     * @param c character to get
     * @return info about the glyph
     */
    protected abstract GlyphInfo getGlyph(char c);

    /**
     * Draws text into a {@code DrawList2D}.
     *
     * @param r draw list to draw into
     * @param text the text to draw
     * @param x x position of the left side of the text
     * @param y y position of the baseline of the text
     * @param colorRGBA color to draw the text
     */
    public void draw(DrawList2D r, String text, float x, float y, int colorRGBA) {
        for (char c : text.toCharArray()) {
            GlyphInfo glyph = getGlyph(c);
            if (glyph == null) {
                continue;
            }

            Vector2f size = glyph.getSize();
            r.drawImage(
                    x + glyph.getOffset().x, y + glyph.getOffset().y,
                    size.x, size.y,
                    glyph.getTexture(),
                    colorRGBA,
                    glyph.getUVMin().x, glyph.getUVMin().y,
                    glyph.getUVMax().x, glyph.getUVMax().y
            );

            x += glyph.getXAdvance();
        }
    }

    /**
     * Gets the width a string of text would be rendered as.
     *
     * @param text text to get width
     * @return the width the text would be
     */
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

    /**
     * Gets the metrics of this font.
     *
     * @return metrics
     */
    public FontMetrics getMetrics() {
        return metrics;
    }

    @Override
    public void close() {}
}
