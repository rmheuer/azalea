package com.github.rmheuer.azalea.render;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2i;

public interface Framebuffer extends SafeCloseable {
    Vector2i getSize();
}
