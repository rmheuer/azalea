package com.github.rmheuer.azalea.voxel.render;

public enum NeighborUpdateRule {
    FACES(false, false),
    FACES_AND_EDGES(true, false),
    FACES_EDGES_VERTICES(true, true);

    private final boolean includesEdges;
    private final boolean includesVertices;

    NeighborUpdateRule(boolean includesEdges, boolean includesVertices) {
        this.includesEdges = includesEdges;
        this.includesVertices = includesVertices;
    }

    public boolean includesEdges() {
        return includesEdges;
    }

    public boolean includesVertices() {
        return includesVertices;
    }
}
