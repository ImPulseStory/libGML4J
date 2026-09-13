package org.libGML4J.core;

public class Clock {
    private static float lastTime = 0.0f;
    private static float deltaTime = 0.0f;
    private static int fps = 0;
    private static int frameCount = 0;
    private static float fpsTimer = 0.0f;
    private static int targetFPS = 60;

    public static void update() {
        float currentTime = Time.getTime();
        deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        // Считаем FPS
        frameCount++;
        fpsTimer += deltaTime;
        if (fpsTimer >= 1.0f) {
            fps = frameCount;
            frameCount = 0;
            fpsTimer = 0.0f;
        }

        // Ограничитель FPS
        if (targetFPS > 0) {
            float targetFrameTime = 1.0f / targetFPS;
            if (deltaTime < targetFrameTime) {
                try {
                    Thread.sleep((long) ((targetFrameTime - deltaTime) * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static float getDeltaTime() { return deltaTime; }
    public static int getFPS() { return fps; }
    public static void setTargetFPS(int target) { targetFPS = target; }
    public static int getTargetFPS() { return targetFPS; }
}