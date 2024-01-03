package com.github.rmheuer.azalea.render.event;

import com.github.rmheuer.azalea.render.Window;
import org.joml.Vector2i;

/**
 * Event fired when a window is resized.
 */
public final class WindowSizeEvent extends WindowEvent {
    private final Vector2i size;

    public WindowSizeEvent(Window window, Vector2i size) {
        super(window);
        this.size = size;
    }

    /**
     * New size for the window as returned by {@link Window#getSize()}
     *
     * @return new window size in screen pixels
     */
    public Vector2i getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "WindowSizeEvent{" +
                "size=" + size +
                '}';
    }
}
