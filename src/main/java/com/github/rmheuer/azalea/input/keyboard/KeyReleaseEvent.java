package com.github.rmheuer.azalea.input.keyboard;

public final class KeyReleaseEvent extends KeyEvent {
    public KeyReleaseEvent(Keyboard keyboard, Key key) {
        super(keyboard, key);
    }

    @Override
    public String toString() {
        return "KeyReleaseEvent{key=" + getKey() + "}";
    }
}
