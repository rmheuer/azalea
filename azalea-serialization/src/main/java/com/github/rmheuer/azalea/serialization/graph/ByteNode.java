package com.github.rmheuer.azalea.serialization.graph;

public final class ByteNode implements NumberNode {
    private byte value;

    public ByteNode() {
        this((byte) 0);
    }

    public ByteNode(byte value) {
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

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "b";
    }
}
