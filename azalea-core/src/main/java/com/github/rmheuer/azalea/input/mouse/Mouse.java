package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

/**
 * Access to polling mouse input.
 */
public interface Mouse {
    /**
     * Gets the current position of the cursor within the window.
     *
     * @return cursor position in pixel coordinates
     */
    Vector2d getCursorPos();

    /**
     * Gets whether a mouse button is currently pressed.
     *
     * @return whether the button is pressed
     */
    boolean isButtonPressed(MouseButton button);

    /**
     * Sets whether the mouse cursor should be captured by the window.
     * This hides the cursor and prevents it from ever leaving the window.
     */
    void setCursorCaptured(boolean captured);
}
