package com.github.rmheuer.azalea.event;

/**
 * Defines the order in which event handlers are processed. Handlers with
 * higher priority are invoked earlier than handlers with lower priority.
 */
public enum EventPriority implements Comparable<EventPriority> {
    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4);

    private final int level;

    EventPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
