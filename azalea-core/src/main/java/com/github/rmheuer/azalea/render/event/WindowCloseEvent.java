package com.github.rmheuer.azalea.render.event;

import com.github.rmheuer.azalea.render.Window;

/**
 * Event fired when the user requests a window to close. This could be by
 * pressing its close button or any other way to close a window.
 */
public final class WindowCloseEvent extends WindowEvent {
    public WindowCloseEvent(Window window) {
        super(window);
    }

    @Override
    public String toString() {
        return "WindowCloseEvent{}";
    }
}
