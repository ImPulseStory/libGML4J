package org.libGML4J.graphics;

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

public class Sprite {
    // Instance fields — each sprite has its own state
    private int textureID;
    private float x, y, w, h;
    private float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
    private float rotation = 0;
    private boolean flipX = false, flipY = false;

    // Shared shader — same for all sprites
    private static final Shader textureShader = new Shader(vertexShaderSrc(), fragmentShaderSrc());

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

    private static String fragmentShaderSrc() {
        return "#version 330 core\n" +
                "in vec2 fTexCoord;\n" +
                "out vec4 color;\n" +
                "uniform sampler2D uTexture;\n" +
                "void main() {\n" +
                "    color = texture(uTexture, fTexCoord);\n" +
                "}";
    }

    // Constructor for a full texture
    public Sprite(int textureID, float x, float y, float w, float h) {
        this.textureID = textureID;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    // Constructor for a tile (with UV)
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

    // Calculate UV for a tile by index
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

    // Load a PNG/JPG into a texture, return its ID
    public static int loadTexture(String path) {
        int texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer image = STBImage.stbi_load(path, w, h, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load image " + path);
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

    // Draw this sprite
    public void draw() {
        float screenW = Window.getWidth();
        float screenH = Window.getHeight();

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
        Shader.setUniform1i("uTexture", 0);
        mesh.draw();
        mesh.destroy();
        textureShader.unbind();
    }

    // Slice a tileset into a map of sprites by index
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
                throw new RuntimeException("Failed to load image " + path);
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

    // Setters
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void flip(boolean fx, boolean fy) { this.flipX = fx; this.flipY = fy; }
    public void rotate(float angle) { this.rotation = angle; }
    public void scale(float newW, float newH) { this.w = newW; this.h = newH; }

    // Getters
    public int getTextureID() { return textureID; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getW() { return w; }
    public float getH() { return h; }

    public float getV0() { return v0; }
    public float getV1() { return v1; }
    public float getU0() { return u0; }
    public float getU1() { return u1; }
}