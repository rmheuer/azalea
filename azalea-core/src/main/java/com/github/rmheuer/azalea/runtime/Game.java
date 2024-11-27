package com.github.rmheuer.azalea.runtime;

/**
 * Base class for a game.
 */
public abstract class Game {
    private boolean running = false;

    /**
     * Called repeatedly while the game is running.
     *
     * @param dt time since the last update
     */
    protected abstract void update(float dt);

    /**
     * Called when the game closes, to clean up any resources.
     */
    protected abstract void close();

    public void run() {
        running = true;
        long prevTime = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            float dt = (now - prevTime) / 1_000_000_000.0f;
            prevTime = now;

            update(dt);
        }

        close();
    }

    /**
     * Tells the game to stop running.
     */
    public void stop() {
        running = false;
    }
}
