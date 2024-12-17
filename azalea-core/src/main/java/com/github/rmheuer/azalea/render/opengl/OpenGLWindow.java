package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.WindowSettings;
import com.github.rmheuer.azalea.render.glfw.GlfwWindow;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public final class OpenGLWindow extends GlfwWindow {
    private OpenGLRenderer renderer;

    public OpenGLWindow(WindowSettings settings) {
        super(settings);
    }

    @Override
    protected void setContextWindowHints() {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    }

    @Override
    protected void initContext(boolean vSync) {
        glfwSwapInterval(vSync ? 1 : 0);
	
        GL.createCapabilities(true);
        renderer = new OpenGLRenderer(this);
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void close() {
        renderer.close();
        super.close();
    }
}
