package com.github.rmheuer.azalea.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiMouseCursor;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.system.Callback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class ImGuiImplGlfwFix extends ImGuiImplGlfw {
    private static final class DataExt extends Data {
        boolean installedCallbacks() {
            return installedCallbacks;
        }

        long window() {
            return window;
        }

        long[] mouseCursors() {
            return mouseCursors;
        }

        protected void freeCallback(final Callback cb) {
            System.out.println("Freeing: " + cb);
            if (cb != null) {
                cb.free();
            }
        }
        
        void restoreCallbacks(long window) {
            freeCallback(glfwSetWindowFocusCallback(window, prevUserCallbackWindowFocus));
            freeCallback(glfwSetCursorEnterCallback(window, prevUserCallbackCursorEnter));
            freeCallback(glfwSetCursorPosCallback(window, prevUserCallbackCursorPos));
            freeCallback(glfwSetMouseButtonCallback(window, prevUserCallbackMousebutton));
            freeCallback(glfwSetScrollCallback(window, prevUserCallbackScroll));
            freeCallback(glfwSetKeyCallback(window, prevUserCallbackKey));
            freeCallback(glfwSetCharCallback(window, prevUserCallbackChar));
            freeCallback(glfwSetMonitorCallback(prevUserCallbackMonitor));
            installedCallbacks = false;
            prevUserCallbackWindowFocus = null;
            prevUserCallbackCursorEnter = null;
            prevUserCallbackCursorPos = null;
            prevUserCallbackMousebutton = null;
            prevUserCallbackScroll = null;
            prevUserCallbackKey = null;
            prevUserCallbackChar = null;
            prevUserCallbackMonitor = null;
        }
    }

    @Override
    protected Data newData() {
        return new DataExt();
    }

    @Override
    public void shutdown() {
        final ImGuiIO io = ImGui.getIO();
        DataExt data = (DataExt) this.data;

        shutdownPlatformInterface();

        if (data.installedCallbacks()) {
            data.restoreCallbacks(data.window());
//            restoreCallbacks(data.window());
        }

        long[] mouseCursors = data.mouseCursors();
        for (int cursorN = 0; cursorN < ImGuiMouseCursor.COUNT; cursorN++) {
            long cursor = mouseCursors[cursorN];
            if (cursor != NULL)
                glfwDestroyCursor(mouseCursors[cursorN]);
        }

        io.setBackendPlatformName(null);
        this.data = null;
    }
}
