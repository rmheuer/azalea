package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

public interface Mouse {
    Vector2d getCursorPos();
    boolean isButtonPressed(MouseButton button);
}
