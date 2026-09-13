package org.libGML4J.gl;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private int vaoID, vboID, eboID;
    private int vertexCount;

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

    public void draw() {
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void destroy() {
        glDeleteBuffers(vboID);
        glDeleteBuffers(eboID);
        glDeleteVertexArrays(vaoID);
    }

    public void update(float[] vertices) {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}