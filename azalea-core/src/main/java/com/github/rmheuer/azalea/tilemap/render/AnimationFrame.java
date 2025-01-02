package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;

public final class AnimationFrame {
    private final BitmapRegion img;
    private final float time;
    private final boolean interpolateToNext;

    public AnimationFrame(BitmapRegion img, float time) {
        this(img, time, false);
    }

    public AnimationFrame(BitmapRegion img, float time, boolean interpolateToNext) {
        this.img = img;
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
}
