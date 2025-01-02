package com.github.rmheuer.azalea.tilemap;

public interface TilemapListener<T> {
    void tileChanged(int x, int y, T prevTile, T newTile);
}
