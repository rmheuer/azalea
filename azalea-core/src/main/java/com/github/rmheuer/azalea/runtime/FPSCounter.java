package com.github.rmheuer.azalea.runtime;

public final class FPSCounter {
    private long frameStartTime;
    private float interval;

    private int countThisSecond;
    private float timer;
    private float rate;

    public void beginFrame() {
        frameStartTime = System.nanoTime();
    }

    public boolean endFrame(float dt) {
        long endTime = System.nanoTime();
        interval = (endTime - frameStartTime) / 1_000_000_000.0f;

        timer += dt;
        countThisSecond++;
        if (timer >= 1) {
            rate = countThisSecond;
            timer %= 1;
            countThisSecond = 0;
            return true;
        }
        return false;
    }

    public float getFrameTime() {
        return interval;
    }

    public float getFrameRate() {
        return rate;
    }
}
