package com.github.rmheuer.azalea.input.keyboard;

import com.github.rmheuer.azalea.event.Event;

public abstract class KeyboardEvent extends Event {
    private final Keyboard keyboard;

    public KeyboardEvent(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }
}
