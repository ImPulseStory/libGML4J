package org.libGML4J.core;

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

/**
 * Tracks delta time, FPS, and enforces a target frame rate.
 * <p>
 * Call {@link #update()} once at the beginning of every frame. It computes the
 * time since the last frame ({@link #getDeltaTime()}), counts frames per second
 * ({@link #getFPS()}), and sleeps if the frame finished faster than the target
 * frame time ({@link #setTargetFPS(int)}).
 * <p>
 * This class is static; there is only one clock per application.
 *
 * @author ImPulseStory
 */
public class Clock {

    /** The time reported at the end of the previous frame. */
    private static float lastTime = 0.0f;

    /** Time elapsed between the last two frames, in seconds. */
    private static float deltaTime = 0.0f;

    /** The FPS value computed over the last full second. */
    private static int fps = 0;

    /** Number of frames counted since the last FPS update. */
    private static int frameCount = 0;

    /** Accumulated time since the last FPS update, in seconds. */
    private static float fpsTimer = 0.0f;

    /** The target frames per second (0 = unlimited). */
    private static int targetFPS = 60;

    /**
     * Updates delta time, FPS counter, and applies the frame limiter.
     * <p>
     * Must be called once at the start of each frame, before any game logic
     * that depends on delta time.
     */
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

    /**
     * Returns the time elapsed between the last two frames, in seconds.
     *
     * @return the delta time
     */
    public static float getDeltaTime() { return deltaTime; }

    /**
     * Returns the current frames-per-second value.
     *
     * @return the FPS
     */
    public static int getFPS() { return fps; }

    /**
     * Sets the target frame rate. Use 0 for unlimited.
     *
     * @param target the target FPS
     */
    public static void setTargetFPS(int target) { targetFPS = target; }

    /**
     * Returns the current target frame rate.
     *
     * @return the target FPS
     */
    public static int getTargetFPS() { return targetFPS; }
}