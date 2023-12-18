package com.github.rmheuer.azalea.serialization.graph;

public final class StringNode implements TextNode {
    private String value;

    public StringNode() {
        this("");
    }

    public StringNode(String value) {
        this.value = value;
    }

    @Override
    public String getAsString() {
        return value;
    }

    @Override
    public char getAsChar() {
        if (value.length() == 1)
            return value.charAt(0);
        throw new ClassCastException("Cannot get " + this + " as char");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return '"' + value + '"';
    }
}
