package com.github.rmheuer.azalea.render;

/**
 * The settings for creating a window.
 */
public final class WindowSettings {
    private final int width;
    private final int height;
    private final String title;
    private boolean resizable;
    private boolean fullScreen;

    /**
     * @param width desired width of the window in screen coordinates
     * @param height desired height of the window in screen coordinates
     * @param title title of the window
     */
    public WindowSettings(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        resizable = true;
        fullScreen = false;
    }

    /**
     * Sets whether the window should be resizable by the user.
     *
     * @param resizable whether the window should be resizable
     * @return this
     */
    public WindowSettings setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    /**
     * Sets whether the window should fill the monitor.
     *
     * @param fullScreen whether the window should be full-screen
     * @return this
     */
    public WindowSettings setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    /**
     * Gets the desired window width.
     *
     * @return width in screen coordinates
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the desired window height.
     *
     * @return height in screen coordinates
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the title of the window to create.
     *
     * @return window title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets whether the window should be resizable by the user.
     *
     * @return resizable
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Gets whether the window should be full-screen.
     *
     * @return full-screen
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public String toString() {
        return "WindowSettings{" +
                "width=" + width +
                ", height=" + height +
                ", title='" + title + '\'' +
                ", resizable=" + resizable +
                ", fullScreen=" + fullScreen +
                '}';
    }
}
