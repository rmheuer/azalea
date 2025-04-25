package com.github.rmheuer.azalea.input.keyboard;

/**
 * The keys on a standard US keyboard.
 */
public enum Key {
    UNKNOWN("Unknown"),
    SPACE("Space"),
    APOSTROPHE("Apostrophe"),
    COMMA("Comma"),
    MINUS("Minus"),
    PERIOD("Period"),
    SLASH("Slash"),
    ZERO("Zero", 0),
    ONE("One", 1),
    TWO("Two", 2),
    THREE("Three", 3),
    FOUR("Four", 4),
    FIVE("Five", 5),
    SIX("Six", 6),
    SEVEN("Seven", 7),
    EIGHT("Eight", 8),
    NINE("Nine", 9),
    SEMICOLON("Semicolon"),
    EQUALS("Equals"),
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G"),
    H("H"),
    I("I"),
    J("J"),
    K("K"),
    L("L"),
    M("M"),
    N("N"),
    O("O"),
    P("P"),
    Q("Q"),
    R("R"),
    S("S"),
    T("T"),
    U("U"),
    V("V"),
    W("W"),
    X("X"),
    Y("Y"),
    Z("Z"),
    LEFT_BRACKET("Left Bracket"),
    RIGHT_BRACKET("Right Bracket"),
    GRAVE_ACCENT("Grave Accent"),
    WORLD_1("World 1"),
    WORLD_2("World 2"),
    ESCAPE("Escape"),
    ENTER("Enter"),
    TAB("Tab"),
    BACKSPACE("Backspace"),
    INSERT("Insert"),
    DELETE("Delete"),
    RIGHT("Right Arrow"),
    LEFT("Left Arrow"),
    DOWN("Down Arrow"),
    UP("Up Arrow"),
    PAGE_UP("Page Up"),
    PAGE_DOWN("Page Down"),
    HOME("Home"),
    END("End"),
    CAPS_LOCK("Caps Lock"),
    SCROLL_LOCK("Scroll Lock"),
    NUM_LOCK("Num Lock"),
    PRINT_SCREEN("Print Screen"),
    PAUSE("Pause"),
    F1("F1"),
    F2("F2"),
    F3("F3"),
    F4("F4"),
    F5("F5"),
    F6("F6"),
    F7("F7"),
    F8("F8"),
    F9("F9"),
    F10("F10"),
    F11("F11"),
    F12("F12"),
    F13("F13"),
    F14("F14"),
    F15("F15"),
    F16("F16"),
    F17("F17"),
    F18("F18"),
    F19("F19"),
    F20("F20"),
    F21("F21"),
    F22("F22"),
    F23("F23"),
    F24("F24"),
    F25("F25"),
    NUMPAD_ZERO("Numpad Zero", 0),
    NUMPAD_ONE("Numpad One", 1),
    NUMPAD_TWO("Numpad Two", 2),
    NUMPAD_THREE("Numpad Three", 3),
    NUMPAD_FOUR("Numpad Four", 4),
    NUMPAD_FIVE("Numpad Five", 5),
    NUMPAD_SIX("Numpad Six", 6),
    NUMPAD_SEVEN("Numpad Seven", 7),
    NUMPAD_EIGHT("Numpad Eight", 8),
    NUMPAD_NINE("Numpad Nine", 9),
    NUMPAD_DECIMAL("Numpad Decimal Point"),
    NUMPAD_DIVIDE("Numpad Divide"),
    NUMPAD_MULTIPLY("Numpad Multiply"),
    NUMPAD_SUBTRACT("Numpad Subtract"),
    NUMPAD_ADD("Numpad Add"),
    NUMPAD_ENTER("Numpad Enter"),
    NUMPAD_EQUALS("Numpad Equals"),
    LEFT_SHIFT("Left Shift"),
    LEFT_CONTROL("Left Control"),
    LEFT_ALT("Left Alt"),
    LEFT_SUPER("Left Super"),
    RIGHT_SHIFT("Right Shift"),
    RIGHT_CONTROL("Right Control"),
    RIGHT_ALT("Right Alt"),
    RIGHT_SUPER("Right Super"),
    MENU("Menu");

    private final String name;
    private final int number;

    Key(String name) {
        this(name, -1);
    }

    Key(String name, int number) {
        this.name = name;
        this.number = number;
    }

    /**
     * Gets the human-readable name of this key.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the numerical value of this key, or -1 if this is not a number key.
     *
     * @return number of key
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets whether this key is a number key. This includes both the standard
     * digit keys and the numpad.
     *
     * @return whether this is a number key
     */
    public boolean isNumberKey() {
        return number != -1;
    }
}
