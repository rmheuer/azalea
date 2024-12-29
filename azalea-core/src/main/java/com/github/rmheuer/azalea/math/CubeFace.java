package com.github.rmheuer.azalea.math;

import org.joml.Vector3i;

public enum CubeFace {
    POS_X(1, 0, 0),
    NEG_X(-1, 0, 0),
    POS_Y(0, 1, 0),
    NEG_Y(0, -1, 0),
    POS_Z(0, 0, 1),
    NEG_Z(0, 0, -1);

    public final int x, y, z;

    CubeFace(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i getDirection() {
        return new Vector3i(x, y, z);
    }
}
