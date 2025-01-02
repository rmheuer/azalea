package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.texture.Texture2DRegion;

public final class TileSprite {
    private final Texture2DRegion texRegion;

    TileSprite(Texture2DRegion texRegion) {
        this.texRegion = texRegion;
    }

    Texture2DRegion getTexRegion() {
        return texRegion;
    }
}
