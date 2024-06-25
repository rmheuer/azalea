package com.github.rmheuer.azalea.serialization.graph;

public final class BooleanNode implements DataNode {
    private boolean value;

    public BooleanNode() {
        this(false);
    }

    public BooleanNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }
}
