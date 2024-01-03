package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

/**
 * Access to polling mouse input.
 */
public interface Mouse {
    Vector2d getCursorPos();
    boolean isButtonPressed(MouseButton button);
}
