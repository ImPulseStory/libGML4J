package org.libGML4J.input;

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

import static org.lwjgl.glfw.GLFW.*;

/**
 * Static keyboard input handler backed by GLFW.
 * <p>
 * Tracks the current and previous state of every key, allowing you to check
 * whether a key is currently held, was just pressed, or was just released.
 * <p>
 * Call {@link #init(long)} once after creating the window, and
 * {@link #update()} at the beginning of each frame to keep the previous state
 * in sync.
 *
 * @author ImPulseStory
 */
public class Keyboard {

    /** Current state of every key: {@code true} if held down. */
    private static boolean[] current = new boolean[GLFW_KEY_LAST + 1];

    /** State of every key on the previous frame. */
    private static boolean[] previous = new boolean[GLFW_KEY_LAST + 1];

    /**
     * Registers the GLFW key callback for the given window.
     * <p>
     * This must be called once after the window is created, before the game
     * loop starts.
     *
     * @param window the GLFW window handle
     */
    public static void init(long window) {
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key >= 0 && key < current.length) {
                if (action == GLFW_PRESS) {
                    current[key] = true;
                } else if (action == GLFW_RELEASE) {
                    current[key] = false;
                }
            }
        });
    }

    /**
     * Updates the previous frame's key state.
     * <p>
     * Must be called once per frame, before any {@code isJust...} checks.
     */
    public static void update() {
        previous = current.clone();
    }

    /**
     * Checks whether the given key is currently held down.
     *
     * @param key the GLFW key code (e.g. {@code Keys.W})
     * @return {@code true} if the key is held, {@code false} otherwise
     */
    public static boolean isPressed(int key) {
        return key >= 0 && key < current.length && current[key];
    }

    /**
     * Checks whether the given key was pressed on this exact frame.
     * <p>
     * Returns {@code true} only on the frame the key transitions from up to
     * down. Useful for actions that should not repeat while held.
     *
     * @param key the GLFW key code
     * @return {@code true} if the key was just pressed, {@code false} otherwise
     */
    public static boolean isJustPressed(int key) {
        return isPressed(key) && !previous[key];
    }

    /**
     * Checks whether the given key was released on this exact frame.
     * <p>
     * Returns {@code true} only on the frame the key transitions from down to
     * up.
     *
     * @param key the GLFW key code
     * @return {@code true} if the key was just released, {@code false} otherwise
     */
    public static boolean isJustReleased(int key) {
        return !isPressed(key) && previous[key];
    }
}