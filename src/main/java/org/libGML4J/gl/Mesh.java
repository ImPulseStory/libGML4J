package org.libGML4J.gl;

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

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * A low-level wrapper around a VAO, VBO, and EBO for rendering indexed geometry.
 * <p>
 * A mesh stores vertex data (positions, optional colors, optional UVs) and
 * index data, and knows how to draw itself with {@code glDrawElements}.
 * <p>
 * Three vertex layouts are supported, selected by the constructor flags:
 * <ul>
 *   <li>{@code float5 = true}: position (3) + UV (2) — used by sprites and the
 *       sprite batch (5 floats per vertex).</li>
 *   <li>{@code hasTexCoords = true}: position (3) + color (4) + UV (2) —
 *       used by textured primitives (9 floats per vertex).</li>
 *   <li>neither flag: position (3) + color (4) — used by untextured
 *       primitives (7 floats per vertex).</li>
 * </ul>
 *
 * @author ImPulseStory
 */
public class Mesh {

    /** OpenGL handles for the VAO, VBO, and EBO. */
    private int vaoID, vboID, eboID;

    /** Number of indices in the mesh. */
    private int vertexCount;

    /**
     * Creates a new mesh from the given vertex and index data.
     * <p>
     * Uploads the data to the GPU, configures vertex attributes, and stores the
     * VAO for later drawing.
     *
     * @param vertices     the vertex data (floats per vertex depends on flags)
     * @param indices      the index data
     * @param hasTexCoords whether vertices include texture coordinates
     * @param float5       whether vertices use the 5-float layout (pos + UV)
     */
    public Mesh(float[] vertices, int[] indices, boolean hasTexCoords, boolean float5) {
        vertexCount = indices.length;

        // Create VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create VBO and load vertexses
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create EBO and load indicies
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        //int stride = hasTexCoords ? 9 * Float.BYTES : 7 * Float.BYTES;
        int stride;
        if (float5) stride = 5 * Float.BYTES;
        else if (hasTexCoords) stride = 9 * Float.BYTES;
        else stride = 7 * Float.BYTES;

        // Attribute 0
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        if (!float5) {
            // Attribute 1
            glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, 3L * Float.BYTES);
            glEnableVertexAttribArray(1);

            if (hasTexCoords) {
                glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 7L * Float.BYTES);
                glEnableVertexAttribArray(2);
            }
        } else if (float5) {
            // Attribute 1
            glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3L * Float.BYTES);
            glEnableVertexAttribArray(1);
        }

        // Unlink VAO
        glBindVertexArray(0);
    }

    /**
     * Draws the mesh using its index buffer.
     * <p>
     * Binds the VAO, issues a single {@code glDrawElements} call, then unbinds.
     */
    public void draw() {
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    /**
     * Deletes the VBO, EBO, and VAO, freeing GPU memory.
     */
    public void destroy() {
        glDeleteBuffers(vboID);
        glDeleteBuffers(eboID);
        glDeleteVertexArrays(vaoID);
    }

    /**
     * Updates the vertex data in the VBO without recreating the mesh.
     * <p>
     * The new array must have the same length as the original.
     *
     * @param vertices the new vertex data
     */
    public void update(float[] vertices) {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}