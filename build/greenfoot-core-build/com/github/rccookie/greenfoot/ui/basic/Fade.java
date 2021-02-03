package com.github.rccookie.greenfoot.ui.basic;

import greenfoot.Color;
import greenfoot.GreenfootImage;
import com.github.rccookie.greenfoot.ui.basic.Fade;
import com.github.rccookie.common.util.ClassTag;
import com.github.rccookie.greenfoot.ui.util.UIElement;

public class Fade extends UIElement {

    static {
        ClassTag.tag(Fade.class, "ui");
    }
    
    public final Color color;
    public final long duration;
    public final boolean fadeIn;

    private long startTime, endTime;

    private Fade(Color color, double duration, boolean fadeIn) {
        this.color = color;
        this.duration = (long)(duration * 1000000000);
        this.fadeIn = fadeIn;

        addAddedAction(w -> {
            startTime = System.nanoTime();
            endTime = startTime + this.duration;
            imageChanged();
        });
    }


    /**
     * 
     * @param color The color at the beginning
     * @param duration In secondes
     * @return A fadein
     */
    public static final Fade fadeIn(Color color, double duration) {
        return new Fade(color, duration, true);
    }

    /**
     * 
     * @param color The color at the end
     * @param duration In secondes
     * @return A fadeout
     */
    public static final Fade fadeOut(Color color, double duration) {
        return new Fade(color, duration, false);
    }


    


    @Override
    public void update() {
        long time = System.nanoTime();
        if(time > endTime) {
            getWorld().removeObject(this);
            return;
        }
        if(fadeIn) getImage().setTransparency((int)(255 * (1 - (time - startTime) / (double)duration)));
        else getImage().setTransparency((int)(255 * (time - startTime) / (double)duration));
    }

    @Override
    protected void assignDefaultColorMappings() {
        mapColor("fade-color", 1, false);
    }

    @Override
    protected void regenerateImages() {
        if(getWorld() == null) setImage((GreenfootImage)null);
        GreenfootImage image = new GreenfootImage(getWorld().getWidth(), getWorld().getHeight());
        image.setColor(color);
        image.fill();
        setImage(image);
    }
}
