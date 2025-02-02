package com.github.rmheuer.azalea.imgui;

import com.github.rmheuer.azalea.event.EventBus;
import com.github.rmheuer.azalea.event.EventListener;
import com.github.rmheuer.azalea.event.EventPriority;
import com.github.rmheuer.azalea.input.keyboard.Keyboard;
import com.github.rmheuer.azalea.input.keyboard.KeyboardEvent;
import com.github.rmheuer.azalea.input.mouse.MouseEvent;
import com.github.rmheuer.azalea.render.Window;
import com.github.rmheuer.azalea.render.glfw.GlfwWindow;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.glfw.ImGuiImplGlfw;

/**
 * ImGui backend implementation to render ImGui into an engine window.
 */
public final class ImGuiBackend implements EventListener, SafeCloseable {
    private final ImGuiImplGlfw implGlfw;
    private final ImGuiRenderBackend renderBackend;
    private final FrameTextures frameTextures;
    private final Keyboard maskedKeyboard;

    /**
     * @param window window to render into
     * @param eventBus event bus to receive input events from
     */
    public ImGuiBackend(Window window, EventBus eventBus) {
        eventBus.addListener(this);

        implGlfw = new ImGuiImplGlfw();

        // For now, just assume we're running OpenGL so we can use the provided
        // backend implementation
        long windowHandle = ((GlfwWindow) window).getHandle();

        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        ImGui.styleColorsDark();

        frameTextures = new FrameTextures();
        implGlfw.init(windowHandle, true);
        renderBackend = new ImGuiRenderBackend(window.getRenderer(), frameTextures);

        Keyboard winKb = window.getKeyboard();
        maskedKeyboard = (key) -> !ImGui.getIO().getWantCaptureKeyboard() && winKb.isKeyPressed(key);
    }

    @Override
    public void registerEventHandlers(EventBus.HandlerSet handlers) {
        handlers.register(KeyboardEvent.class, EventPriority.FIRST, this::onKeyboardEvent);
        handlers.register(MouseEvent.class, EventPriority.FIRST, this::onMouseEvent);
    }

    // Cancel input events that ImGui captured
    public void onKeyboardEvent(KeyboardEvent event) {
        if (ImGui.getIO().getWantCaptureKeyboard())
            event.cancel();
    }

    public void onMouseEvent(MouseEvent event) {
        if (ImGui.getIO().getWantCaptureMouse())
            event.cancel();
    }

    public void beginFrame() {
        implGlfw.newFrame();
        frameTextures.newFrame();
        ImGui.newFrame();
    }

    public void endFrameAndRender() {
        ImGui.render();
        renderBackend.renderDrawData(ImGui.getDrawData());
    }

    /**
     * Gets a keyboard that ignores keys captured by ImGui
     *
     * @return masked keyboard
     */
    public Keyboard getMaskedKeyboard() {
        return maskedKeyboard;
    }

    @Override
    public void close() {
        renderBackend.close();
        implGlfw.dispose();
        ImGui.destroyContext();
    }
}
