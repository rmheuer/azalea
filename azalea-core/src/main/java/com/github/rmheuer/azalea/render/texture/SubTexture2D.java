package com.github.rmheuer.azalea.render.texture;

import org.joml.Vector2f;

final class SubTexture2D implements Texture2DRegion {
    private final Texture2D source;
    private final Vector2f uvMin;
    private final Vector2f uvMax;

    public SubTexture2D(Texture2D source, Vector2f uvMin, Vector2f uvMax) {
        this.source = source;
        this.uvMin = uvMin;
        this.uvMax = uvMax;
    }

    @Override
    public Texture2D getSourceTexture() {
        return source;
    }

    @Override
    public Vector2f getRegionTopLeftUV() {
        return new Vector2f(uvMin);
    }

    @Override
    public Vector2f getRegionBottomRightUV() {
        return new Vector2f(uvMax);
    }
}
