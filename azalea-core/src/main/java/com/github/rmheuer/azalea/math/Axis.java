package com.github.rmheuer.azalea.math;

public enum Axis {
    X,
    Y,
    Z;

    /**
     * Gets the index of this axis, as used by
     * {@link org.joml.Vector3f#get(int)}.
     *
     * @return axis index: 0 for X, 1 for Y, 2 for Z
     */
    public int getIndex() {
        return ordinal();
    }
}
