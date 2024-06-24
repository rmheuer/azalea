package com.github.rmheuer.azalea.render.shader;

import com.github.rmheuer.azalea.utils.SafeCloseable;

/** One stage of a shader program. */
public interface ShaderStage extends SafeCloseable {
    /** The type of stage. */
    enum Type {
        /** Stage that executes for every vertex in the mesh. */
        VERTEX,

        /** Stage that executes for every rasterized pixel. */
        FRAGMENT
    }

    Type getType();
}
