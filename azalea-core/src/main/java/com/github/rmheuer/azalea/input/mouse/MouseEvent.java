package com.github.rmheuer.azalea.input.mouse;

import com.github.rmheuer.azalea.event.Event;
import com.github.rmheuer.azalea.render.Window;
import org.joml.Vector2d;

/**
 * An event that was produced by a mouse.
 */
public abstract class MouseEvent extends Event {
    private final Mouse mouse;
    private final Vector2d cursorPos;

    public MouseEvent(Mouse mouse, Vector2d cursorPos) {
        this.mouse = mouse;
        this.cursorPos = cursorPos;
    }

    /**
     * Gets the mouse that produced this event.
     *
     * @return mouse
     */
    public Mouse getMouse() {
        return mouse;
    }

    /**
     * Gets the position of the mouse cursor within the window. This is in
     * pixel coordinates as returned by {@link Window#getSize()}.
     *
     * @return cursor position
     */
    public Vector2d getCursorPos() {
        return cursorPos;
    }

    /**
     * Gets the X position of the mouse cursor within the window. This is in
     * pixel coordinates as returned by {@link Window#getSize()}.
     *
     * @return cursor x position
     */
    public double getX() {
        return cursorPos.x;
    }

    /**
     * Gets the Y position of the mouse cursor within the window. This is in
     * pixel coordinates as returned by {@link Window#getSize()}.
     *
     * @return cursor y position
     */
    public double getY() {
        return cursorPos.y;
    }
}
