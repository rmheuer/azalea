package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

public final class MouseMoveEvent extends MouseEvent {
    public MouseMoveEvent(Mouse mouse, Vector2d cursorPos) {
        super(mouse, cursorPos);
    }

    @Override
    public String toString() {
        return "MouseMoveEvent{" +
                "cursorPos=" + getCursorPos() +
                '}';
    }
}
