package com.github.rmheuer.azalea.serialization.graph;

public final class DoubleNode implements NumberNode {
    private double value;

    public DoubleNode() {
        this(0);
    }

    public DoubleNode(double value) {
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "D";
    }
}
