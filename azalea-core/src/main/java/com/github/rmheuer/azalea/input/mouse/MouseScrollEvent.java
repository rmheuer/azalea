package com.github.rmheuer.azalea.input.mouse;

import org.joml.Vector2d;

public final class MouseScrollEvent extends MouseEvent {
    private final double scrollX;
    private final double scrollY;

    public MouseScrollEvent(Mouse mouse, Vector2d cursorPos, double scrollX, double scrollY) {
        super(mouse, cursorPos);
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }

    @Override
    public String toString() {
        return "MouseScrollEvent{" +
                "cursorPos=" + getCursorPos() +
                ", scrollX=" + scrollX +
                ", scrollY=" + scrollY +
                '}';
    }
}
