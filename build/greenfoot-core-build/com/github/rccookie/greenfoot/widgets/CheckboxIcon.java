package com.github.rccookie.greenfoot.widgets;

import greenfoot.GreenfootImage;

public class CheckboxIcon extends Visual {
    
    private boolean ticked;

    public CheckboxIcon() {
        this(false);
    }

    public CheckboxIcon(boolean ticked) {
        this.ticked = ticked;
    }

    public boolean isTicked() {
        return ticked;
    }

    public void setTicked(boolean flag) {
        if(ticked == flag) return;
        modifyState(() -> ticked = flag);
    }

    public boolean swap() {
        setTicked(!isTicked());
        return ticked;
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        boolean ticked = isTicked();
        GreenfootImage icon = Icon.loadIconImage("checkbox_" + (ticked ? "ticked" : "unticked"));

        double scale = Math.min(maxSize.width / (double)icon.getWidth(), maxSize.height / (double)icon.getHeight());
        GreenfootImage image = new GreenfootImage(icon);
        image.scale((int)(image.getWidth() * scale), (int)(image.getHeight() * scale));
        cacheState(ticked ? 1 : 0, image);
        return image;
    }

    @Override
    GreenfootImage getCachedRender(GreenfootImage lastRender) {
        boolean ticked = isTicked();
        return getStateCache(ticked ? 1 : 0, () -> {
            GreenfootImage icon = Icon.loadIconImage("checkbox_" + (ticked ? "ticked" : "unticked"));

            GreenfootImage image = new GreenfootImage(icon);
            image.scale(lastRender.getWidth(), lastRender.getHeight());
            cacheState(ticked ? 1 : 0, image);
            return image;
        });
    }

    @Override
    public String toString() {
        return super.toString() + (isTicked() ? " (ticked)" : " (not ticked)");
    }
}
