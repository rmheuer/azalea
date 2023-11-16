package com.github.rmheuer.azalea.input.keyboard;

public abstract class KeyEvent extends KeyboardEvent {
    private final Key key;

    public KeyEvent(Keyboard keyboard, Key key) {
        super(keyboard);
        this.key = key;
    }

    public Key getKey() {
        return key;
    }
}
