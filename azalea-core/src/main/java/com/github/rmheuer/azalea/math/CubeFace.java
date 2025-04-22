package com.github.rmheuer.azalea.math;

import org.joml.Vector3i;

public enum CubeFace {
    POS_X(Axis.X, 1, 0, 0),
    NEG_X(Axis.X, -1, 0, 0),
    POS_Y(Axis.Y, 0, 1, 0),
    NEG_Y(Axis.Y, 0, -1, 0),
    POS_Z(Axis.Z, 0, 0, 1),
    NEG_Z(Axis.Z, 0, 0, -1);
    
    private static final CubeFace[] HORIZONTAL = {
            POS_X, NEG_X,
            POS_Y, NEG_Y
    };

    public static CubeFace[] horizontal() {
        return HORIZONTAL.clone();
    }

    static {
        POS_X.reverse = NEG_X;
        NEG_X.reverse = POS_X;
        POS_Y.reverse = NEG_Y;
        NEG_Y.reverse = POS_Y;
        POS_Z.reverse = NEG_Z;
        NEG_Z.reverse = POS_Z;
    }

    public final Axis axis;
    public final int x, y, z;
    private CubeFace reverse;

    CubeFace(Axis axis, int x, int y, int z) {
        this.axis = axis;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i getDirection() {
        return new Vector3i(x, y, z);
    }

    public CubeFace getReverse() {
        return reverse;
    }
}
