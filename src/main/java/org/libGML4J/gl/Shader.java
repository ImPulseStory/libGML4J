package org.libGML4J.gl;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int vertexID;
    private int fragmentID;
    private static int programID;

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

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        glDeleteProgram(programID);
    }

    private void checkCompileError(int shaderID, String type) {
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderID);
            System.err.println(type + " shader compilation error: " + log);
            throw new RuntimeException(type + " shader failed");
        }
    }

    private void checkLinkError(int programID) {
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programID);
            System.err.println("Program link error: " + log);
            throw new RuntimeException("Program link failed");
        }
    }

    public static void setUniform1i(String name, int value) {
        glUniform1i(glGetUniformLocation(programID, name), value);
    }

    public static void setUniform1f(String name, float value) {
        glUniform1f(glGetUniformLocation(programID, name), value);
    }

    public static void setUniform2f(String name, float x, float y) {
        glUniform2f(glGetUniformLocation(programID, name), x, y);
    }

    public static void setUniform4f(String name, float x, float y, float z, float w) {
        glUniform4f(glGetUniformLocation(programID, name), x, y, z, w);
    }
    public static void setUniformMatrix4(String name, float[] matrix) {
        glUniformMatrix4fv(glGetUniformLocation(programID, name), false, matrix);
    }
}