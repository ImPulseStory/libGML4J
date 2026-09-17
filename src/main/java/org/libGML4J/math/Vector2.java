package org.libGML4J.math;

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
 * A 2D vector with {@code x} and {@code y} components.
 * <p>
 * Supports basic arithmetic ({@link #add(Vector2)}, {@link #sub(Vector2)},
 * {@link #mul(float)}, {@link #div(float)}), length and normalization
 * ({@link #length()}, {@link #normalize()}), dot and cross products
 * ({@link #dot(Vector2)}, {@link #crs(Vector2)}), distance ({@link #dst(Vector2)}),
 * rotation ({@link #rotate(float)}), and angle ({@link #angle()}).
 * <p>
 * All arithmetic methods mutate the vector and return {@code this} for chaining.
 * Use {@link #copy()} to create an independent copy.
 *
 * @author ImPulseStory
 */
public class Vector2 {

    /** The x component. */
    public float x;

    /** The y component. */
    public float y;

    /**
     * Creates a vector with the given components.
     *
     * @param x the x component
     * @param y the y component
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a copy of the given vector.
     *
     * @param v the vector to copy
     */
    public Vector2(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    /**
     * Creates a zero vector (0, 0).
     */
    public Vector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    /**
     * Adds the given vector to this one.
     *
     * @param v the vector to add
     * @return this vector, for chaining
     */
    public Vector2 add(Vector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    /**
     * Subtracts the given vector from this one.
     *
     * @param v the vector to subtract
     * @return this vector, for chaining
     */
    public Vector2 sub(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    /**
     * Multiplies this vector by a scalar.
     *
     * @param m the scalar
     * @return this vector, for chaining
     */
    public Vector2 mul(float m) {
        x *= m;
        y *= m;
        return this;
    }

    /**
     * Divides this vector by a scalar.
     *
     * @param m the scalar (must not be zero)
     * @return this vector, for chaining
     */
    public Vector2 div(float m) {
        x /= m;
        y /= m;
        return this;
    }

    /**
     * Returns the length (magnitude) of this vector.
     *
     * @return the length
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the squared length of this vector.
     * <p>
     * Faster than {@link #length()} because it avoids the square root.
     *
     * @return the squared length
     */
    public float length2() {
        return x * x + y * y;
    }

    /**
     * Normalizes this vector to unit length.
     * <p>
     * If the length is zero, the vector is left unchanged.
     *
     * @return this vector, for chaining
     */
    public Vector2 normalize() {
        float length = length();
        if (length != 0) {
            x /= length;
            y /= length;
        }
        return this;
    }

    /**
     * Returns the dot product of this vector and the given vector.
     *
     * @param v the other vector
     * @return the dot product
     */
    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    /**
     * Returns the cross product (z-component) of this vector and the given vector.
     *
     * @param v the other vector
     * @return the cross product
     */
    public float crs(Vector2 v) {
        return x * v.y - y * v.x;
    }

    /**
     * Returns the distance between this vector and the given vector.
     *
     * @param v the other vector
     * @return the distance
     */
    public float dst(Vector2 v) {
        float dx = x - v.x;
        float dy = y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Rotates this vector by the given angle in degrees, around the origin.
     *
     * @param degrees the rotation angle in degrees
     * @return this vector, for chaining
     */
    public Vector2 rotate(float degrees) {
        float rad = (float) Math.toRadians(degrees);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float newX = x * cos - y * sin;
        float newY = x * sin + y * cos;
        x = newX;
        y = newY;
        return this;
    }

    /**
     * Returns the angle of this vector in degrees, measured from the positive
     * x-axis.
     *
     * @return the angle in degrees
     */
    public float angle() {
        return (float) Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns an independent copy of this vector.
     *
     * @return a new vector with the same components
     */
    public Vector2 copy() {
        return new Vector2(this);
    }

    /**
     * Compares this vector to another object for equality.
     *
     * @param o the object to compare with
     * @return {@code true} if the other object is a {@code Vector2} with the
     *         same components
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector2) {
            Vector2 v = (Vector2) o;
            return x == v.x && y == v.y;
        }
        return false;
    }

    /**
     * Returns a string representation of this vector.
     *
     * @return a string in the form {@code (x, y)}
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}