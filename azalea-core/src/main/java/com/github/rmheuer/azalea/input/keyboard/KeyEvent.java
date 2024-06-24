package com.github.rmheuer.azalea.input.keyboard;

/**
 * An event regarding a specific keyboard key.
 */
public abstract class KeyEvent extends KeyboardEvent {
    private final Key key;

    /**
     * @param keyboard keyboard that produced the event
     * @param key key that produced the event
     */
    public KeyEvent(Keyboard keyboard, Key key) {
        super(keyboard);
        this.key = key;
    }

    /**
     * Gets the key involved with this event.
     *
     * @return key
     */
    public Key getKey() {
        return key;
    }
}
