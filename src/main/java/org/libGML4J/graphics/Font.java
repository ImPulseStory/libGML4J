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

import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBTruetype.*;

/**
 * A bitmap font baked from a TrueType file into an OpenGL texture.
 * <p>
 * When a {@code Font} is created, it loads a {@code .ttf} file, rasterizes the
 * first 96 ASCII characters (from space to the end of the Latin alphabet) into
 * a 512x512 texture, and stores the metrics of each glyph.
 * <p>
 * Text is drawn through a {@link SpriteBatch}: each character is turned into a
 * temporary sprite and added to the batch.
 * <p>
 * <b>Note:</b> the baked texture is single-channel ({@code GL_RED}), so the
 * standard sprite shader must be adjusted if you need colored text. In the
 * current implementation, text is rendered as-is (usually red or white,
 * depending on the shader).
 *
 * @author ImPulseStory
 */
public class Font {

    /** Path to the original TTF file. */
    private String font;

    /** Font size in pixels used when baking. */
    private int size;

    /** OpenGL texture ID of the baked font atlas. */
    private int textureID;

    /** Map of character to its glyph metrics. */
    private Map<Character, Glyph> glyphs = new HashMap<>();

    /**
     * Loads and bakes a TrueType font.
     * <p>
     * The constructor reads the TTF file, initializes STB TrueType, bakes the
     * first 96 ASCII characters into a 512x512 texture, and fills the glyph map.
     *
     * @param fontName the path to the {@code .ttf} file
     * @param size     the font size in pixels
     * @throws IOException if the font file cannot be read
     */
    public Font(String fontName, int size) throws IOException {
        this.font = fontName;
        this.size = size;

        // 1. Read TTF
        FileInputStream fis = new FileInputStream(fontName);
        byte[] bytes = fis.readAllBytes();
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        // 2. Init font
        STBTTFontinfo info = STBTTFontinfo.create();
        stbtt_InitFont(info, buffer);
        float scale = stbtt_ScaleForPixelHeight(info, size);

        // 3. Bake atlas
        ByteBuffer bitmap = MemoryUtil.memAlloc(512 * 512);
        STBTTBakedChar.Buffer chardata = STBTTBakedChar.malloc(96);
        stbtt_BakeFontBitmap(buffer, size, bitmap, 512, 512, 32, chardata);

        // 4. Create texture
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, 512, 512, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);

        // 5. Fill glyphs
        for (int i = 0; i < 96; i++) {
            char c = (char) (32 + i);
            STBTTBakedChar bc = chardata.get(i);

            Glyph g = new Glyph();
            g.u0 = bc.x0() / 512.0f;
            g.v0 = bc.y0() / 512.0f;
            g.u1 = bc.x1() / 512.0f;
            g.v1 = bc.y1() / 512.0f;
            g.w = bc.x1() - bc.x0();
            g.h = bc.y1() - bc.y0();
            g.offsetX = bc.xoff();
            g.offsetY = bc.yoff();
            g.advance = bc.xadvance();
            glyphs.put(c, g);
        }

        this.textureID = id;
    }

    /**
     * Draws a string of text at the given position using the provided sprite batch.
     * <p>
     * Each character is converted into a temporary sprite and added to the batch.
     * The cursor advances by the glyph's {@code advance} value after each character.
     *
     * @param text  the string to draw
     * @param x     the x position of the text's left edge in pixels
     * @param y     the y position of the text's baseline in pixels
     * @param batch the sprite batch to draw into
     */
    public void draw(String text, float x, float y, SpriteBatch batch) {
        float cursorX = x;

        for (char c : text.toCharArray()) {
            Glyph g = glyphs.get(c);
            if (g == null) continue;

            float px = cursorX + g.offsetX;
            float py = y + g.offsetY;

            Sprite sprite = new Sprite(textureID, g.u0, g.v0, g.u1, g.v1, g.w, g.h);
            sprite.setPosition(px, py);
            batch.draw(sprite);

            cursorX += g.advance;
        }
    }

    /**
     * Deletes the font's OpenGL texture.
     * <p>
     * Call this when the font is no longer needed to free GPU memory.
     */
    public void destroy() {
        glDeleteTextures(textureID);
    }

    /**
     * Returns the font size in pixels.
     *
     * @return the font size
     */
    public int getSize() { return this.size; }

    /**
     * Returns the path to the original TTF file.
     *
     * @return the font path
     */
    public String getFont() { return this.font; }

    /**
     * Sets the font path.
     * <p>
     * This does not reload the font; it only updates the stored path.
     *
     * @param font the new font path
     */
    public void setFont(String font) { this.font = font; }

    /**
     * Sets the font size.
     * <p>
     * This does not rebake the font; it only updates the stored size.
     *
     * @param size the new font size
     */
    public void setSize(int size) { this.size = size; }

    /**
     * Returns the OpenGL texture ID of the baked font atlas.
     *
     * @return the texture ID
     */
    public int getTextureID() { return textureID; }

    /**
     * Returns the glyph for the given character, or {@code null} if not baked.
     *
     * @param c the character
     * @return the glyph, or {@code null}
     */
    public Glyph getGlyph(char c) { return glyphs.get(c); }
}