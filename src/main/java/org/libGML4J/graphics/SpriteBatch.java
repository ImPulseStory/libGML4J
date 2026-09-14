package org.libGML4J.graphics;

/*
 * Copyright (c) 2026 ImPulseStory
 * ... (полный текст MIT)
 */

import org.libGML4J.core.Window;
import org.libGML4J.gl.Shader;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Batches multiple sprites into a single draw call for performance.
 * <p>
 * Instead of drawing each sprite individually (which causes one GPU call per
 * sprite), {@code SpriteBatch} accumulates vertex data and draws everything at
 * once. When the texture changes, the batch flushes automatically.
 * <p>
 * Usage:
 * <pre>{@code
 * batch.begin();
 * batch.draw(sprite1);
 * batch.draw(sprite2);
 * batch.end();
 * }</pre>
 *
 * @author ImPulseStory
 */
public class SpriteBatch {

    /** Number of vertices currently in the buffer. */
    private int vertexCount;

    /** Number of indices currently in the buffer. */
    private int indexCount;

    /** Maximum number of sprites per batch. */
    private int maxSprites;

    /** Vertex buffer (x, y, z, u, v per vertex). */
    private float[] vertices;

    /** Index buffer (6 indices per sprite). */
    private int[] indices;

    /** OpenGL handles for VAO, VBO, and EBO. */
    private int vaoID, vboID, eboID;

    /** Texture ID of the current batch. */
    private int currentTexture = 0;

    /** Fragment shader source. */
    private static final String fragmentShader = "#version 330 core\n" +
            "\n" +
            "in vec2 fTexCoord;\n" +
            "out vec4 color;\n" +
            "\n" +
            "uniform sampler2D uTexture;\n" +
            "\n" +
            "void main() {\n" +
            "    color = texture(uTexture, fTexCoord);\n" +
            "}";

    /** Vertex shader source. */
    private static final String vertexShader = "#version 330 core\n" +
            "\n" +
            "layout(location = 0) in vec3 aPos;\n" +
            "layout(location = 1) in vec2 aTexCoord;\n" +
            "\n" +
            "out vec2 fTexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    fTexCoord = aTexCoord;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    /** Shared shader used by all sprite batches. */
    private static final Shader shader = new Shader(vertexShader, fragmentShader);

    /**
     * Creates a new sprite batch with a default capacity of 1000 sprites.
     * <p>
     * Sets up the VAO, VBO, and EBO, and configures vertex attributes.
     */
    public SpriteBatch() {
        maxSprites = 1000;
        vertices = new float[maxSprites * 4 * 5];
        indices = new int[maxSprites * 6];

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) indices.length * Integer.BYTES, GL_DYNAMIC_DRAW);

        // attribute 0: position (3 floats)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // attribute 1: UV (2 floats)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    /**
     * Starts a new batch.
     * <p>
     * Resets the vertex and index counters. Must be called before any
     * {@link #draw(Sprite)} calls.
     */
    public void begin() {
        vertexCount = 0;
        indexCount = 0;
    }

    /**
     * Ends the batch and draws all accumulated sprites.
     * <p>
     * Calls {@link #flush()} to render the current buffer, then resets.
     */
    public void end() {
        flush();
    }

    /**
     * Adds a sprite to the batch.
     * <p>
     * If the buffer is full, or if the sprite uses a different texture than the
     * current batch, the buffer is flushed automatically before adding.
     *
     * @param batch the sprite to add
     */
    public void draw(Sprite batch) {
        if (vertexCount + 20 > vertices.length) {
            flush();
        }
        int texID = batch.getTextureID();
        if (texID != currentTexture) {
            flush();
            currentTexture = texID;
        }

        float x = batch.getX();
        float y = batch.getY();
        float w = batch.getW();
        float h = batch.getH();

        float u0 = batch.getU0();
        float u1 = batch.getU1();
        float v0 = batch.getV0();
        float v1 = batch.getV1();

        float screenW = Window.getWidth();
        float screenH = Window.getHeight();

        float px0 = x;
        float py0 = y;
        float px1 = x + w;
        float py1 = y;
        float px2 = x + w;
        float py2 = y + h;
        float px3 = x;
        float py3 = y + h;

        float x0 = (px0 / screenW) * 2.0f - 1.0f;
        float y0 = 1.0f - (py0 / screenH) * 2.0f;

        float x1 = (px1 / screenW) * 2.0f - 1.0f;
        float y1 = 1.0f - (py1 / screenH) * 2.0f;

        float x2 = (px2 / screenW) * 2.0f - 1.0f;
        float y2 = 1.0f - (py2 / screenH) * 2.0f;

        float x3 = (px3 / screenW) * 2.0f - 1.0f;
        float y3 = 1.0f - (py3 / screenH) * 2.0f;

        vertices[vertexCount++] = x0;
        vertices[vertexCount++] = y0;
        vertices[vertexCount++] = 0.0f;
        vertices[vertexCount++] = u0;
        vertices[vertexCount++] = v0;

        vertices[vertexCount++] = x1;
        vertices[vertexCount++] = y1;
        vertices[vertexCount++] = 0.0f;
        vertices[vertexCount++] = u1;
        vertices[vertexCount++] = v0;

        vertices[vertexCount++] = x2;
        vertices[vertexCount++] = y2;
        vertices[vertexCount++] = 0.0f;
        vertices[vertexCount++] = u1;
        vertices[vertexCount++] = v1;

        vertices[vertexCount++] = x3;
        vertices[vertexCount++] = y3;
        vertices[vertexCount++] = 0.0f;
        vertices[vertexCount++] = u0;
        vertices[vertexCount++] = v1;

        int base = vertexCount / 5 - 4;
        indices[indexCount++] = base + 0;
        indices[indexCount++] = base + 1;
        indices[indexCount++] = base + 2;
        indices[indexCount++] = base + 2;
        indices[indexCount++] = base + 3;
        indices[indexCount++] = base + 0;
    }

    /**
     * Renders all accumulated sprites and resets the buffer.
     * <p>
     * Called automatically by {@link #draw(Sprite)} when the buffer is full or
     * the texture changes, and by {@link #end()}.
     */
    public void flush() {
        if (vertexCount == 0) {
            return;
        }

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertexCount);
        buffer.put(vertices, 0, vertexCount);
        buffer.flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indexCount);
        indexBuffer.put(indices, 0, indexCount);
        indexBuffer.flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indexBuffer);

        glBindVertexArray(vaoID);
        shader.bind();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, currentTexture);

        shader.setUniform1i("uTexture", 0);

        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        shader.unbind();

        vertexCount = 0;
        indexCount = 0;
    }
}