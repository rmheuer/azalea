package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

/**
 * An event regarding a specific mouse button.
 */
public abstract class MouseButtonEvent extends MouseEvent {
    private final MouseButton button;

    public MouseButtonEvent(Mouse mouse, Vector2d cursorPos, MouseButton button) {
        super(mouse, cursorPos);
        this.button = button;
    }

    /**
     * Gets the button involved with this event.
     *
     * @return button
     */
    public MouseButton getButton() {
        return button;
    }
}
