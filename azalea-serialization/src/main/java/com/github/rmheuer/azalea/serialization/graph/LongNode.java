package com.github.rmheuer.azalea.serialization.graph;

public final class LongNode implements NumberNode {
    private long value;

    public LongNode() {
        this(0);
    }

    public LongNode(long value) {
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

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "L";
    }
}
