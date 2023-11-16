package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.utils.SafeCloseable;

public interface Texture extends SafeCloseable {
    enum Filter {
        NEAREST,
        LINEAR
    }

    void setMinFilter(Filter minFilter);
    void setMagFilter(Filter magFilter);
    default void setFilters(Filter filter) {
        setMinFilter(filter);
        setMagFilter(filter);
    }
}
