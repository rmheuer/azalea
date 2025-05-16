package com.github.rmheuer.azalea.render;

import com.github.rmheuer.azalea.event.EventBus;
import com.github.rmheuer.azalea.input.keyboard.Keyboard;
import com.github.rmheuer.azalea.input.mouse.Mouse;
import com.github.rmheuer.azalea.render.opengl.OpenGLWindow;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2i;

/**
 * Represents the platform window graphics are rendered into. There can
 * currently only be one window at a time.
 */
public interface Window extends SafeCloseable {
    /**
     * Gets the renderer used to render into this window.
     *
     * @return renderer
     */
    Renderer getRenderer();

    /**
     * Gets whether the window should currently close. This will be true after
     * something has requested the window to close, such as pressing its close
     * button.
     *
     * @return whether the window should close
     */
    boolean shouldClose();

    /**
     * Updates the contents of the window. This will show the result of
     * rendering done using {@link #getRenderer()}. This may temporarily block
     * if VSync is enabled, in order to limit the frame rate.
     */
    void update();

    /**
     * Sets the window title. This is typically shown in the window's title
     * bar.
     *
     * @param title new window title
     */
    void setTitle(String title);

    /**
     * Gets the size of the window's framebuffer. This is not necessarily the
     * same as the window's size, since the framebuffer may be larger on high
     * DPI displays.
     *
     * @return size of the framebuffer
     */
    Vector2i getFramebufferSize();

    /**
     * Gets the size of the window.
     *
     * @return window size
     */
    Vector2i getSize();

    /**
     * Gets the window's keyboard input.
     *
     * @return keyboard input
     */
    Keyboard getKeyboard();

    /**
     * Gets the window's mouse input.
     *
     * @return mouse input
     */
    Mouse getMouse();

    /**
     * Registers this window with the {@code EventBus} so it can send events.
     *
     * @param bus event bus to receive events
     */
    void registerEvents(EventBus bus);

    /**
     * Gets whether this window is the currently focused window in the user's
     * desktop.
     *
     * @return whether the window is focused
     */
    boolean isFocused();

    /**
     * Creates a window compatible with the current platform.
     *
     * @param settings settings for the window to create
     * @return created window
     */
    static Window create(WindowSettings settings) {
        return new OpenGLWindow(settings);
    }
}
