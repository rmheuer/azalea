package com.github.rmheuer.azalea.runtime;

import java.util.function.Consumer;

/**
 * Helper to run a function at a fixed interval.
 */
public final class FixedRateExecutor {
    private final Consumer<Float> fn;
    private float period;
    private float unprocessedTime;
    private boolean enabled;

    /**
     * @param period time in seconds between each call
     * @param fn function to call
     */
    public FixedRateExecutor(float period, Consumer<Float> fn) {
        this.period = period;
        this.fn = fn;
        unprocessedTime = 0;
        enabled = true;
    }

    /**
     * Updates the executor. This should be called in your update() method.
     *
     * @param dt time since the last update in seconds
     */
    public void update(float dt) {
        unprocessedTime += dt;
        while (unprocessedTime >= period) {
            fn.accept(period);
            unprocessedTime -= period;
        }
    }

    /**
     * Gets the period between calls to the function.
     *
     * @return function period in seconds
     */
    public float getPeriod() {
        return period;
    }

    /**
     * Sets the period between calls to the function.
     *
     * @param period function period in seconds
     */
    public void setPeriod(float period) {
        this.period = period;
    }

    /**
     * Sets whether the periodic function should be called.
     *
     * @param enabled whether to call the periodic function
     */
    public void setEnabled(boolean enabled) {
        if (!this.enabled && enabled)
            unprocessedTime = 0;
        this.enabled = enabled;
    }
}
