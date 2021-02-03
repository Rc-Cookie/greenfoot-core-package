package com.github.rccookie.greenfoot.widgets;

import greenfoot.GreenfootImage;

public class RenderTask {
    
    public final GreenfootImage image;
    public final int x, y;

    public RenderTask(GreenfootImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }
}
