package org.libGML4J.core;

public class Time {
    private static long timestarted = System.nanoTime();

    public static float getTime() {
        return (float) ((System.nanoTime() - timestarted) * 1E-9);
    }
}