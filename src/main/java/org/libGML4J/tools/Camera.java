package org.libGML4J.tools;

/*
 * Copyright (c) 2026 ImPulseStory
 * ... (полный текст MIT)
 */

import org.libGML4J.core.Window;
import org.libGML4J.utils.Rect;

/**
 * A simple 2D camera that defines the visible region of the world.
 * <p>
 * The camera stores an offset ({@code x}, {@code y}) in world coordinates.
 * To convert world coordinates to screen coordinates, subtract the camera
 * offset. To keep the camera centered on an entity, call {@link #follow(Rect)}.
 * <p>
 * The camera does not render anything itself; it only provides the offset used
 * by rendering code (e.g. {@link org.libGML4J.tools.World} and
 * {@link org.libGML4J.graphics.Sprite}).
 *
 * @author ImPulseStory
 */
public class Camera {

    /** The camera's x offset in world coordinates. */
    public int x;

    /** The camera's y offset in world coordinates. */
    public int y;

    /**
     * Creates a new camera positioned at the origin.
     */
    public Camera() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Converts a world x-coordinate to a screen x-coordinate.
     *
     * @param x the world x-coordinate
     * @return the screen x-coordinate
     */
    public int applyX(int x) { return x - this.x; }

    /**
     * Converts a world y-coordinate to a screen y-coordinate.
     *
     * @param y the world y-coordinate
     * @return the screen y-coordinate
     */
    public int applyY(int y) { return y - this.y; }

    /**
     * Centers the camera on the given rectangle.
     * <p>
     * Typically called once per frame with the player's rectangle, so the
     * player stays in the middle of the screen.
     *
     * @param rect the rectangle to follow
     */
    public void follow(Rect rect) {
        this.x = rect.getCenterX() - Window.getWidth() / 2;
        this.y = rect.getCenterY() - Window.getHeight() / 2;
    }

    /**
     * Returns a copy of the given rectangle with its position converted to
     * screen coordinates.
     * <p>
     * The original rectangle is not modified.
     *
     * @param rect the rectangle in world coordinates
     * @return a new rectangle in screen coordinates
     */
    public Rect player(Rect rect) {
        Rect newi = rect.copy();
        newi.move(rect.getX() - this.x, rect.getY() - this.y);
        return newi;
    }

    /**
     * Returns the camera's x offset.
     *
     * @return the x offset
     */
    public int getX() { return x; }

    /**
     * Returns the camera's y offset.
     *
     * @return the y offset
     */
    public int getY() { return y; }
}