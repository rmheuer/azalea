package com.github.rmheuer.azalea.render.pipeline;

/**
 * Which direction is considered the front of the face.
 */
public enum FaceWinding {
    /** Triangles wound clockwise are the front side. */
    CW_FRONT,

    /** Triangles wound counterclockwise are the front side. */
    CCW_FRONT
}
