package org.libGML4J.input;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard {
    private static boolean[] current = new boolean[GLFW_KEY_LAST + 1];
    private static boolean[] previous = new boolean[GLFW_KEY_LAST + 1];

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

    public static void update() {
        previous = current.clone();
    }

    public static boolean isPressed(int key) {
        return key >= 0 && key < current.length && current[key];
    }

    public static boolean isJustPressed(int key) {
        return isPressed(key) && !previous[key];
    }

    public static boolean isJustReleased(int key) {
        return !isPressed(key) && previous[key];
    }
}