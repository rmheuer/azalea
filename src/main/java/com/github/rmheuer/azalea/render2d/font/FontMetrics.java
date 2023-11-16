package com.github.rmheuer.azalea.render2d.font;

public final class FontMetrics {
    private final float ascent;
    private final float descent;

    public FontMetrics(float ascent, float descent) {
	this.ascent = ascent;
	this.descent = descent;
    }

    public float getAscent() {
	return ascent;
    }

    public float getDescent() {
	return descent;
    }

    public float getHeight() {
	return ascent + descent;
    }
}
