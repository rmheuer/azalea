package com.github.rmheuer.azalea.imgui;

import com.github.rmheuer.azalea.event.EventBus;
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
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.opengl.GL33C;

/**
 * ImGui backend implementation to render ImGui into an engine window.
 */
public final class ImGuiBackend implements SafeCloseable {
    private final ImGuiImplGlfw implGlfw;
    private final ImGuiImplGl3 implGl3;
    private final Keyboard maskedKeyboard;

    /**
     * @param window window to render into
     * @param eventBus event bus to receive input events from
     */
    public ImGuiBackend(Window window, EventBus eventBus) {
        eventBus.addListener(KeyboardEvent.class, EventPriority.FIRST, this::onKeyboardEvent);
        eventBus.addListener(MouseEvent.class, EventPriority.FIRST, this::onMouseEvent);

        implGlfw = new ImGuiImplGlfw();
        implGl3 = new ImGuiImplGl3();

        // For now, just assume we're running OpenGL so we can use the provided
        // backend implementation
        long windowHandle = ((GlfwWindow) window).getHandle();

        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        ImGui.styleColorsDark();

        implGlfw.init(windowHandle, true);
        implGl3.init();

        Keyboard winKb = window.getKeyboard();
        maskedKeyboard = (key) -> !ImGui.getIO().getWantCaptureKeyboard() && winKb.isKeyPressed(key);
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
        ImGui.newFrame();
    }

    public void endFrameAndRender() {
        ImGui.render();
        GL33C.glPolygonMode(GL33C.GL_FRONT_AND_BACK, GL33C.GL_FILL);
        implGl3.renderDrawData(ImGui.getDrawData());
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
        implGl3.dispose();
        implGlfw.dispose();
        ImGui.destroyContext();
    }
}
