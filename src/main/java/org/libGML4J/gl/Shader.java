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

import org.libGML4J.Exceptions.ShaderCompilationException;

import static org.lwjgl.opengl.GL20.*;

/**
 * Compiles, links, and manages an OpenGL shader program.
 * <p>
 * A shader is created from a vertex source and a fragment source. The
 * constructor compiles both, links them into a program, and deletes the
 * individual shader objects (keeping only the program).
 * <p>
 * Uniform setters are static and always operate on the most recently linked
 * program. This works for the current single-shader design but should be
 * revisited if multiple programs are used simultaneously.
 *
 * @author ImPulseStory
 */
public class Shader {

    /** OpenGL handle for the vertex shader (deleted after linking). */
    private int vertexID;

    /** OpenGL handle for the fragment shader (deleted after linking). */
    private int fragmentID;

    /** OpenGL handle for the linked shader program. */
    private int programID;

    /**
     * Compiles and links a shader program from the given sources.
     *
     * @param vertexSource   the vertex shader source code
     * @param fragmentSource the fragment shader source code
     * @throws ShaderCompilationException if compilation or linking fails
     */
    public Shader(String vertexSource, String fragmentSource) {
        // Vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        checkCompileError(vertexID, "VERTEX");

        // Fragment Shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        checkCompileError(fragmentID, "FRAGMENT");

        // Link
        programID = glCreateProgram();
        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);
        glLinkProgram(programID);
        checkLinkError(programID);

        // Delete shaders
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    /**
     * Makes this shader program the active one.
     */
    public void bind() {
        glUseProgram(programID);
    }

    /**
     * Unbinds any active shader program.
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * Deletes the shader program, freeing GPU memory.
     */
    public void destroy() {
        glDeleteProgram(programID);
    }

    /**
     * Checks a shader's compile status and throws if it failed.
     *
     * @param shaderID the shader handle
     * @param type     the shader type label ("VERTEX" or "FRAGMENT")
     * @throws ShaderCompilationException if compilation failed
     */
    private void checkCompileError(int shaderID, String type) {
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderID);
            System.err.println(type + " shader compilation error: " + log);
            throw new ShaderCompilationException(type + " shader compilation error: " + log);
        }
    }

    /**
     * Checks a program's link status and throws if it failed.
     *
     * @param programID the program handle
     * @throws ShaderCompilationException if linking failed
     */
    private void checkLinkError(int programID) {
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programID);
            System.err.println("Program link error: " + log);
            throw new ShaderCompilationException("Program link failed, failed to link shader program with name: " + programID);
        }
    }

    /**
     * Sets an {@code int} uniform.
     *
     * @param name  the uniform name
     * @param value the value
     */
    public void setUniform1i(String name, int value) {
        glUniform1i(glGetUniformLocation(programID, name), value);
    }

    /**
     * Sets a {@code float} uniform.
     *
     * @param name  the uniform name
     * @param value the value
     */
    public void setUniform1f(String name, float value) {
        glUniform1f(glGetUniformLocation(programID, name), value);
    }

    /**
     * Sets a {@code vec2} uniform.
     *
     * @param name the uniform name
     * @param x    the first component
     * @param y    the second component
     */
    public void setUniform2f(String name, float x, float y) {
        glUniform2f(glGetUniformLocation(programID, name), x, y);
    }

    /**
     * Sets a {@code vec3} uniform.
     *
     * @param name the uniform name
     * @param v      the first component
     * @param v1     the second component
     * @param v2     the third component
     */
    public void setUniform3f(String name, float v, float v1, float v2) {
        glUniform3f(glGetUniformLocation(programID, name), v, v1, v2);
    }

    /**
     * Sets a {@code vec4} uniform.
     *
     * @param name the uniform name
     * @param x    the first component
     * @param y    the second component
     * @param z    the third component
     * @param w    the fourth component
     */
    public void setUniform4f(String name, float x, float y, float z, float w) {
        glUniform4f(glGetUniformLocation(programID, name), x, y, z, w);
    }

    /**
     * Sets a {@code mat4} uniform.
     *
     * @param name   the uniform name
     * @param matrix the 4x4 matrix as a 16-element float array
     */
    public void setUniformMatrix4(String name, float[] matrix) {
        glUniformMatrix4fv(glGetUniformLocation(programID, name), false, matrix);
    }

    /**
     * Sets an {@code ivec2} uniform.
     *
     * @param name the uniform name
     * @param x    the first component
     * @param y    the second component
     */
    public void setUniform2i(String name, int x, int y) {
        glUniform2i(glGetUniformLocation(programID, name), x, y);
    }

    /**
     * Sets an {@code ivec3} uniform.
     *
     * @param name the uniform name
     * @param x    the first component
     * @param y    the second component
     * @param z    the third component
     */
    public void setUniform3i(String name, int x, int y, int z) {
        glUniform3i(glGetUniformLocation(programID, name), x, y, z);
    }

    /**
     * Sets a {@code vec4} uniform using an int for the first component.
     * <p>
     * <b>Note:</b> this overload is ambiguous with {@link #setUniform4f(String, float, float, float, float)}
     * when integer literals are passed. Consider removing or renaming it.
     *
     * @param name the uniform name
     * @param u    the first component (int)
     * @param v    the second component
     * @param v1   the third component
     * @param v2   the fourth component
     */
    public void setUniform4f(String name, int u, float v, float v1, float v2) {
        glUniform4f(glGetUniformLocation(programID, name), u, v, v1, v2);
    }
}