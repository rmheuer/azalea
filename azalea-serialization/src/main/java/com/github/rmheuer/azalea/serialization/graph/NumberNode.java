package com.github.rmheuer.azalea.serialization.graph;

public interface NumberNode extends DataNode {
    Number getAsNumber();

    boolean isWholeNumber();
}
