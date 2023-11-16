package com.github.rmheuer.azalea.runtime;

import org.lwjgl.system.Configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class EngineRuntime {
    private static final String JVM_RESTARTED_PROP = "azalea.jvmRestarted";

    /**
     * Gets the PID of the currently running JVM.
     *
     * @return pid
     */
    public static int getPID() {
        String pidStr = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        return Integer.parseInt(pidStr);
    }

    /**
     * Gets the name of the main class. This must be called from the
     * main thread.
     *
     * @return name if it could be found, null otherwise
     */
    public static String getMainClassName() {
        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + getPID());
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) {
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                return null;
            }
        }
        return mainClass;
    }

    /**
     * Adds macOS support by restarting the JVM if it was not started
     * on the first thread. This is necessary due to window creation
     * needing to be on the first thread on macOS. You do not need to
     * call this method if your game does not open a window.
     *
     * Calling method should be the first thing in your main method.
     * If this method returns true, you must return from the main
     * method immediately. This method must be called from the main thread.
     *
     * @param args command-line arguments from the main() method
     * @return whether this JVM should exit immediately
     */
    public static boolean restartForMacOS(String[] args) {
        String osName = System.getProperty("os.name");

        if (!osName.startsWith("Mac") && !osName.startsWith("Darwin"))
            return false;

        int pid = getPID();
        String env = System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid);

        if (env != null && env.equals("1"))
            return false;

        if ("true".equals(System.getProperty(JVM_RESTARTED_PROP))) {
//            System.err.println("Failed to determine whether the JVM was started on the first thread");
            return false;
        }

        System.out.println("Running on MacOS, restarting JVM with -XstartOnFirstThread");

        String mainClass = getMainClassName();
        if (mainClass == null) {
            System.err.println("Failed to find main class");
            return false;
        }

        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String jvmPath = System.getProperty("java.home")
                + separator
                + "bin"
                + separator
                + "java";

        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

        List<String> jvmArgs = new ArrayList<>();
        jvmArgs.add(jvmPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_PROP + "=true");
        jvmArgs.addAll(inputArguments);
        jvmArgs.add("-cp");
        jvmArgs.add(classpath);
        jvmArgs.add(mainClass);
        jvmArgs.addAll(Arrays.asList(args));

        StringBuilder builder = new StringBuilder();
        for (String arg : jvmArgs) {
            builder.append(arg);
            builder.append(" ");
        }
        System.out.println("Starting new JVM with command: " + builder.toString());

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            int exit = process.waitFor();
            System.out.println("JVM exited with code " + exit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

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
