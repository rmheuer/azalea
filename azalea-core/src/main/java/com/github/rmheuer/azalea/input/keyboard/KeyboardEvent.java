package com.github.rmheuer.azalea.input.keyboard;

import com.github.rmheuer.azalea.event.Event;

/**
 * An event that was produced by a keyboard.
 */
public abstract class KeyboardEvent extends Event {
    private final Keyboard keyboard;

    /**
     * @param keyboard keyboard that produced the event
     */
    public KeyboardEvent(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    /**
     * Gets the keyboard the event came from.
     *
     * @return keyboard
     */
    public Keyboard getKeyboard() {
        return keyboard;
    }
}
