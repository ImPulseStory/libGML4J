package org.libGML4J.graphics;

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
 * Stores the visual and positioning data for a single character in a font atlas.
 * <p>
 * A {@code Glyph} is produced when a TrueType font is baked into a texture.
 * Each glyph knows its UV coordinates in the atlas, its size, its offset from
 * the text cursor, and how far the cursor should advance after drawing it.
 * <p>
 * This class is a plain data container. It has no methods beyond implicit
 * getters and setters for its public fields.
 *
 * @author ImPulseStory
 */
public class Glyph {

    /** Left UV coordinate of the glyph in the font atlas (0.0 to 1.0). */
    public float u0;

    /** Top UV coordinate of the glyph in the font atlas (0.0 to 1.0). */
    public float v0;

    /** Right UV coordinate of the glyph in the font atlas (0.0 to 1.0). */
    public float u1;

    /** Bottom UV coordinate of the glyph in the font atlas (0.0 to 1.0). */
    public float v1;

    /** Width of the glyph in pixels. */
    public float w;

    /** Height of the glyph in pixels. */
    public float h;

    /** Horizontal offset from the text cursor to the glyph's left edge. */
    public float offsetX;

    /** Vertical offset from the text cursor to the glyph's top edge. */
    public float offsetY;

    /** How far the text cursor should advance after drawing this glyph. */
    public float advance;
}