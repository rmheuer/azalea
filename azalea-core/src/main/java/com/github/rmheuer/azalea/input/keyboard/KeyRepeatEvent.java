package com.github.rmheuer.azalea.input.keyboard;

/**
 * Event fired when a key repeats. This is determined by the OS, and typically
 * happens while the key is held down. The rate this event is fired at is also
 * platform-specific.
 */
public final class KeyRepeatEvent extends KeyEvent {
    /**
     * @param keyboard keyboard that produced the event
     * @param key key that was repeated
     */
    public KeyRepeatEvent(Keyboard keyboard, Key key) {
        super(keyboard, key);
    }

    @Override
    public String toString() {
        return "KeyRepeatEvent{key=" + getKey() + "}";
    }
}
