package com.github.rmheuer.azalea.render.event;

import com.github.rmheuer.azalea.event.Event;
import com.github.rmheuer.azalea.render.Window;

/**
 * An event that was produced by a window.
 */
public abstract class WindowEvent extends Event {
    private final Window window;

    public WindowEvent(Window window) {
        this.window = window;
    }

    public Window getWindow() {
        return window;
    }
}
