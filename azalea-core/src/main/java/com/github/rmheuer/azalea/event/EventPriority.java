package com.github.rmheuer.azalea.event;

/**
 * Defines the order in which event handlers are processed. Handlers with
 * higher priority are invoked earlier than handlers with lower priority.
 */
public enum EventPriority {
    /** Processed first */
    FIRST(4),

    /** Processed after FIRST, but before NORMAL */
    EARLY(3),

    /** Processed after EARLY, but before LATER. This is the default priority. */
    NORMAL(2),

    /** Processed after NORMAL, but before LAST. */
    LATER(1),

    /** Processed last */
    LAST(0);

    private final int level;

    EventPriority(int level) {
        this.level = level;
    }

    /**
     * Gets the priority level. A higher level should be invoked earlier.
     *
     * @return priority level
     */
    // TODO: Refactor to use declaration order instead of level
    public int getLevel() {
        return level;
    }
}
