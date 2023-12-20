package com.github.rmheuer.azalea.runtime;

import java.util.function.Supplier;

public abstract class Game {
    private boolean running = false;

    protected abstract void update(float dt);
    protected abstract void close();

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

    public void stop() {
        running = false;
    }
}
