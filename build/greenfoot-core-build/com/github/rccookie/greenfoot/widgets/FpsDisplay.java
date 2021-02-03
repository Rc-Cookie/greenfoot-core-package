package com.github.rccookie.greenfoot.widgets;

import com.github.rccookie.common.event.Time;

import greenfoot.Color;

public class FpsDisplay extends BigText {

    private static final String FPS_TITLE = "FPS: ";

    private final Time time;
    private int lastFps = -1;

    public FpsDisplay() {
        this(new Color(0, 150, 50));
    }
    
    public FpsDisplay(Color color) {
        super(FPS_TITLE + "-", color);
        time = new Time();
    }

    @Override
    protected void update(Size maxSize) {
        time.update();
        int currentFps = time.stableFps();
        if(currentFps != lastFps) {
            modify(() -> {
                lastFps = currentFps;
                setTitle(FPS_TITLE + lastFps);
            });
        }
    }
}
