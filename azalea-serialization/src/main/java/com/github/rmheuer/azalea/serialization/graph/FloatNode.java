package com.github.rmheuer.azalea.serialization.graph;

public final class FloatNode implements NumberNode {
    private float value;

    public FloatNode() {
        this(0);
    }

    public FloatNode(float value) {
        this.value = value;
    }

    @Override
    public Number getAsNumber() {
        return value;
    }

    @Override
    public boolean isWholeNumber() {
        return false;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "f";
    }
}
