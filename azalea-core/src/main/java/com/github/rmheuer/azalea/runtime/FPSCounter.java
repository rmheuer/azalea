package com.github.rmheuer.azalea.runtime;

/**
 * Helper to count the frame rate the game runs at.
 */
public final class FPSCounter {
    private long frameStartTime;
    private float interval;

    private int countThisSecond;
    private float timer;
    private float rate;

    /**
     * Call to begin timing the frame.
     */
    public void beginFrame() {
        frameStartTime = System.nanoTime();
    }

    /**
     * Call to end timing the grame.
     *
     * @param dt time since the last frame
     * @return whether the frame rate was updated this frame
     */
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

    /**
     * Gets the time in seconds the last frame took.
     *
     * @return frame time
     */
    public float getFrameTime() {
        return interval;
    }

    /**
     * Gets the frame rate the game is running at.
     *
     * @return frames per second
     */
    public float getFrameRate() {
        return rate;
    }
}
