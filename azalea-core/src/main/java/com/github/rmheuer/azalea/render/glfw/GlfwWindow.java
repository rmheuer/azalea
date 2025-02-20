package com.github.rmheuer.azalea.render.glfw;

import com.github.rmheuer.azalea.event.EventBus;
import com.github.rmheuer.azalea.input.keyboard.*;
import com.github.rmheuer.azalea.input.mouse.*;
import com.github.rmheuer.azalea.render.Window;
import com.github.rmheuer.azalea.render.WindowSettings;
import com.github.rmheuer.azalea.render.event.WindowCloseEvent;
import com.github.rmheuer.azalea.render.event.WindowFramebufferSizeEvent;
import com.github.rmheuer.azalea.render.event.WindowSizeEvent;
import com.github.rmheuer.azalea.utils.BiMap;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Handles window creation and input handling using
 * <a href="https://www.glfw.org/">GLFW</a>.
 */
public abstract class GlfwWindow implements Window, Keyboard, Mouse {
    private static final BiMap<MouseButton, Integer> MOUSE_BUTTONS = new BiMap<>();
    private static final BiMap<Integer, Key> KEYS = new BiMap<>();

    private final long handle;
    private Vector2d cursorPos;

    /**
     * Creates a new GLFW window with the specified settings.
     *
     * @param settings settings for the window to create
     */
    public GlfwWindow(WindowSettings settings) {
        preInit();

        if (!glfwInit())
            throw new RuntimeException("Failed to init GLFW!");

        glfwDefaultWindowHints();
        setContextWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, settings.isResizable() ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        if (settings.isFullScreen()) {
            // FIXME: both glfwGetPrimaryMonitor() and glfwGetVideoMode() could fail
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            handle = glfwCreateWindow(vidMode.width(), vidMode.height(), settings.getTitle(), monitor, NULL);
        } else {
            handle = glfwCreateWindow(settings.getWidth(), settings.getHeight(), settings.getTitle(), NULL, NULL);
        }

        if (handle == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(handle);
        initContext(settings.isVSync());

        glfwShowWindow(handle);
        glfwFocusWindow(handle);

        // Poll once so cursor position is correct
        glfwPollEvents();
        cursorPos = getCurrentCursorPos();
    }

    protected abstract void preInit();

    /**
     * Sets the window hints for the graphics context.
     */
    protected abstract void setContextWindowHints();

    /**
     * Initializes the graphics context in the newly created window.
     */
    protected abstract void initContext(boolean vSync);

    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    @Override
    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    @Override
    public void setTitle(String title) {
        glfwSetWindowTitle(handle, title);
    }

    @Override
    public Vector2i getSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(handle, pWidth, pHeight);
            return new Vector2i(pWidth.get(0), pHeight.get(0));
        }
    }

    @Override
    public Vector2i getFramebufferSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetFramebufferSize(handle, pWidth, pHeight);
            return new Vector2i(pWidth.get(0), pHeight.get(0));
        }
    }

    @Override
    public Keyboard getKeyboard() {
        return this;
    }

    @Override
    public Mouse getMouse() {
        return this;
    }

    @Override
    public void registerEvents(EventBus bus) {
        glfwSetWindowCloseCallback(handle, (window) -> {
            bus.dispatchEvent(new WindowCloseEvent(this));
        });
        glfwSetWindowSizeCallback(handle, (window, width, height) -> {
            bus.dispatchEvent(new WindowSizeEvent(this, new Vector2i(width, height)));
        });
        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            bus.dispatchEvent(new WindowFramebufferSizeEvent(this, new Vector2i(width, height)));
        });
        glfwSetCursorPosCallback(handle, (window, x, y) -> {
            Vector2d newCursorPos = new Vector2d(x, y);
            bus.dispatchEvent(new MouseMoveEvent(this, newCursorPos, cursorPos));
            cursorPos = newCursorPos;
        });
        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            MouseButton mouseButton = getMouseButton(button);
            if (action == GLFW_PRESS) {
                bus.dispatchEvent(new MouseButtonPressEvent(this, cursorPos, mouseButton));
            } else if (action == GLFW_RELEASE) {
                bus.dispatchEvent(new MouseButtonReleaseEvent(this, cursorPos, mouseButton));
            }
        });
        glfwSetScrollCallback(handle, (window, scrollX, scrollY) -> {
            bus.dispatchEvent(new MouseScrollEvent(this, cursorPos, scrollX, scrollY));
        });
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            Key k = getKey(key);
            switch (action) {
                case GLFW_PRESS:
                    bus.dispatchEvent(new KeyPressEvent(this, k));
                    break;
                case GLFW_REPEAT:
                    bus.dispatchEvent(new KeyRepeatEvent(this, k));
                    break;
                case GLFW_RELEASE:
                    bus.dispatchEvent(new KeyReleaseEvent(this, k));
                    break;
            }
        });
        glfwSetCharCallback(handle, (window, c) -> {
            bus.dispatchEvent(new CharTypeEvent(this, (char) c));
        });
    }

    @Override
    public void close() {
        Callbacks.glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
        glfwTerminate();
    }

    @Override
    public boolean isKeyPressed(Key key) {
        return glfwGetKey(handle, getGlfwId(key)) == GLFW_PRESS;
    }

    @Override
    public Vector2d getCursorPos() {
        return cursorPos;
    }

    @Override
    public boolean isButtonPressed(MouseButton button) {
        return glfwGetMouseButton(handle, getGlfwId(button)) == GLFW_PRESS;
    }

    private Vector2d getCurrentCursorPos() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer x = stack.mallocDouble(1);
            DoubleBuffer y = stack.mallocDouble(1);
            glfwGetCursorPos(handle, x, y);

            return new Vector2d(x.get(0), y.get(0));
        }
    }

    @Override
    public void setCursorCaptured(boolean captured) {
        glfwSetInputMode(handle, GLFW_CURSOR, captured ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);

        // For some reason GLFW fires a mouse move event to the center of the
        // window if the cursor position is initially outside the window, so
        // prevent that by moving the cursor inside the window. For example,
        // this prevents a mouse-controlled camera from suddenly jumping on the
        // first frame.
        if (captured) {
            cursorPos = new Vector2d(getSize().div(2));
            glfwSetCursorPos(handle, cursorPos.x, cursorPos.y);
        }
    }

    /**
     * Gets the GLFW window handle.
     *
     * @return GLFW native window handle
     */
    public long getHandle() {
        return handle;
    }

    private static int getGlfwId(MouseButton button) {
        return MOUSE_BUTTONS.getBOrDefault(button, GLFW_MOUSE_BUTTON_LEFT);
    }

    private static MouseButton getMouseButton(int id) {
        return MOUSE_BUTTONS.getAOrDefault(id, MouseButton.UNKNOWN);
    }

    private static Key getKey(int id) {
        return KEYS.getBOrDefault(id, Key.UNKNOWN);
    }

    private static int getGlfwId(Key key) {
        return KEYS.getAOrDefault(key, GLFW_KEY_UNKNOWN);
    }

    static {
        MOUSE_BUTTONS.put(MouseButton.LEFT, GLFW_MOUSE_BUTTON_LEFT);
        MOUSE_BUTTONS.put(MouseButton.RIGHT, GLFW_MOUSE_BUTTON_RIGHT);
        MOUSE_BUTTONS.put(MouseButton.MIDDLE, GLFW_MOUSE_BUTTON_MIDDLE);

        KEYS.put(GLFW_KEY_UNKNOWN, Key.UNKNOWN);
        KEYS.put(GLFW_KEY_SPACE, Key.SPACE);
        KEYS.put(GLFW_KEY_APOSTROPHE, Key.APOSTROPHE);
        KEYS.put(GLFW_KEY_COMMA, Key.COMMA);
        KEYS.put(GLFW_KEY_MINUS, Key.MINUS);
        KEYS.put(GLFW_KEY_PERIOD, Key.PERIOD);
        KEYS.put(GLFW_KEY_SLASH, Key.SLASH);
        KEYS.put(GLFW_KEY_0, Key.ZERO);
        KEYS.put(GLFW_KEY_1, Key.ONE);
        KEYS.put(GLFW_KEY_2, Key.TWO);
        KEYS.put(GLFW_KEY_3, Key.THREE);
        KEYS.put(GLFW_KEY_4, Key.FOUR);
        KEYS.put(GLFW_KEY_5, Key.FIVE);
        KEYS.put(GLFW_KEY_6, Key.SIX);
        KEYS.put(GLFW_KEY_7, Key.SEVEN);
        KEYS.put(GLFW_KEY_8, Key.EIGHT);
        KEYS.put(GLFW_KEY_9, Key.NINE);
        KEYS.put(GLFW_KEY_SEMICOLON, Key.SEMICOLON);
        KEYS.put(GLFW_KEY_EQUAL, Key.EQUALS);
        KEYS.put(GLFW_KEY_A, Key.A);
        KEYS.put(GLFW_KEY_B, Key.B);
        KEYS.put(GLFW_KEY_C, Key.C);
        KEYS.put(GLFW_KEY_D, Key.D);
        KEYS.put(GLFW_KEY_E, Key.E);
        KEYS.put(GLFW_KEY_F, Key.F);
        KEYS.put(GLFW_KEY_G, Key.G);
        KEYS.put(GLFW_KEY_H, Key.H);
        KEYS.put(GLFW_KEY_I, Key.I);
        KEYS.put(GLFW_KEY_J, Key.J);
        KEYS.put(GLFW_KEY_K, Key.K);
        KEYS.put(GLFW_KEY_L, Key.L);
        KEYS.put(GLFW_KEY_M, Key.M);
        KEYS.put(GLFW_KEY_N, Key.N);
        KEYS.put(GLFW_KEY_O, Key.O);
        KEYS.put(GLFW_KEY_P, Key.P);
        KEYS.put(GLFW_KEY_Q, Key.Q);
        KEYS.put(GLFW_KEY_R, Key.R);
        KEYS.put(GLFW_KEY_S, Key.S);
        KEYS.put(GLFW_KEY_T, Key.T);
        KEYS.put(GLFW_KEY_U, Key.U);
        KEYS.put(GLFW_KEY_V, Key.V);
        KEYS.put(GLFW_KEY_W, Key.W);
        KEYS.put(GLFW_KEY_X, Key.X);
        KEYS.put(GLFW_KEY_Y, Key.Y);
        KEYS.put(GLFW_KEY_Z, Key.Z);
        KEYS.put(GLFW_KEY_LEFT_BRACKET, Key.LEFT_BRACKET);
        KEYS.put(GLFW_KEY_RIGHT_BRACKET, Key.RIGHT_BRACKET);
        KEYS.put(GLFW_KEY_GRAVE_ACCENT, Key.GRAVE_ACCENT);
        KEYS.put(GLFW_KEY_WORLD_1, Key.WORLD_1);
        KEYS.put(GLFW_KEY_WORLD_2, Key.WORLD_2);
        KEYS.put(GLFW_KEY_ESCAPE, Key.ESCAPE);
        KEYS.put(GLFW_KEY_ENTER, Key.ENTER);
        KEYS.put(GLFW_KEY_TAB, Key.TAB);
        KEYS.put(GLFW_KEY_BACKSPACE, Key.BACKSPACE);
        KEYS.put(GLFW_KEY_INSERT, Key.INSERT);
        KEYS.put(GLFW_KEY_DELETE, Key.DELETE);
        KEYS.put(GLFW_KEY_RIGHT, Key.RIGHT);
        KEYS.put(GLFW_KEY_LEFT, Key.LEFT);
        KEYS.put(GLFW_KEY_DOWN, Key.DOWN);
        KEYS.put(GLFW_KEY_UP, Key.UP);
        KEYS.put(GLFW_KEY_PAGE_UP, Key.PAGE_UP);
        KEYS.put(GLFW_KEY_PAGE_DOWN, Key.PAGE_DOWN);
        KEYS.put(GLFW_KEY_HOME, Key.HOME);
        KEYS.put(GLFW_KEY_END, Key.END);
        KEYS.put(GLFW_KEY_CAPS_LOCK, Key.CAPS_LOCK);
        KEYS.put(GLFW_KEY_SCROLL_LOCK, Key.SCROLL_LOCK);
        KEYS.put(GLFW_KEY_NUM_LOCK, Key.NUM_LOCK);
        KEYS.put(GLFW_KEY_PRINT_SCREEN, Key.PRINT_SCREEN);
        KEYS.put(GLFW_KEY_PAUSE, Key.PAUSE);
        KEYS.put(GLFW_KEY_F1, Key.F1);
        KEYS.put(GLFW_KEY_F2, Key.F2);
        KEYS.put(GLFW_KEY_F3, Key.F3);
        KEYS.put(GLFW_KEY_F4, Key.F4);
        KEYS.put(GLFW_KEY_F5, Key.F5);
        KEYS.put(GLFW_KEY_F6, Key.F6);
        KEYS.put(GLFW_KEY_F7, Key.F7);
        KEYS.put(GLFW_KEY_F8, Key.F8);
        KEYS.put(GLFW_KEY_F9, Key.F9);
        KEYS.put(GLFW_KEY_F10, Key.F10);
        KEYS.put(GLFW_KEY_F11, Key.F11);
        KEYS.put(GLFW_KEY_F12, Key.F12);
        KEYS.put(GLFW_KEY_F13, Key.F13);
        KEYS.put(GLFW_KEY_F14, Key.F14);
        KEYS.put(GLFW_KEY_F15, Key.F15);
        KEYS.put(GLFW_KEY_F16, Key.F16);
        KEYS.put(GLFW_KEY_F17, Key.F17);
        KEYS.put(GLFW_KEY_F18, Key.F18);
        KEYS.put(GLFW_KEY_F19, Key.F19);
        KEYS.put(GLFW_KEY_F20, Key.F20);
        KEYS.put(GLFW_KEY_F21, Key.F21);
        KEYS.put(GLFW_KEY_F22, Key.F22);
        KEYS.put(GLFW_KEY_F23, Key.F23);
        KEYS.put(GLFW_KEY_F24, Key.F24);
        KEYS.put(GLFW_KEY_F25, Key.F25);
        KEYS.put(GLFW_KEY_KP_0, Key.NUMPAD_ZERO);
        KEYS.put(GLFW_KEY_KP_1, Key.NUMPAD_ONE);
        KEYS.put(GLFW_KEY_KP_2, Key.NUMPAD_TWO);
        KEYS.put(GLFW_KEY_KP_3, Key.NUMPAD_THREE);
        KEYS.put(GLFW_KEY_KP_4, Key.NUMPAD_FOUR);
        KEYS.put(GLFW_KEY_KP_5, Key.NUMPAD_FIVE);
        KEYS.put(GLFW_KEY_KP_6, Key.NUMPAD_SIX);
        KEYS.put(GLFW_KEY_KP_7, Key.NUMPAD_SEVEN);
        KEYS.put(GLFW_KEY_KP_8, Key.NUMPAD_EIGHT);
        KEYS.put(GLFW_KEY_KP_9, Key.NUMPAD_NINE);
        KEYS.put(GLFW_KEY_KP_DECIMAL, Key.NUMPAD_DECIMAL);
        KEYS.put(GLFW_KEY_KP_DIVIDE, Key.NUMPAD_DIVIDE);
        KEYS.put(GLFW_KEY_KP_MULTIPLY, Key.NUMPAD_MULTIPLY);
        KEYS.put(GLFW_KEY_KP_SUBTRACT, Key.NUMPAD_SUBTRACT);
        KEYS.put(GLFW_KEY_KP_ADD, Key.NUMPAD_ADD);
        KEYS.put(GLFW_KEY_KP_ENTER, Key.NUMPAD_ENTER);
        KEYS.put(GLFW_KEY_KP_EQUAL, Key.NUMPAD_EQUALS);
        KEYS.put(GLFW_KEY_LEFT_SHIFT, Key.LEFT_SHIFT);
        KEYS.put(GLFW_KEY_LEFT_CONTROL, Key.LEFT_CONTROL);
        KEYS.put(GLFW_KEY_LEFT_ALT, Key.LEFT_ALT);
        KEYS.put(GLFW_KEY_LEFT_SUPER, Key.LEFT_SUPER);
        KEYS.put(GLFW_KEY_RIGHT_SHIFT, Key.RIGHT_SHIFT);
        KEYS.put(GLFW_KEY_RIGHT_CONTROL, Key.RIGHT_CONTROL);
        KEYS.put(GLFW_KEY_RIGHT_ALT, Key.RIGHT_ALT);
        KEYS.put(GLFW_KEY_RIGHT_SUPER, Key.RIGHT_SUPER);
        KEYS.put(GLFW_KEY_MENU, Key.MENU);
    }
}
