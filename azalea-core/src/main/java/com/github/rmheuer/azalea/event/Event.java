package com.github.rmheuer.azalea.event;

public abstract class Event {
    private boolean cancelled;

    public Event() {
        cancelled = false;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
