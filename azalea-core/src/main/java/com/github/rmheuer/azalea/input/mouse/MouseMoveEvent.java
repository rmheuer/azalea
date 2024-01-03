package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

/**
 * Event fired when the mouse cursor is moved.
 */
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
