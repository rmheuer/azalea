package com.github.rmheuer.azalea.render2d.font;

/**
 * The sizing information of a font.
 */
public final class FontMetrics {
    private final float ascent;
    private final float descent;

    /**
     * @param ascent distance above the baseline the text extends
     * @param descent distance below the baseline the text extends
     */
    public FontMetrics(float ascent, float descent) {
        this.ascent = ascent;
        this.descent = descent;
    }

    /**
     * Gets the ascent of the font. This is the distance the text extends above
     * the baseline.
     *
     * @return ascent
     */
    public float getAscent() {
        return ascent;
    }

    /**
     * Gets the descent of the font. This is the distance the text extends
     * below the baseline.
     *
     * @return descent
     */
    public float getDescent() {
        return descent;
    }

    /**
     * Gets the total height of the text.
     *
     * @return {@code ascent + descent}
     */
    public float getHeight() {
        return ascent + descent;
    }
}
