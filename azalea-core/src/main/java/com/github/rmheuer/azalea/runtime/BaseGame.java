package com.github.rmheuer.azalea.runtime;

import com.github.rmheuer.azalea.audio.AudioSystem;
import com.github.rmheuer.azalea.event.EventBus;
import com.github.rmheuer.azalea.render.*;
import org.joml.Vector2i;

/**
 * Base game implementation that handles things most games use.
 */
public abstract class BaseGame extends Game {
    private final EventBus eventBus;
    private final Window window;
    private final Renderer renderer;
    private final FPSCounter fpsCounter;
    private final AudioSystem audioSystem;

    private ColorRGBA backgroundColor;

    /**
     * Initializes the engine systems.
     *
     * @param settings settings to create the game window
     */
    public BaseGame(WindowSettings settings) {
        eventBus = new EventBus();
        window = Window.create(settings);
        window.registerEvents(eventBus);
        renderer = window.getRenderer();
        fpsCounter = new FPSCounter();
        audioSystem = new AudioSystem();

        backgroundColor = new ColorRGBA(0.2f, 0.2f, 0.2f);
    }

    /**
     * Steps the game state.
     *
     * @param dt time since the last tick
     */
    protected abstract void tick(float dt);

    /**
     * Draws the game to the window.
     *
     * @param renderer renderer to render with
     */
    protected abstract void render(Renderer renderer);

    /**
     * Cleans up any resources the game is using. Called when the game ends.
     */
    protected abstract void cleanUp();

    @Override
    protected void update(float dt) {
        fpsCounter.beginFrame();

        tick(dt);

        Vector2i size = window.getFramebufferSize();
        renderer.setViewportRect(0, 0, size.x, size.y);
        renderer.setClearColor(backgroundColor);
        renderer.clear(BufferType.COLOR, BufferType.DEPTH);
        render(renderer);

        if (fpsCounter.endFrame(dt)) {
            System.out.printf("%.2f ms/frame, %.2f fps%n", fpsCounter.getFrameTime() * 1000, fpsCounter.getFrameRate());
        }
        window.update();
        if (window.shouldClose()) {
            stop();
        }
    }

    @Override
    protected final void close() {
        cleanUp();
        window.close();
        audioSystem.close();
    }

    /**
     * Gets the event bus used for event dispatch.
     *
     * @return event bus
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Gets the window the game is rendered into.
     *
     * @return window
     */
    public Window getWindow() {
        return window;
    }

    /**
     * Gets the window renderer.
     *
     * @return renderer
     */
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * Gets the frame rate counter.
     *
     * @return fps counter
     */
    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }

    /**
     * Gets the audio system.
     *
     * @return audio system
     */
    public AudioSystem getAudioSystem() {
        return audioSystem;
    }

    /**
     * Gets the current background color.
     *
     * @return background color
     */
    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the background color.
     *
     * @param backgroundColor new background color
     */
    public void setBackgroundColor(ColorRGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
