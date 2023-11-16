package com.github.rmheuer.azalea.render.event;

import com.github.rmheuer.azalea.render.Window;

public final class WindowCloseEvent extends WindowEvent {
    public WindowCloseEvent(Window window) {
        super(window);
    }

    @Override
    public String toString() {
        return "WindowCloseEvent{}";
    }
}
