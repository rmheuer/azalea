package com.github.rmheuer.azalea.input.keyboard;

public final class KeyPressEvent extends KeyEvent {
    public KeyPressEvent(Keyboard keyboard, Key key) {
        super(keyboard, key);
    }

    @Override
    public String toString() {
        return "KeyPressEvent{"
                + "key=" + getKey()
                + "}";
    }
}
