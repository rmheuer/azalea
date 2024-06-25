package com.github.rmheuer.azalea.serialization.graph;

public interface NumberNode extends DataNode {
    default float getAsFloat() { return getAsNumber().floatValue(); }

    Number getAsNumber();

    boolean isWholeNumber();
}
