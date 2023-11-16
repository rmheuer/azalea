package com.github.rmheuer.azalea.input.mouse;

import com.github.rmheuer.azalea.event.Event;
import org.joml.Vector2d;

public abstract class MouseEvent extends Event {
    private final Mouse mouse;
    private final Vector2d cursorPos;

    public MouseEvent(Mouse mouse, Vector2d cursorPos) {
        this.mouse = mouse;
        this.cursorPos = cursorPos;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public Vector2d getCursorPos() {
        return cursorPos;
    }

    public double getX() {
        return cursorPos.x;
    }

    public double getY() {
        return cursorPos.y;
    }
}
