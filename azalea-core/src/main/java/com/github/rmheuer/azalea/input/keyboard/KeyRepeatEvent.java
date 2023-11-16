package com.github.rmheuer.azalea.input.keyboard;

public final class KeyRepeatEvent extends KeyEvent {
    public KeyRepeatEvent(Keyboard keyboard, Key key) {
        super(keyboard, key);
    }

    @Override
    public String toString() {
        return "KeyRepeatEvent{key=" + getKey() + "}";
    }
}
