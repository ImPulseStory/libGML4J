package org.libGML4J.utils;

public class Rect {
    private int x, y, width, height;

    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public int getLeft() { return x; }
    public int getRight() { return x + width; }
    public int getTop() { return y; }
    public int getBottom() { return y + height; }
    public int getCenterX() { return x + width / 2; }
    public int getCenterY() { return y + height / 2; }
    public boolean intersects(Rect rect) {
        return getLeft() < rect.getRight() &&
                getRight() > rect.getLeft() &&
                getTop() < rect.getBottom() &&
                getBottom() > rect.getTop();
    }
    public boolean contains(int px, int py) {
        return px >= getLeft() && px <= getRight() && py >= getTop() && py <= getBottom();
    }
    public Rect copy() { return new Rect(x, y, width, height); }
    public void move(int dx, int dy) { x += dx; y += dy; }
    public Rect move(int dx, int dy, Rect rect) { Rect newR = rect.copy(); newR.setX(newR.getX() + dx); newR.setY(newR.getY() + dy); return newR; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setSize(int width, int height) { this.width = width; this.height = height; }
    public Rect intersection(Rect rect) {
        int left = Math.max(getLeft(), rect.getLeft());
        int right = Math.min(getRight(), rect.getRight());
        int top = Math.max(getTop(), rect.getTop());
        int bottom = Math.min(getBottom(), rect.getBottom());

        if (left >= right || top >= bottom) {
            return null;  // нет пересечения
        }

        return new Rect(left, top, right - left, bottom - top);
    }

}
