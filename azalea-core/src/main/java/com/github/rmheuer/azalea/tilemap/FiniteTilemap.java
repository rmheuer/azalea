package com.github.rmheuer.azalea.tilemap;

import com.github.rmheuer.azalea.math.MathUtil;
import com.github.rmheuer.azalea.utils.UnsafeUtil;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public final class FiniteTilemap<T> extends Tilemap<T> {
    private final Vector2i size;

    public FiniteTilemap(int width, int height) {
        size = new Vector2i(width, height);
        createLayer(0);
    }

    @Override
    protected TilemapLayer<T> createLayerImpl(int zIndex) {
        return new Layer(zIndex);
    }

    private final class Layer extends TilemapLayer<T> {
        private final T[] tiles;

        public Layer(int zIndex) {
            super(zIndex);
            tiles = UnsafeUtil.newGenericArray(size.x * size.y);
        }

        private boolean outOfBounds(int x, int y) {
            return x < 0 || x >= size.x || y < 0 || y >= size.y;
        }

        private int tileIndex(int x, int y) {
            return x + y * size.x;
        }

        @Override
        public T getTile(int x, int y) {
            if (outOfBounds(x, y))
                return null;

            return tiles[tileIndex(x, y)];
        }

        @Override
        public T setTile(int x, int y, T newTile) {
            if (outOfBounds(x, y))
                return null;

            int index = tileIndex(x, y);
            T prevTile = tiles[index];
            tiles[index] = newTile;

            if (newTile != prevTile)
                fireTileChanged(x, y, prevTile, newTile);

            return prevTile;
        }

        @Override
        public Vector2ic getBoundsMin() {
            return MathUtil.VEC2I_ZERO;
        }

        @Override
        public Vector2ic getBoundsMax() {
            return size;
        }
    }
}
