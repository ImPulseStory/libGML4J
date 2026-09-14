package org.libGML4J.utils;

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

/**
 * A simple 2D rectangle with integer coordinates, used for positions, sizes,
 * and collision detection.
 * <p>
 * The rectangle is defined by its top-left corner ({@code x}, {@code y}) and
 * its dimensions ({@code width}, {@code height}). All coordinates grow
 * downwards, following screen-space conventions.
 *
 * @author ImPulseStory
 */
public class Rect {
    /** The x-coordinate of the rectangle's top-left corner. */
    private int x;

    /** The y-coordinate of the rectangle's top-left corner. */
    private int y;

    /** The width of the rectangle in pixels. */
    private int width;

    /** The height of the rectangle in pixels. */
    private int height;

    /**
     * Creates a new rectangle with the given position and size.
     *
     * @param x      the x-coordinate of the top-left corner
     * @param y      the y-coordinate of the top-left corner
     * @param width  the width of the rectangle in pixels
     * @param height the height of the rectangle in pixels
     */
    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the x-coordinate of the rectangle's top-left corner.
     *
     * @return the x-coordinate
     */
    public int getX() { return x; }

    /**
     * Sets the x-coordinate of the rectangle's top-left corner.
     *
     * @param x the new x-coordinate
     */
    public void setX(int x) { this.x = x; }

    /**
     * Returns the y-coordinate of the rectangle's top-left corner.
     *
     * @return the y-coordinate
     */
    public int getY() { return y; }

    /**
     * Sets the y-coordinate of the rectangle's top-left corner.
     *
     * @param y the new y-coordinate
     */
    public void setY(int y) { this.y = y; }

    /**
     * Returns the width of the rectangle.
     *
     * @return the width in pixels
     */
    public int getWidth() { return width; }

    /**
     * Sets the width of the rectangle.
     *
     * @param width the new width in pixels
     */
    public void setWidth(int width) { this.width = width; }

    /**
     * Returns the height of the rectangle.
     *
     * @return the height in pixels
     */
    public int getHeight() { return height; }

    /**
     * Sets the height of the rectangle.
     *
     * @param height the new height in pixels
     */
    public void setHeight(int height) { this.height = height; }

    /**
     * Returns the x-coordinate of the rectangle's left edge.
     * Equivalent to {@link #getX()}.
     *
     * @return the left edge x-coordinate
     */
    public int getLeft() { return x; }

    /**
     * Returns the x-coordinate of the rectangle's right edge.
     * Computed as {@code x + width}.
     *
     * @return the right edge x-coordinate
     */
    public int getRight() { return x + width; }

    /**
     * Returns the y-coordinate of the rectangle's top edge.
     * Equivalent to {@link #getY()}.
     *
     * @return the top edge y-coordinate
     */
    public int getTop() { return y; }

    /**
     * Returns the y-coordinate of the rectangle's bottom edge.
     * Computed as {@code y + height}.
     *
     * @return the bottom edge y-coordinate
     */
    public int getBottom() { return y + height; }

    /**
     * Returns the x-coordinate of the rectangle's center.
     * Computed as {@code x + width / 2}.
     *
     * @return the center x-coordinate
     */
    public int getCenterX() { return x + width / 2; }

    /**
     * Returns the y-coordinate of the rectangle's center.
     * Computed as {@code y + height / 2}.
     *
     * @return the center y-coordinate
     */
    public int getCenterY() { return y + height / 2; }

    /**
     * Checks whether this rectangle intersects with another rectangle.
     * <p>
     * Two rectangles intersect if their edges overlap on both axes.
     * Touching edges do not count as an intersection.
     *
     * @param rect the other rectangle to check against
     * @return {@code true} if the rectangles overlap, {@code false} otherwise
     */
    public boolean intersects(Rect rect) {
        return getLeft() < rect.getRight() &&
                getRight() > rect.getLeft() &&
                getTop() < rect.getBottom() &&
                getBottom() > rect.getTop();
    }

    /**
     * Checks whether the given point lies inside this rectangle.
     * <p>
     * Points on the edges are considered inside.
     *
     * @param px the x-coordinate of the point
     * @param py the y-coordinate of the point
     * @return {@code true} if the point is inside, {@code false} otherwise
     */
    public boolean contains(int px, int py) {
        return px >= getLeft() && px <= getRight() && py >= getTop() && py <= getBottom();
    }

    /**
     * Creates a copy of this rectangle.
     *
     * @return a new {@code Rect} with the same position and size
     */
    public Rect copy() { return new Rect(x, y, width, height); }

    /**
     * Moves this rectangle by the given offsets.
     * <p>
     * This method modifies the rectangle in place.
     *
     * @param dx the offset along the x-axis
     * @param dy the offset along the y-axis
     */
    public void move(int dx, int dy) { x += dx; y += dy; }

    /**
     * Creates a new rectangle by moving the given rectangle by the given offsets.
     * <p>
     * The original rectangle is not modified.
     *
     * @param dx   the offset along the x-axis
     * @param dy   the offset along the y-axis
     * @param rect the rectangle to move
     * @return a new {@code Rect} representing the moved rectangle
     */
    public Rect move(int dx, int dy, Rect rect) { Rect newR = rect.copy(); newR.setX(newR.getX() + dx); newR.setY(newR.getY() + dy); return newR; }

    /**
     * Sets the position of this rectangle.
     *
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     */
    public void setPosition(int x, int y) { this.x = x; this.y = y; }

    /**
     * Sets the size of this rectangle.
     *
     * @param width  the new width in pixels
     * @param height the new height in pixels
     */
    public void setSize(int width, int height) { this.width = width; this.height = height; }

    /**
     * Computes the intersection of this rectangle with another rectangle.
     * <p>
     * If the rectangles do not overlap, this method returns {@code null}.
     *
     * @param rect the other rectangle
     * @return a new {@code Rect} representing the overlapping area,
     *         or {@code null} if there is no intersection
     */
    public Rect intersection(Rect rect) {
        int left = Math.max(getLeft(), rect.getLeft());
        int right = Math.min(getRight(), rect.getRight());
        int top = Math.max(getTop(), rect.getTop());
        int bottom = Math.min(getBottom(), rect.getBottom());

        if (left >= right || top >= bottom) {
            return null;  // нет пересечения
        }

        return new Rect(left, top, right - left, bottom - top);
    }

}