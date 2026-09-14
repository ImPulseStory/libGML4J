package org.libGML4J.input;

/*
 * Copyright (c) 2026 ImPulseStory
 * ... (полный текст MIT)
 */

import static org.lwjgl.glfw.GLFW.*;

/**
 * Singleton mouse input handler backed by GLFW.
 * <p>
 * Tracks cursor position, scroll offsets, button states, and dragging.
 * Because it is a singleton, all access goes through {@link #get()}.
 * <p>
 * Call the static callbacks from your window setup via
 * {@code glfwSetCursorPosCallback}, {@code glfwSetMouseButtonCallback}, and
 * {@code glfwSetScrollCallback}. Call {@link #endFrame()} at the end of every
 * frame to reset per-frame values.
 *
 * @author ImPulseStory
 */
public class Mouse {

    /** The single shared instance. */
    private static Mouse instance;

    /** Horizontal scroll offset for the current frame. */
    private double scrollX;

    /** Vertical scroll offset for the current frame. */
    private double scrollY;

    /** Current cursor x position. */
    private double xPos;

    /** Current cursor y position. */
    private double yPos;

    /** Cursor y position on the previous frame. */
    private double lastY;

    /** Cursor x position on the previous frame. */
    private double lastX;

    /** Button states: index 0 = left, 1 = middle, 2 = right. */
    private boolean mouseButtonPressed[] = new boolean[3];

    /** Whether any mouse button is currently held while the cursor moves. */
    private boolean isDragging;

    /**
     * Private constructor — use {@link #get()} instead.
     */
    private Mouse() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    /**
     * Returns the shared {@code Mouse} instance, creating it on first call.
     *
     * @return the singleton instance
     */
    public static Mouse get() {
        if (Mouse.instance == null) {
            Mouse.instance = new Mouse();
        }
        return Mouse.instance;
    }

    /**
     * GLFW cursor position callback.
     * <p>
     * Updates the current position, saves the previous one, and recalculates
     * the dragging state.
     *
     * @param window the GLFW window handle
     * @param x      the new cursor x position
     * @param y      the new cursor y position
     */
    public static void mousePosCallback(long window, double x, double y) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = x;
        get().yPos = y;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    /**
     * GLFW mouse button callback.
     * <p>
     * Updates the pressed state for the given button. Buttons beyond the
     * tracked range are ignored.
     *
     * @param window the GLFW window handle
     * @param button the button index (0 = left, 1 = middle, 2 = right)
     * @param action {@code GLFW_PRESS} or {@code GLFW_RELEASE}
     * @param mods   modifier key flags (unused)
     */
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button >= get().mouseButtonPressed.length) return;

        if (action == GLFW_PRESS) {
            get().mouseButtonPressed[button] = true;
        }  else if (action == GLFW_RELEASE) {
        get().mouseButtonPressed[button] = false;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
        }
    }

    /**
     * GLFW scroll callback.
     * <p>
     * Stores the scroll offsets for the current frame.
     *
     * @param window  the GLFW window handle
     * @param xOffset the horizontal scroll amount
     * @param yOffset the vertical scroll amount
     */
    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    /**
     * Resets per-frame values and saves the current position as the previous one.
     * <p>
     * Must be called once at the end of every frame.
     */
    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    /**
     * Returns the current cursor x position.
     *
     * @return the cursor x position
     */
    public static float getX() {
        return (float)get().xPos;
    }

    /**
     * Returns the current cursor y position.
     *
     * @return the cursor y position
     */
    public static float getY() {
        return (float)get().yPos;
    }

    /**
     * Returns the horizontal cursor movement since the last frame.
     *
     * @return the delta x
     */
    public static float getDx() {
        return (float)(get().lastX - get().xPos);
    }

    /**
     * Returns the vertical cursor movement since the last frame.
     *
     * @return the delta y
     */
    public static float getDy() {
        return (float)(get().lastY - get().yPos);
    }

    /**
     * Returns the horizontal scroll offset for the current frame.
     *
     * @return the scroll x
     */
    public static float getScrollX() {
        return (float)get().scrollX;
    }

    /**
     * Returns the vertical scroll offset for the current frame.
     *
     * @return the scroll y
     */
    public static float getScrollY() {
        return (float)get().scrollY;
    }

    /**
     * Checks whether the user is currently dragging (moving with a button held).
     *
     * @return {@code true} if dragging, {@code false} otherwise
     */
    public static boolean isDragging() {
        return get().isDragging;
    }

    /**
     * Checks whether the given mouse button is currently held down.
     *
     * @param button the button index (0 = left, 1 = middle, 2 = right)
     * @return {@code true} if held, {@code false} otherwise
     */
    public static boolean mouseButtonDown(int button) {
        if (button >= 0 && button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    /**
     * Returns the raw singleton instance (may be {@code null} if not yet created).
     *
     * @return the instance or {@code null}
     */
    public static Mouse getInstance() {
        return instance;
    }

    /**
     * Replaces the singleton instance.
     * <p>
     * Primarily useful for testing or manual control.
     *
     * @param instance the new instance
     */
    public static void setInstance(Mouse instance) {
        Mouse.instance = instance;
    }
}