package com.github.rmheuer.azalea.input.keyboard;

public final class CharTypeEvent extends KeyboardEvent {
    private final char c;

    public CharTypeEvent(Keyboard keyboard, char c) {
        super(keyboard);
        this.c = c;
    }

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
