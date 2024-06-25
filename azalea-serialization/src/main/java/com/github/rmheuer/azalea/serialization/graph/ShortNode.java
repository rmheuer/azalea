package com.github.rmheuer.azalea.serialization.graph;

public final class ShortNode implements NumberNode {
    private short value;

    public ShortNode() {
        this((short) 0);
    }

    public ShortNode(short value) {
        this.value = value;
    }

    @Override
    public Number getAsNumber() {
        return value;
    }

    @Override
    public boolean isWholeNumber() {
        return true;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "s";
    }
}
