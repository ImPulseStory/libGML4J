package org.libGML4J.core;

/*
 * Copyright (c) 2026 ImPulseStory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Creates and manages a GLFW window with an OpenGL 3.3 context.
 * <p>
 * This is the entry point of every libGML4J application. Call
 * {@link #createWindow(int, int, String)} once at startup, then run your game
 * loop using {@link #shouldClose()} and {@link #update()}. When the loop ends,
 * call {@link #destroy()} to free resources.
 * <p>
 * The window is set up with:
 * <ul>
 *   <li>Blending enabled ({@code GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA})</li>
 *   <li>Vertical sync enabled ({@code glfwSwapInterval(1)})</li>
 *   <li>Resizable, not maximized by default</li>
 * </ul>
 *
 * @author ImPulseStory
 */
public class Window {

    /** The width of the window in pixels. */
    private static int width;

    /** The height of the window in pixels. */
    private static int height;

    /** The title displayed in the window's title bar. */
    private static String title;

    /** The GLFW window handle. */
    private static long glfwWindow;

    /**
     * Creates an empty {@code Window} instance.
     * <p>
     * All methods are static, so this constructor is optional.
     */
    private Window() {
    }

    /**
     * Creates a GLFW window with an OpenGL context and shows it.
     * <p>
     * Initializes GLFW, sets window hints, creates the window, makes the
     * OpenGL context current, enables VSync, initializes OpenGL capabilities,
     * and enables alpha blending.
     *
     * @param widthi  the window width in pixels
     * @param heighti the window height in pixels
     * @param titlei  the window title
     * @return the GLFW window handle
     * @throws IllegalStateException if GLFW cannot be initialized or the window
     *                               cannot be created
     */
    public static long createWindow(int widthi, int heighti, String titlei) {
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

        glfwSwapInterval(1);

        // Create Capabilities
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        return glfwWindow;
    }

    /**
     * Processes pending events and swaps the front and back buffers.
     * <p>
     * Must be called once at the end of every frame.
     */
    public static void update() {
        glfwPollEvents();
        glfwSwapBuffers(glfwWindow);
    }

    /**
     * Clears the window with the given RGB color.
     * <p>
     * The alpha channel is set to fully opaque.
     *
     * @param r the red component (0–255)
     * @param g the green component (0–255)
     * @param b the blue component (0–255)
     */
    public static void clear(float r, float g, float b) {
        glClearColor(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Clears the window with the given RGBA color.
     *
     * @param r the red component (0–255)
     * @param g the green component (0–255)
     * @param b the blue component (0–255)
     * @param a the alpha component (0–255)
     */
    public static void clear(float r, float g, float b, float a) {
        glClearColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Returns the window width in pixels.
     *
     * @return the width
     */
    public static int getWidth() {
        return width;
    }

    /**
     * Returns the window height in pixels.
     *
     * @return the height
     */
    public static int getHeight() {
        return height;
    }

    /**
     * Returns the window title.
     *
     * @return the title
     */
    public static String getTitle() {
        return title;
    }

    /**
     * Sets the window title.
     *
     * @param titlei the new title
     */
    public static void setTitle(String titlei) {
        title = titlei;
        glfwSetWindowTitle(glfwWindow, title);
    }

    /**
     * Returns the GLFW window handle.
     *
     * @return the window handle
     */
    public static long getWindow() {
        return glfwWindow;
    }

    /**
     * Sets both the width and height of the window.
     *
     * @param widthi  the new width in pixels
     * @param heighti the new height in pixels
     */
    public static void setSize(int widthi, int heighti) {
        width = widthi;
        height = heighti;
        glfwSetWindowSize(glfwWindow, width, height);
    }

    /**
     * Sets only the width of the window.
     *
     * @param widthi the new width in pixels
     */
    public static void setWidth(int widthi) {
        width = widthi;
        glfwSetWindowSize(glfwWindow, widthi, height);
    }

    /**
     * Sets only the height of the window.
     *
     * @param height1 the new height in pixels
     */
    public static void setHeight(int height1) {
        height = height1;
        glfwSetWindowSize(glfwWindow, width, height1);
    }

    /**
     * Destroys the given GLFW window and terminates GLFW.
     */
    public static void destroy() {
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
    }

    /**
     * Checks whether the window should close (e.g. the user clicked the close
     * button).
     *
     * @return {@code true} if the window should close
     */
    public static boolean shouldClose() {
        return glfwWindowShouldClose(glfwWindow);
    }
}