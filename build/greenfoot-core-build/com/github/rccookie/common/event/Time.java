package com.github.rccookie.common.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Time {
    public static double MAX_DELTA_TIME = 0.08;
    public static double AVERAGE_DELTA_TIME = 0.01;
    long lastNanos;
    double deltaTime = AVERAGE_DELTA_TIME;
    public double timeScale = 1;
    public boolean useStaticFramelength = false;
    public double staticFramelength = 0.001;
    public double maxDeltaTime = MAX_DELTA_TIME;
    long frameIndex = 0;

    long lastFpsUpdateNanos = 0;
    int frameCount;
    int stableFps;


    private final List<Consumer<Double>> listeners = new ArrayList<>();


    public Time() {
        lastFpsUpdateNanos = System.nanoTime();
    }

    public void update() {
        long currentNanos = System.nanoTime();
        deltaTime = (currentNanos - lastNanos) / 1000000000d;
        lastNanos = currentNanos;
        deltaTime %= 1;


        frameCount++;
        if(currentNanos - lastFpsUpdateNanos >= 1000000000l) {
            lastFpsUpdateNanos += 1000000000l;
            stableFps = frameCount;
            for(Consumer<Double> listener : listeners) listener.accept(deltaTime);
            frameCount = 0;
        }
        frameIndex++;
    }

    /**
     * Fraction of time since the last frame
     */
    public double deltaTime() {
        if(useStaticFramelength) return staticFramelength;
        return Math.min(maxDeltaTime, deltaTime) * timeScale;
    }

    public void setTimeScale(double scale) {
        timeScale = scale;
    }

    /**
     * Updated once per frame
     */
    public int fps() {
        if(deltaTime == 0) return 2000;
        return (int)(1 / deltaTime);
    }

    /**
     * Updated once per second
     */
    public int stableFps() {
        return stableFps;
    }

    public long frameIndex() {
        return frameIndex;
    }

    public void resetFrameIndex() {
        frameIndex = 0;
    }


    public void addSecondListener(Consumer<Double> listener) {
        listeners.add(listener);
    }

    public void removeSecondListener(Consumer<Double> listener) {
        listeners.remove(listener);
    }
}