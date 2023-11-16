package com.github.rmheuer.azalea.render.event;

import com.github.rmheuer.azalea.render.Window;
import org.joml.Vector2i;

public final class WindowFramebufferSizeEvent extends WindowEvent {
    private final Vector2i size;

    public WindowFramebufferSizeEvent(Window window, Vector2i size) {
        super(window);
        this.size = size;
    }

    public Vector2i getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "WindowFramebufferSizeEvent{" +
                "size=" + size +
                '}';
    }
}
