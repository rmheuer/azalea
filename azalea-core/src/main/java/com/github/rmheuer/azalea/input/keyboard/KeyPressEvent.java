package com.github.rmheuer.azalea.input.keyboard;

/**
 * Event fired when a key is pressed down (the rising edge).
 */
public final class KeyPressEvent extends KeyEvent {
    /**
     * @param keyboard keyboard that produced the event
     * @param key key that was pressed
     */
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
