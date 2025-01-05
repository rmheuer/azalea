package com.github.rmheuer.azalea.tilemap;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public abstract class Tilemap<T> {
    protected final TreeMap<Integer, TilemapLayer<T>> layers;
    private TilemapLayer<T> layer0;

    private final List<TilemapListener<? super T>> listeners;

    public Tilemap() {
        layers = new TreeMap<>();
        listeners = new ArrayList<>();
    }

    protected abstract TilemapLayer<T> createLayerImpl(int zIndex);

    // Positive zIndex = in front, negative = behind
    public TilemapLayer<T> createLayer(int zIndex) {
        if (layers.containsKey(zIndex))
            throw new IllegalStateException("Layer already exists at Z index " + zIndex);

        TilemapLayer<T> layer = createLayerImpl(zIndex);
        layers.put(zIndex, layer);
        return layer;
    }

    public TilemapLayer<T> getLayer(int zIndex) {
        return layers.get(zIndex);
    }

    public Iterable<TilemapLayer<T>> getLayersBackToFront() {
        return layers.values();
    }

    private TilemapLayer<T> getLayer0() {
        if (layer0 == null)
            layer0 = getLayer(0);
        return layer0;
    }

    public T getTile(int x, int y) {
        return getLayer0().getTile(x, y);
    }

    public T setTile(int x, int y, T newTile) {
        return getLayer0().setTile(x, y, newTile);
    }

    public void addListener(TilemapListener<? super T> listener) {
        listeners.add(listener);
    }

    public void removeListener(TilemapListener<? super T> listener) {
        listeners.remove(listener);
    }

    protected void fireTileChanged(int x, int y, T prevTile, T newTile) {
        for (TilemapListener<? super T> listener : listeners) {
            listener.tileChanged(x, y, prevTile, newTile);
        }
    }
}
