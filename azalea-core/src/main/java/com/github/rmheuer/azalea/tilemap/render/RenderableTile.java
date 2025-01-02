package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.tilemap.Tilemap;

public interface RenderableTile<T extends RenderableTile<T>> {
    TileSprite getSprite(Tilemap<T> tilemap, int tileX, int tileY);
}
