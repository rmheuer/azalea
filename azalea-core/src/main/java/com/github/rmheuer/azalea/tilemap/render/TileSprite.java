package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.texture.Texture2DRegion;

public final class TileSprite {
    private final Texture2DRegion texRegion;
    private final TilemapRenderer.Animation animation;

    TileSprite(Texture2DRegion texRegion, TilemapRenderer.Animation animation) {
        this.texRegion = texRegion;
        this.animation = animation;
    }

    Texture2DRegion getTexRegion() {
        return texRegion;
    }

    TilemapRenderer.Animation getAnimation() {
        return animation;
    }
}
