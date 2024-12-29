package com.github.rmheuer.azalea.voxel.render;

import com.github.rmheuer.azalea.math.CubeFace;

public final class FaceSet {
    private static final CubeFace[] FACES = CubeFace.values();

    private int mask;

    public void addAll() {
        mask = (1 << FACES.length) - 1;
    }

    public void remove(CubeFace face) {
        mask &= ~(1 << face.ordinal());
    }

    public boolean contains(CubeFace face) {
        return (mask & (1 << face.ordinal())) != 0;
    }
}
