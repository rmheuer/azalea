package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.texture.Texture2DRegion;
import com.github.rmheuer.azalea.render2d.DrawList2D;
import com.github.rmheuer.azalea.tilemap.Tilemap;

@FunctionalInterface
public interface TileRenderer<T> {
    void renderTile(DrawList2D draw, Texture2DRegion sprite, T tile, Tilemap<T> tilemap, int tileX, int tileY);
}
