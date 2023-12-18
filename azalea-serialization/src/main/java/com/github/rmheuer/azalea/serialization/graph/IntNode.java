package com.github.rmheuer.azalea.serialization.graph;

public final class IntNode implements NumberNode {
    private int value;

    public IntNode() {
        this(0);
    }

    public IntNode(int value) {
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "i";
    }
}
