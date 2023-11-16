package com.github.rmheuer.azalea.render.shader;

import com.github.rmheuer.azalea.utils.SafeCloseable;

public interface ShaderStage extends SafeCloseable {
    enum Type {
        VERTEX,
        FRAGMENT
    }

    Type getType();
}
