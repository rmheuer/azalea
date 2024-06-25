package com.github.rmheuer.azalea.serialization.graph;

public final class NullNode implements DataNode {
    public static final NullNode INSTANCE = new NullNode();

    private NullNode() {}

    @Override
    public String toString() {
        // Brackets are to distinguish between actually null value
        return "<null>";
    }
}
