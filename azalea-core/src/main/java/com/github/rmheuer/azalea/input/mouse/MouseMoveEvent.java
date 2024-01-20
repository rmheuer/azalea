package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

/**
 * Event fired when the mouse cursor is moved.
 */
public final class MouseMoveEvent extends MouseEvent {
    private final Vector2d prevCursorPos;
    
    public MouseMoveEvent(Mouse mouse, Vector2d cursorPos, Vector2d prevCursorPos) {
        super(mouse, cursorPos);
	this.prevCursorPos = prevCursorPos;
    }

    /**
     * Gets the previous position of the mouse cursor within the window. This
     * is the position the cursor was in before the movement occured.
     *
     * @return previous cursor position in pixel coordinates
     */
    public Vector2d getPrevCursorPos() {
	return prevCursorPos;
    }

    /**
     * Gets the change in cursor position since the last movement.
     *
     * @return position delta in pixel coordinates
     */
    public Vector2d getCursorDelta() {
	return new Vector2d(getCursorPos()).sub(prevCursorPos);
    }

    @Override
    public String toString() {
        return "MouseMoveEvent{" +
                "cursorPos=" + getCursorPos() +
	        "prevCursorPos=" + prevCursorPos +
                '}';
    }
}
