package io.github.gravitygame.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class PerformanceMonitor {
    private static final int SAMPLE_SIZE = 30; // Number of frames to average
    private final float[] frameTimes = new float[SAMPLE_SIZE];
    private int currentSample = 0;
    private long lastFrameTime;
    private float averageFrameTime;
    private float fps;

    public PerformanceMonitor() {
        // Initialize the frame times array with zeros
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            frameTimes[i] = 0;
        }
        lastFrameTime = TimeUtils.nanoTime();
    }

    public void update() {
        // Calculate frame time in milliseconds
        long currentTime = TimeUtils.nanoTime();
        float frameTime = (currentTime - lastFrameTime) / 1_000_000f; // Convert nanoseconds to milliseconds
        lastFrameTime = currentTime;

        // Update our sample array
        frameTimes[currentSample] = frameTime;
        currentSample = (currentSample + 1) % SAMPLE_SIZE;

        // Calculate average frame time over the SAMPLE_SIZE most recent frames
        float totalTime = 0;
        for (float time : frameTimes) {
            totalTime += time;
        }
        averageFrameTime = totalTime / SAMPLE_SIZE;
        
        // Calculate FPS (1000ms / frameTime)
        fps = 1000f / (averageFrameTime > 0 ? averageFrameTime : 16.67f); // Default to 60 FPS if no valid average
    }

    public float getFPS() {
        return fps;
    }

    public float getAverageFrameTime() {
        return averageFrameTime;
    }
}