package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.texture.Texture2DRegion;
import com.github.rmheuer.azalea.render2d.DrawList2D;
import com.github.rmheuer.azalea.tilemap.Tilemap;

public final class DefaultTileRenderer<T> implements TileRenderer<T> {
    private static final DefaultTileRenderer<?> INSTANCE = new DefaultTileRenderer<>();

    @SuppressWarnings("unchecked")
    public static <T> DefaultTileRenderer<T> getInstance() {
        return (DefaultTileRenderer<T>) INSTANCE;
    }

    @Override
    public void renderTile(DrawList2D draw, Texture2DRegion sprite, T tile, Tilemap<T> tilemap, int tileX, int tileY) {
        draw.drawImage(tileX, tileY, 1, 1, sprite);
    }
}
