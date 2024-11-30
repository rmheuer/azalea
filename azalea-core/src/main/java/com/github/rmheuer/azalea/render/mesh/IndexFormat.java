package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SizeOf;

public enum IndexFormat {
    UNSIGNED_SHORT(SizeOf.SHORT),
    UNSIGNED_INT(SizeOf.INT);

    private final int sizeOf;

    IndexFormat(int sizeOf) {
        this.sizeOf = sizeOf;
    }

    public int sizeOf() {
        return sizeOf;
    }
}
