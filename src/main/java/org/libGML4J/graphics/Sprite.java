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

import org.libGML4J.Exceptions.TextureLoadException;
import org.libGML4J.core.Window;
import org.libGML4J.gl.Mesh;
import org.libGML4J.gl.Shader;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Represents a drawable 2D image, either a full texture or a sub-region of a
 * tileset.
 * <p>
 * A sprite stores its texture ID, position, size, UV coordinates, and optional
 * transforms (rotation, flip). It can draw itself directly, or be passed to a
 * {@link SpriteBatch} for batched rendering.
 * <p>
 * For tilesets, use {@link #cutTileSet(String, int, int)} to slice a texture
 * into individual sprites, and {@link #resizeTileSet(Map, int, int)} to scale
 * them.
 *
 * @author ImPulseStory
 */
public class Sprite {

    /** OpenGL texture ID for this sprite. */
    private int textureID;

    /** Position and size of the sprite in pixels. */
    private float x, y, w, h;

    /** UV coordinates for the texture region: (u0, v0) top-left, (u1, v1) bottom-right. */
    private float u0 = 0, v0 = 0, u1 = 1, v1 = 1;

    /** Rotation angle in degrees, around the sprite's center. */
    private float rotation = 0;

    /** Whether the sprite is flipped horizontally or vertically. */
    private boolean flipX = false, flipY = false;

    /** Shared shader used for all sprites. */
    private static final Shader textureShader = new Shader(vertexShaderSrc(), fragmentShaderSrc());

    /**
     * Returns the vertex shader source code.
     *
     * @return the vertex shader source
     */
    private static String vertexShaderSrc() {
        return "#version 330 core\n" +
                "layout(location = 0) in vec3 aPos;\n" +
                "layout(location = 1) in vec2 aTexCoord;\n" +
                "out vec2 fTexCoord;\n" +
                "void main() {\n" +
                "    fTexCoord = aTexCoord;\n" +
                "    gl_Position = vec4(aPos, 1.0);\n" +
                "}";
    }

    /**
     * Returns the fragment shader source code.
     *
     * @return the fragment shader source
     */
    private static String fragmentShaderSrc() {
        return "#version 330 core\n" +
                "in vec2 fTexCoord;\n" +
                "out vec4 color;\n" +
                "uniform sampler2D uTexture;\n" +
                "void main() {\n" +
                "    color = texture(uTexture, fTexCoord);\n" +
                "}";
    }

    /**
     * Creates a sprite from a full texture.
     *
     * @param textureID the OpenGL texture ID
     * @param x         the x position in pixels
     * @param y         the y position in pixels
     * @param w         the width in pixels
     * @param h         the height in pixels
     */
    public Sprite(int textureID, float x, float y, float w, float h) {
        this.textureID = textureID;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Creates a sprite from a sub-region of a texture (a tile).
     *
     * @param textureID the OpenGL texture ID
     * @param u0        the left UV coordinate
     * @param v0        the top UV coordinate
     * @param u1        the right UV coordinate
     * @param v1        the bottom UV coordinate
     * @param w         the width in pixels
     * @param h         the height in pixels
     */
    public Sprite(int textureID, float u0, float v0, float u1, float v1, float w, float h) {
        this.textureID = textureID;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
        this.w = w;
        this.h = h;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Calculates UV coordinates for a tile by its index in a tileset.
     *
     * @param textureID the OpenGL texture ID
     * @param textureW  the total width of the tileset in pixels
     * @param textureH  the total height of the tileset in pixels
     * @param tileW     the width of one tile in pixels
     * @param tileH     the height of one tile in pixels
     * @param index     the tile index (0-based, left to right, top to bottom)
     * @return a new {@code Sprite} representing the tile
     */
    public static Sprite subSurface(int textureID, int textureW, int textureH,
                                    int tileW, int tileH, int index) {
        int tilesPerRow = textureW / tileW;
        int col = index % tilesPerRow;
        int row = index / tilesPerRow;

        float u0 = (float)(col * tileW) / textureW;
        float v0 = (float)(row * tileH) / textureH;
        float u1 = (float)((col + 1) * tileW) / textureW;
        float v1 = (float)((row + 1) * tileH) / textureH;

        return new Sprite(textureID, u0, v0, u1, v1, tileW, tileH);
    }

    /**
     * Loads a PNG or JPG image into an OpenGL texture.
     *
     * @param path the file path to the image
     * @return the OpenGL texture ID
     * @throws RuntimeException if the image cannot be loaded
     */
    public static int loadTexture(String path) {
        int texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer image = STBImage.stbi_load(path, w, h, channels, 4);
            if (image == null) {
                throw new TextureLoadException("Failed to load image " + path);
            }

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                    w.get(), h.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glBindTexture(GL_TEXTURE_2D, 0);
        }

        return texID;
    }

    public void draw(float screenW,  float screenH) {
        // Compute final UVs based on flips (without mutating fields)
        float fu0 = flipX ? u1 : u0;
        float fu1 = flipX ? u0 : u1;
        float fv0 = flipY ? v1 : v0;
        float fv1 = flipY ? v0 : v1;

        // Rotation around center
        float cx = x + w / 2.0f;
        float cy = y + h / 2.0f;
        float rad = (float) Math.toRadians(rotation);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float px0 = x,     py0 = y;
        float px1 = x + w, py1 = y;
        float px2 = x + w, py2 = y + h;
        float px3 = x,     py3 = y + h;

        float vx0 = (px0 - cx) * cos - (py0 - cy) * sin + cx;
        float vy0 = (px0 - cx) * sin + (py0 - cy) * cos + cy;
        float vx1 = (px1 - cx) * cos - (py1 - cy) * sin + cx;
        float vy1 = (px1 - cx) * sin + (py1 - cy) * cos + cy;
        float vx2 = (px2 - cx) * cos - (py2 - cy) * sin + cx;
        float vy2 = (px2 - cx) * sin + (py2 - cy) * cos + cy;
        float vx3 = (px3 - cx) * cos - (py3 - cy) * sin + cx;
        float vy3 = (px3 - cx) * sin + (py3 - cy) * cos + cy;

        // To NDC
        float x0 = (vx0 / screenW) * 2.0f - 1.0f;
        float y0 = 1.0f - (vy0 / screenH) * 2.0f;
        float x1 = (vx1 / screenW) * 2.0f - 1.0f;
        float y1 = 1.0f - (vy1 / screenH) * 2.0f;
        float x2 = (vx2 / screenW) * 2.0f - 1.0f;
        float y2 = 1.0f - (vy2 / screenH) * 2.0f;
        float x3 = (vx3 / screenW) * 2.0f - 1.0f;
        float y3 = 1.0f - (vy3 / screenH) * 2.0f;

        float[] vertices = {
                x0, y0, 0.0f,  fu0, fv0,
                x1, y1, 0.0f,  fu1, fv0,
                x2, y2, 0.0f,  fu1, fv1,
                x3, y3, 0.0f,  fu0, fv1,
        };

        int[] indices = { 0, 1, 2, 2, 3, 0 };

        Mesh mesh = new Mesh(vertices, indices, false, true);

        textureShader.bind();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        textureShader.setUniform1i("uTexture", 0);
        mesh.draw();
        mesh.destroy();
        textureShader.unbind();
    }

    /**
     * Draws this sprite immediately using its own mesh.
     * <p>
     * For batched rendering, pass the sprite to {@link SpriteBatch} instead.
     */
    public void draw() {
        float screenW = Window.getWidth();
        float screenH = Window.getHeight();

        draw(screenW, screenH);
    }

    /**
     * Slices a tileset image into individual sprites, indexed by tile order.
     *
     * @param path  the file path to the tileset image
     * @param tileW the width of one tile in pixels
     * @param tileH the height of one tile in pixels
     * @return a map of tile index to sprite
     * @throws TextureLoadException if the tileset cannot be loaded
     */
    public static Map<Integer, Sprite> cutTileSet(String path, int tileW, int tileH) {
        // 1. Load texture and read its size in one go
        int texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        int textureW, textureH;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer image = STBImage.stbi_load(path, w, h, channels, 4);
            if (image == null) {
                throw new TextureLoadException("Failed to load texture: " + path);
            }

            textureW = w.get();
            textureH = h.get();

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                    textureW, textureH, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glBindTexture(GL_TEXTURE_2D, 0);
        }

        // 2. Slice into sprites
        Map<Integer, Sprite> tiles = new HashMap<>();
        int index = 0;

        for (int y = 0; y < textureH; y += tileH) {
            for (int x = 0; x < textureW; x += tileW) {
                Sprite tile = subSurface(texID, textureW, textureH, tileW, tileH, index);
                tiles.put(index, tile);
                index++;
            }
        }

        return tiles;
    }

    /**
     * Creates a new tileset where every sprite has the given size.
     * <p>
     * UV coordinates are preserved, so the texture is stretched to the new size.
     *
     * @param tileSet the original tileset
     * @param tileW   the new tile width in pixels
     * @param tileH   the new tile height in pixels
     * @return a new map of scaled sprites
     */
    public static Map<Integer, Sprite> resizeTileSet(Map<Integer, Sprite> tileSet, int tileW, int tileH) {
        Map<Integer, Sprite> tiles = new HashMap<>();

        for (Map.Entry<Integer, Sprite> entry : tileSet.entrySet()) {
            int key = entry.getKey();
            Sprite tile = entry.getValue();
            Sprite scaled = new Sprite(tile.getTextureID(),
                    tile.getU0(), tile.getV0(),
                    tile.getU1(), tile.getV1(),
                    tileW, tileH);
            tiles.put(key, scaled);
        }
        return tiles;
    }

    /**
     * Sets the sprite's position in pixels.
     *
     * @param x the new x position
     * @param y the new y position
     */
    public void setPosition(float x, float y) { this.x = x; this.y = y; }

    /**
     * Sets the horizontal and vertical flip flags.
     *
     * @param fx whether to flip horizontally
     * @param fy whether to flip vertically
     */
    public void flip(boolean fx, boolean fy) { this.flipX = fx; this.flipY = fy; }

    /**
     * Sets the rotation angle in degrees.
     *
     * @param angle the rotation angle
     */
    public void rotate(float angle) { this.rotation = angle; }

    /**
     * Sets the sprite's size in pixels.
     *
     * @param newW the new width
     * @param newH the new height
     */
    public void scale(float newW, float newH) { this.w = newW; this.h = newH; }

    /**
     * Returns the OpenGL texture ID.
     *
     * @return the texture ID
     */
    public int getTextureID() { return textureID; }

    /**
     * Returns the shared shader used by sprites.
     *
     * @return the texture shader
     */
    public static Shader getTextureShader() { return textureShader; }

    /**
     * Returns the x position.
     *
     * @return the x position
     */
    public float getX() { return x; }

    /**
     * Returns the y position.
     *
     * @return the y position
     */
    public float getY() { return y; }

    /**
     * Returns the width.
     *
     * @return the width
     */
    public float getW() { return w; }

    /**
     * Returns the height.
     *
     * @return the height
     */
    public float getH() { return h; }

    /**
     * Returns the top UV coordinate.
     *
     * @return the v0 value
     */
    public float getV0() { return v0; }

    /**
     * Returns the bottom UV coordinate.
     *
     * @return the v1 value
     */
    public float getV1() { return v1; }

    /**
     * Returns the left UV coordinate.
     *
     * @return the u0 value
     */
    public float getU0() { return u0; }

    /**
     * Returns the right UV coordinate.
     *
     * @return the u1 value
     */
    public float getU1() { return u1; }
}