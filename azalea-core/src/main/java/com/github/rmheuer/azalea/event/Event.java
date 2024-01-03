package com.github.rmheuer.azalea.event;

/**
 * Base class for an event.
 */
public abstract class Event {
    private boolean cancelled;

    public Event() {
        cancelled = false;
    }

    /**
     * Cancels the event. This prevents any event handlers after the current
     * one from receiving the event.
     */
    public void cancel() {
        cancelled = true;
    }

    /**
     * Gets whether this event has been cancelled.
     *
     * @return cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }
}
