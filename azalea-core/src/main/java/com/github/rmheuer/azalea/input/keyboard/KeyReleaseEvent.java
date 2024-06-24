package com.github.rmheuer.azalea.input.keyboard;

/**
 * Event fired when a key is released (falling edge).
 */
public final class KeyReleaseEvent extends KeyEvent {
    /**
     * @param keyboard keyboard that produced the event
     * @param key key that was released
     */
    public KeyReleaseEvent(Keyboard keyboard, Key key) {
        super(keyboard, key);
    }

    @Override
    public String toString() {
        return "KeyReleaseEvent{key=" + getKey() + "}";
    }
}
