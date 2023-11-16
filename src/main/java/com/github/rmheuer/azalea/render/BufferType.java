package com.github.rmheuer.azalea.render;

/**
 * Enumeration of the types of buffers which can be rendered into.
 */
public enum BufferType {
    /**
     * The color buffer. This stores the color of each pixel.
     */
    COLOR,

    /**
     * The depth buffer. This stores the distance from the camera of each
     * pixel.
     */
    DEPTH,

    /**
     * The stencil buffer. This can be used to selectively mask regions to
     * render into.
     */
    STENCIL
}
