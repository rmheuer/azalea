package com.github.rmheuer.azalea.serialization.graph;

public final class CharNode implements TextNode {
    private char value;

    public CharNode() {
        this((char) 0);
    }

    public CharNode(char value) {
        this.value = value;
    }

    @Override
    public String getAsString() {
        return String.valueOf(value);
    }

    @Override
    public char getAsChar() {
        return value;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
