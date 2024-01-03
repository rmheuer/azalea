package com.github.rmheuer.azalea.input.keyboard;

/**
 * Event fired when a character is typed on the keyboard.
 */
public final class CharTypeEvent extends KeyboardEvent {
    private final char c;

    /**
     * @param keyboard keyboard that produced the event
     * @param c character that was typed
     */
    public CharTypeEvent(Keyboard keyboard, char c) {
        super(keyboard);
        this.c = c;
    }

    /**
     * Gets the character that was typed.
     *
     * @return typed character
     */
    public char getChar() {
        return c;
    }

    @Override
    public String toString() {
        return "CharTypeEvent{" +
                "c=" + c +
                "}";
    }
}
