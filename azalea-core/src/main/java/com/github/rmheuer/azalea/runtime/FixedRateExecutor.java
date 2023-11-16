package com.github.rmheuer.azalea.runtime;

import java.util.function.Consumer;

public final class FixedRateExecutor {
    private final Consumer<Float> fn;
    private float period;
    private float unprocessedTime;

    public FixedRateExecutor(float period, Consumer<Float> fn) {
        this.period = period;
        this.fn = fn;
        unprocessedTime = 0;
    }

    public void update(float dt) {
        unprocessedTime += dt;
        while (unprocessedTime >= period) {
            fn.accept(period);
            unprocessedTime -= period;
        }
    }

    public float getPeriod() {
        return period;
    }

    public void setPeriod(float period) {
        this.period = period;
    }
}
