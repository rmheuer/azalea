package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

public final class MouseButtonReleaseEvent extends MouseButtonEvent {
    public MouseButtonReleaseEvent(Mouse mouse, Vector2d cursorPos, MouseButton button) {
        super(mouse, cursorPos, button);
    }

    @Override
    public String toString() {
        return "MouseButtonReleaseEvent{" +
                "cursorPos=" + getCursorPos() + "," +
                "button=" + getButton() +
                '}';
    }
}
