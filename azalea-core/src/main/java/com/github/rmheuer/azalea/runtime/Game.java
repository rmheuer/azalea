package com.github.rmheuer.azalea.runtime;

import java.util.function.Supplier;

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

    /**
     * The entry point for the engine. This should be the only thing in the
     * main method.
     *
     * @param args command-line arguments provided to the game
     * @param gameConstructor constructor for the game instance
     */
    public static void launch(String[] args, Supplier<Game> gameConstructor) {
        if (EngineRuntime.restartForMacOS(args))
            return;

        Game game = gameConstructor.get();
        game.run();
    }

    private void run() {
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
