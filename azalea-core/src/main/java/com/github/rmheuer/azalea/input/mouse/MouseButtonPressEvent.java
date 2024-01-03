package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

/**
 * Event fired when a mouse button is pressed (rising edge).
 */
public final class MouseButtonPressEvent extends MouseButtonEvent {
    public MouseButtonPressEvent(Mouse mouse, Vector2d cursorPos, MouseButton button) {
        super(mouse, cursorPos, button);
    }

    @Override
    public String toString() {
        return "MouseButtonPressEvent{" +
                "cursorPos=" + getCursorPos() + "," +
                "button=" + getButton() +
                '}';
    }
}
