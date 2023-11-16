package com.github.rmheuer.azalea.runtime;

import com.github.rmheuer.azalea.audio.AudioSystem;
import com.github.rmheuer.azalea.event.EventBus;
import com.github.rmheuer.azalea.render.*;
import org.joml.Vector2i;

public abstract class BaseGame extends Game {
    private final EventBus eventBus;
    private final Window window;
    private final Renderer renderer;
    private final FPSCounter fpsCounter;
    private final AudioSystem audioSystem;

    private ColorRGBA backgroundColor;

    public BaseGame(WindowSettings settings) {
        eventBus = new EventBus();
        window = Window.create(settings);
        window.registerEvents(eventBus);
        renderer = window.getRenderer();
        fpsCounter = new FPSCounter();
        audioSystem = new AudioSystem();

        backgroundColor = new ColorRGBA(0.2f, 0.2f, 0.2f);
    }

    protected abstract void tick(float dt);
    protected abstract void render(Renderer renderer);

    protected abstract void cleanUp();

    @Override
    protected final void update(float dt) {
        fpsCounter.beginFrame();

        tick(dt);

        Vector2i size = window.getFramebufferSize();
        renderer.setViewportRect(0, 0, size.x, size.y);
        renderer.setClearColor(backgroundColor);
        renderer.clear(BufferType.COLOR);
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

    public EventBus getEventBus() {
        return eventBus;
    }

    public Window getWindow() {
        return window;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }

    public AudioSystem getAudioSystem() {
        return audioSystem;
    }

    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(ColorRGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
