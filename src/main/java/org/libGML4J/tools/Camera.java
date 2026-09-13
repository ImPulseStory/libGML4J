package org.libGML4J.tools;

import org.libGML4J.core.Window;
import org.libGML4J.utils.Rect;

public class Camera {
    public int x, y;

    public Camera() {
        this.x = 0;
        this.y = 0;
    }

    public int applyX(int x) { return x - this.x; }
    public int applyY(int y) { return y - this.y; }

    public void follow(Rect rect) {
        this.x = rect.getCenterX() - Window.getWidth() / 2;
        this.y = rect.getCenterY() - Window.getHeight() / 2;
    }

    public Rect player(Rect rect) {
        Rect newi = rect.copy();
        newi.move(rect.getX() - this.x, rect.getY() - this.y);
        return newi;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}