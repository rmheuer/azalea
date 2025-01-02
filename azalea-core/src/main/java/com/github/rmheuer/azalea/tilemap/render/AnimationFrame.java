package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.texture.Bitmap;
import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.utils.SafeCloseable;

public final class AnimationFrame implements SafeCloseable {
    private final Bitmap img;
    private final float time;
    private final boolean interpolateToNext;

    public AnimationFrame(BitmapRegion img, float time) {
        this(img, time, false);
    }

    public AnimationFrame(BitmapRegion img, float time, boolean interpolateToNext) {
        this.img = img.copied();
        this.time = time;
        this.interpolateToNext = interpolateToNext;
    }

    public BitmapRegion getImg() {
        return img;
    }

    public float getTime() {
        return time;
    }

    public boolean isInterpolateToNext() {
        return interpolateToNext;
    }

    @Override
    public void close() {
        img.close();
    }
}
