package com.github.rmheuer.azalea.render;

public final class WindowSettings {
    private final int width;
    private final int height;
    private final String title;
    private boolean resizable;
    private boolean fullScreen;

    public WindowSettings(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        resizable = true;
        fullScreen = false;
    }

    public WindowSettings setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public WindowSettings setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public boolean isResizable() {
        return resizable;
    }

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
