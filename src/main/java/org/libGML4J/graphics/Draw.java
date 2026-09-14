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

import org.libGML4J.core.Window;
import org.libGML4J.gl.Mesh;
import org.libGML4J.gl.Shader;

/**
 * Static utility for drawing simple 2D primitives.
 * <p>
 * Supports rectangles, squares, triangles, circles, and lines. All shapes are
 * drawn immediately (not batched) using their own {@link Mesh} and
 * {@link Shader}.
 * <p>
 * Before any shape can be drawn, {@link #init()} must be called once after the
 * window is created. This compiles the required shaders and prepares the
 * reusable meshes.
 * <p>
 * Coordinates are in pixels, with the origin at the top-left corner. Colors
 * are given as RGB values in the range 0–255.
 *
 * @author ImPulseStory
 */
public class Draw {

    /** Vertex shader source for untextured shapes (position + color). */
    private static String vertexShaderRect = "#version 330 core\n" +
            "\n" +
            "layout(location = 0) in vec3 aPos;\n" +
            "layout(location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    /** Fragment shader source for untextured shapes. */
    private static String fragmentShaderRect = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    /** Vertex shader source for circles (position + color + UV). */
    private static String vertexShaderCircle = "#version 330 core\n" +
            "\n" +
            "layout(location = 0) in vec3 aPos;\n" +
            "layout(location = 1) in vec4 aColor;\n" +
            "layout(location = 2) in vec2 aTexCoord;  // координаты внутри круга (0..1)\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "out vec2 fTexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    fTexCoord = aTexCoord;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    /** Fragment shader source for circles (discards pixels outside the radius). */
    private static String fragmentShaderCircle = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "in vec2 fTexCoord;\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    // Расстояние от центра (0.5, 0.5)\n" +
            "    float dist = distance(fTexCoord, vec2(0.5, 0.5));\n" +
            "    \n" +
            "    // Если расстояние больше радиуса (0.5) — отбрасываем пиксель\n" +
            "    if (dist > 0.5) {\n" +
            "        discard;\n" +
            "    }\n" +
            "    \n" +
            "    color = fColor;\n" +
            "}";

    /** Reusable shader for untextured shapes. */
    private static Shader shaderRect;

    /** Reusable shader for circles. */
    private static Shader shaderCircle;

    /** Reusable mesh for rectangles. */
    private static Mesh meshRect;

    /** Reusable mesh for triangles. */
    private static Mesh meshTriangle;

    /** Reusable mesh for circles. */
    private static Mesh meshCircle;

    /** Reusable mesh for lines. */
    private static Mesh meshLine;

    /**
     * Initializes shaders and reusable meshes.
     * <p>
     * Must be called once after the window is created and before any shape is
     * drawn.
     */
    public static void init() {
        shaderRect = new Shader(vertexShaderRect, fragmentShaderRect);

        shaderCircle = new Shader(vertexShaderCircle, fragmentShaderCircle);

        float[] emptyTri = new float[3 * 7];
        int[] triIndices = { 0, 1, 2 };
        meshTriangle = new Mesh(emptyTri, triIndices, false, false);

        float[] emptyLine = new float[4 * 7];
        int[] lineIndices = { 0, 1, 2, 2, 3, 0 };
        meshLine = new Mesh(emptyLine, lineIndices, false, false);

        float[] emptyRect = new float[4 * 7];
        int[] rectIndices = { 0, 1, 2, 2, 3, 0 };
        meshRect = new Mesh(emptyRect, rectIndices, false, false);

        float[] emptyCircle = new float[4 * 9];
        int[] circleIndices = { 0, 1, 2, 2, 3, 0 };
        meshCircle = new Mesh(emptyCircle, circleIndices, true, false);    }

    /**
     * Draws a filled rectangle.
     *
     * @param x the x position of the top-left corner
     * @param y the y position of the top-left corner
     * @param w the width in pixels
     * @param h the height in pixels
     * @param r the red component (0–255)
     * @param g the green component (0–255)
     * @param b the blue component (0–255)
     */
    public static void rect(int x, int y, int w, int h, int r, int g, int b) {
        // Window size
        float screenW = Window.getWidth();
        float screenH = Window.getHeight();

        // Color in 0.0..1.0
        float cr = r / 255.0f;
        float cg = g / 255.0f;
        float cb = b / 255.0f;
        float ca = 1.0f;

        // 4 -> NDC
        // Left Bottom
        float x0 = (x / screenW) * 2.0f - 1.0f;
        float y0 = 1.0f - (y / screenH) * 2.0f;

        // Right TOp
        float x1 = ((x + w) / screenW) * 2.0f - 1.0f;
        float y1 = 1.0f - (y / screenH) * 2.0f;

        // Right Bottom
        float x2 = ((x + w) / screenW) * 2.0f - 1.0f;
        float y2 = 1.0f - ((y + h) / screenH) * 2.0f;

        // Left top
        float x3 = (x / screenW) * 2.0f - 1.0f;
        float y3 = 1.0f - ((y + h) / screenH) * 2.0f;

        // Vertexes: x, y, z, r, g, b, a
        float[] vertices = {
                x0, y0, 0.0f,  cr, cg, cb, ca,
                x1, y1, 0.0f,  cr, cg, cb, ca,
                x2, y2, 0.0f,  cr, cg, cb, ca,
                x3, y3, 0.0f,  cr, cg, cb, ca
        };

        // Indicies: two tringles

        // Draw
        meshRect.update(vertices);
        shaderRect.bind();
        meshRect.draw();
        shaderRect.unbind();
    }

    /**
     * Draws a filled square (a rectangle with equal width and height).
     *
     * @param x    the x position of the top-left corner
     * @param y    the y position of the top-left corner
     * @param size the side length in pixels
     * @param r    the red component (0–255)
     * @param g    the green component (0–255)
     * @param b    the blue component (0–255)
     */
    public static void square(int x, int y, int size, int r, int g, int b) {
        rect(x, y, size, size, r, g, b);
    }

    /**
     * Draws a filled circle.
     * <p>
     * The circle is rendered as a quad with a fragment shader that discards
     * pixels outside the radius.
     *
     * @param x      the x position of the circle's center
     * @param y      the y position of the circle's center
     * @param radius the radius in pixels
     * @param r      the red component (0–255)
     * @param g      the green component (0–255)
     * @param b      the blue component (0–255)
     */
    public static void circle(float x, float y, float radius, float r, float g, float b) {
        float screenW = Window.getWidth();
        float screenH = Window.getHeight();

        float cr = r / 255.0f;
        float cg = g / 255.0f;
        float cb = b / 255.0f;
        float ca = 1.0f;

        float x0 = x - radius;
        float y0 = y - radius;
        float x1 = x + radius;
        float y1 = y + radius;

        float nx0 = (x0 / screenW) * 2.0f - 1.0f;
        float ny0 = 1.0f - (y0 / screenH) * 2.0f;
        float nx1 = (x1 / screenW) * 2.0f - 1.0f;
        float ny1 = 1.0f - (y1 / screenH) * 2.0f;

        float[] vertices = {
                nx0, ny0, 0.0f,  cr, cg, cb, ca,  0.0f, 1.0f,  // левый верх
                nx1, ny0, 0.0f,  cr, cg, cb, ca,  1.0f, 1.0f,  // правый верх
                nx1, ny1, 0.0f,  cr, cg, cb, ca,  1.0f, 0.0f,  // правый низ
                nx0, ny1, 0.0f,  cr, cg, cb, ca,  0.0f, 0.0f   // левый низ
        };

        int[] indices = { 0, 1, 2, 2, 3, 0 };

        //meshCircle = new Mesh(vertices, indices, true, false);

        meshCircle.update(vertices);
        shaderCircle.bind();
        meshCircle.draw();
        shaderCircle.unbind();
    }

    /**
     * Draws a line with a given thickness.
     * <p>
     * The line is rendered as a rotated rectangle (two triangles).
     *
     * @param x1        the x position of the start point
     * @param y1        the y position of the start point
     * @param x2        the x position of the end point
     * @param y2        the y position of the end point
     * @param thickness the line thickness in pixels
     * @param r         the red component (0–255)
     * @param g         the green component (0–255)
     * @param b         the blue component (0–255)
     */
    public static void line(float x1, float y1, float x2, float y2, float thickness, float r, float g, float b) {
        float screenW = Window.getWidth();
        float screenH = Window.getHeight();

        float cr = r / 255.0f;
        float cg = g / 255.0f;
        float cb = b / 255.0f;
        float ca = 1.0f;

        // Вектор линии
        float dx = x2 - x1;
        float dy = y2 - y1;

        // Длина
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;  // вырожденная линия

        // Нормализация
        dx /= len;
        dy /= len;

        // Перпендикуляр
        float px = -dy;
        float py = dx;

        // Половина толщины
        px *= thickness / 2.0f;
        py *= thickness / 2.0f;

        // 4 вершины
        float vx0 = x1 + px;
        float vy0 = y1 + py;
        float vx1 = x2 + px;
        float vy1 = y2 + py;
        float vx2 = x2 - px;
        float vy2 = y2 - py;
        float vx3 = x1 - px;
        float vy3 = y1 - py;

        // В NDC
        float nx0 = (vx0 / screenW) * 2.0f - 1.0f;
        float ny0 = 1.0f - (vy0 / screenH) * 2.0f;
        float nx1 = (vx1 / screenW) * 2.0f - 1.0f;
        float ny1 = 1.0f - (vy1 / screenH) * 2.0f;
        float nx2 = (vx2 / screenW) * 2.0f - 1.0f;
        float ny2 = 1.0f - (vy2 / screenH) * 2.0f;
        float nx3 = (vx3 / screenW) * 2.0f - 1.0f;
        float ny3 = 1.0f - (vy3 / screenH) * 2.0f;

        float[] vertices = {
                nx0, ny0, 0.0f,  cr, cg, cb, ca,
                nx1, ny1, 0.0f,  cr, cg, cb, ca,
                nx2, ny2, 0.0f,  cr, cg, cb, ca,
                nx3, ny3, 0.0f,  cr, cg, cb, ca
        };

        int[] indices = { 0, 1, 2, 2, 3, 0 };

        meshLine.update(vertices);
        shaderRect.bind();
        meshLine.draw();
        shaderRect.unbind();
    }

    /**
     * Draws a filled triangle from three points.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @param x3 the x position of the third point
     * @param y3 the y position of the third point
     * @param r  the red component (0–255)
     * @param g  the green component (0–255)
     * @param b  the blue component (0–255)
     */
    public static void triangle(float x1, float y1, float x2, float y2, float x3, float y3, float r, float g, float b) {
        float screenW = Window.getWidth();
        float screenH = Window.getHeight();

        float cr = r / 255.0f;
        float cg = g / 255.0f;
        float cb = b / 255.0f;
        float ca = 1.0f;

        float nx1 = (x1 / screenW) * 2.0f - 1.0f;
        float ny1 = 1.0f - (y1 / screenH) * 2.0f;
        float nx2 = (x2 / screenW) * 2.0f - 1.0f;
        float ny2 = 1.0f - (y2 / screenH) * 2.0f;
        float nx3 = (x3 / screenW) * 2.0f - 1.0f;
        float ny3 = 1.0f - (y3 / screenH) * 2.0f;

        float[] vertices = {
                nx1, ny1, 0.0f,  cr, cg, cb, ca,
                nx2, ny2, 0.0f,  cr, cg, cb, ca,
                nx3, ny3, 0.0f,  cr, cg, cb, ca
        };

        int[] indices = { 0, 1, 2 };

        //meshTriangle = new Mesh(vertices, indices, false);

        meshTriangle.update(vertices);
        shaderRect.bind();
        meshTriangle.draw();
        shaderRect.unbind();
    }
}