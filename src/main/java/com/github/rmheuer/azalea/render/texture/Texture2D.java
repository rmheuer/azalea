package com.github.rmheuer.azalea.render.texture;

import org.joml.Vector2f;

public interface Texture2D extends Texture, Texture2DRegion {
    void setData(Bitmap data);

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
