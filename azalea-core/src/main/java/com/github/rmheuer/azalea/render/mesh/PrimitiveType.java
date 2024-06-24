package com.github.rmheuer.azalea.render.mesh;

/**
 * Type of primitive shape the GPU should assemble vertices into.
 */
public enum PrimitiveType {
    /** Each vertex is its own individual point. */
    POINTS,

    /** The points are connected with lines. */
    LINE_STRIP,

    /**
     * The points are connected with lines, and the last is connected to the
     * first.
     */
    LINE_LOOP,

    /** Each set of two vertices form the endpoints of a line. */
    LINES,

    /**
     * Each vertex forms a triangle with itself and the previous two vertices.
     */
    TRIANGLE_STRIP,

    /**
     * Each vertex forms a triangle with itself, the previous vertex, and the
     * first vertex.
     */
    TRIANGLE_FAN,

    /** Each set of three vertices form an individual triangle. */
    TRIANGLES
}
