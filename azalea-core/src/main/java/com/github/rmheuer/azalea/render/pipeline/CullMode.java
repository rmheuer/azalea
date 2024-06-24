package com.github.rmheuer.azalea.render.pipeline;

/**
 * Which faces should be culled.
 */
public enum CullMode {
    /** Faces facing towards the camera should be culled. **/
    FRONT,

    /** Faces facing away from the camera should be culled. **/
    BACK,

    /** Faces should never be culled. */
    OFF
}
