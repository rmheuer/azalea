package com.github.rmheuer.azalea.tilemap;

import org.joml.Vector2ic;

public abstract class TilemapLayer<T> {
    private final int zIndex;

    public TilemapLayer(int zIndex) {
        this.zIndex = zIndex;
    }

    // Should return null if out of bounds
    public abstract T getTile(int x, int y);

    // Returns previous tile
    public abstract T setTile(int x, int y, T newTile);

    public abstract Vector2ic getBoundsMin();
    public abstract Vector2ic getBoundsMax();

    public int getZIndex() {
        return zIndex;
    }
}
