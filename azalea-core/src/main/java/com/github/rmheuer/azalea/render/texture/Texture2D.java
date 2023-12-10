package com.github.rmheuer.azalea.render.texture;

import org.joml.Vector2f;

public interface Texture2D extends Texture, Texture2DRegion {
    void setData(BitmapRegion data);

    // Sets a section of the texture data on GPU, leaving the rest the same
    void setSubData(BitmapRegion data, int x, int y);

    @Override
    default Texture2D getSourceTexture() {
        return this;
    }

    @Override
    default Vector2f getRegionMinUV() {
        return new Vector2f(0, 0);
    }

    @Override
    default Vector2f getRegionMaxUV() {
        return new Vector2f(1, 1);
    }
}
