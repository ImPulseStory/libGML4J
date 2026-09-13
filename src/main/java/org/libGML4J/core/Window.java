package org.libGML4J.core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static int width, height;
    private static String title;
    private static long glfwWindow;

    public Window() {
    }

    public static  long createWindow(int widthi, int heighti, String titlei) {
        width = widthi; height = heighti; title = titlei;

        // Init openGL
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configuration window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create Window
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // openGL context
        glfwMakeContextCurrent(glfwWindow);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Create Capabilities
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        return glfwWindow;
    }

    public static void update() {
        glfwPollEvents();
        glfwSwapBuffers(glfwWindow);
    }

    public static void clear(float r, float g, float b) {
        glClearColor(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void clear(float r, float g, float b, float a) {
        glClearColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String titlei) {
        title = titlei;
        glfwSetWindowTitle(glfwWindow, title);
    }

    public static long getWindow() {
        return glfwWindow;
    }

    public static void setSize(int widthi, int heighti) {
        width = widthi;
        height = heighti;
        glfwSetWindowSize(glfwWindow, width, height);
    }

    public static void destroy() {
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
    }

    public static boolean shouldClose() {
        return glfwWindowShouldClose(glfwWindow);
    }
}
