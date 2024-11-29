package com.github.rmheuer.azalea.render.mesh;

/**
 * The intended usage of uploaded mesh data. This helps select the most
 * optimal video memory to use for the data.
 */
public enum DataUsage {
    /** The data is uploaded rarely and rendered many times */
    STATIC,

    /** The data is uploaded frequently and rendered many times */
    DYNAMIC,

    /** The data is uploaded frequently and rendered once */
    STREAM
}
