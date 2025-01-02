package com.github.rmheuer.azalea.tilemap;

import com.github.rmheuer.azalea.utils.UnsafeUtil;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.HashMap;
import java.util.Map;

public final class InfiniteTilemap<T> extends Tilemap<T> {
    private final int chunkWidth, chunkHeight;

    public InfiniteTilemap(int chunkSize) {
        this(chunkSize, chunkSize);
    }

    public InfiniteTilemap(int chunkWidth, int chunkHeight) {
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
        createLayer(0);
    }

    @Override
    protected TilemapLayer<T> createLayerImpl(int zIndex) {
        return new Layer(zIndex);
    }

    private final class Chunk {
        private final T[] tiles;
        private int tileCount;

        public Chunk() {
            tiles = UnsafeUtil.newGenericArray(chunkWidth * chunkHeight);
            tileCount = 0;
        }

        private int tileIndex(int x, int y) {
            return x + y * chunkWidth;
        }

        public T getTile(int x, int y) {
            return tiles[tileIndex(x, y)];
        }

        public T setTile(int x, int y, T newTile) {
            int index = tileIndex(x, y);
            T prevTile = tiles[index];
            tiles[index] = newTile;

            if (newTile != null && prevTile == null)
                tileCount++;
            if (newTile == null && prevTile != null)
                tileCount--;

            return prevTile;
        }

        public boolean isEmpty() {
            return tileCount <= 0;
        }
    }

    private final class Layer extends TilemapLayer<T> {
        private final Map<Vector2i, Chunk> chunks = new HashMap<>();
        private final Vector2i getPos = new Vector2i(0, 0);

        private final Vector2i boundsMin = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        private final Vector2i boundsMax = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);

        public Layer(int zIndex) {
            super(zIndex);
        }

        @Override
        public T getTile(int x, int y) {
            int chunkX = Math.floorDiv(x, chunkWidth);
            int chunkY = Math.floorDiv(y, chunkHeight);
            Chunk chunk = chunks.get(getPos.set(chunkX, chunkY));
            if (chunk == null)
                return null;

            int relX = Math.floorMod(x, chunkWidth);
            int relY = Math.floorMod(y, chunkHeight);
            return chunk.getTile(relX, relY);
        }

        @Override
        public T setTile(int x, int y, T newTile) {
            int chunkX = Math.floorDiv(x, chunkWidth);
            int chunkY = Math.floorDiv(y, chunkHeight);
            Chunk chunk = chunks.get(getPos.set(chunkX, chunkY));
            if (chunk == null) {
                chunk = new Chunk();
                chunks.put(new Vector2i(chunkX, chunkY), chunk);
                expandBoundsToIncludeChunk(chunkX, chunkY);
            }

            int relX = Math.floorMod(x, chunkWidth);
            int relY = Math.floorMod(y, chunkHeight);
            T prevTile = chunk.setTile(relX, relY, newTile);
            if (chunk.isEmpty()) {
                chunks.remove(getPos); // getPos = (chunkX, chunkZ)
                recalcBounds();
            }

            if (newTile != prevTile)
                fireTileChanged(x, y, prevTile, newTile);

            return prevTile;
        }

        private void expandBoundsToIncludeChunk(int chunkX, int chunkY) {
            boundsMin.min(new Vector2i(chunkX * chunkWidth, chunkY * chunkHeight));
            boundsMax.max(new Vector2i((chunkX + 1) * chunkWidth, (chunkY + 1) * chunkHeight));
        }

        private void recalcBounds() {
            boundsMin.set(Integer.MAX_VALUE, Integer.MAX_VALUE);
            boundsMax.set(Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (Vector2i chunkPos : chunks.keySet()) {
                expandBoundsToIncludeChunk(chunkPos.x, chunkPos.y);
            }
        }

        @Override
        public Vector2ic getBoundsMin() {
            return boundsMin;
        }

        @Override
        public Vector2ic getBoundsMax() {
            return boundsMax;
        }
    }
}
