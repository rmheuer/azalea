package com.github.rmheuer.azalea.runtime;

import org.lwjgl.system.Configuration;

public final class EngineRuntime {
    /**
     * Enables all debug features of LWJGL. This is useful for ensuring
     * no native memory is being leaked, and for debugging errors when
     * interacting with native libraries through LWJGL.
     */
    public static void enableLWJGLDebug() {
        Configuration.DEBUG.set(true);
        Configuration.DEBUG_FUNCTIONS.set(true);
        Configuration.DEBUG_LOADER.set(true);
        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        Configuration.DEBUG_STACK.set(true);
        Configuration.DEBUG_STREAM.set(true);
    }

    private EngineRuntime() {
        throw new AssertionError();
    }
}
