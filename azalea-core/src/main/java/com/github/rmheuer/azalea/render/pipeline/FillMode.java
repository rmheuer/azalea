package com.github.rmheuer.azalea.render.pipeline;

/**
 * How polygons should be rasterized
 */
public enum FillMode {
    /** The polygon should be fully filled in. */
    FILLED,

    /** Only the edges of the polygon should be rendered. */
    WIREFRAME
}
