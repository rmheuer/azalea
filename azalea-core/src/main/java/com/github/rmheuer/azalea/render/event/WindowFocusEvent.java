package com.github.rmheuer.azalea.render.event;

import com.github.rmheuer.azalea.render.Window;

public final class WindowFocusEvent extends WindowEvent {
    private final boolean focused;

    public WindowFocusEvent(Window window, boolean focused) {
        super(window);
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    @Override
    public String toString() {
        return "WindowFocusEvent{" +
                "focused=" + focused +
                '}';
    }
}
