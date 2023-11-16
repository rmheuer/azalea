package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

public abstract class MouseButtonEvent extends MouseEvent {
    private final MouseButton button;

    public MouseButtonEvent(Mouse mouse, Vector2d cursorPos, MouseButton button) {
        super(mouse, cursorPos);
        this.button = button;
    }

    public MouseButton getButton() {
        return button;
    }
}
