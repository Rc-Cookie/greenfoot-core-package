package com.github.rccookie.greenfoot.game.graphics;

import greenfoot.Color;
import greenfoot.GreenfootImage;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.greenfoot.game.physics.PhysicalActor;

public class Particle extends PhysicalActor {

    private long duration, endTime;

    public Particle(int size, Color color, Vector2D velocity, final double time) {
        addAddedAction(w -> endTime = System.currentTimeMillis() + (duration = (long)(time * 1000)));
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(color);
        image.fill();
        setImage(image);
        setVelocity(velocity);
        setRotation(Math.random() * 360);
        setSpin((Math.random() - 0.5) * 90);
    }

    @Override
    public void update() {
        if(System.currentTimeMillis() > endTime) {
            getWorld().removeObject(this);
            return;
        }
        getImage().setTransparency((int)((endTime - System.currentTimeMillis()) * 255d / duration));
    }
}
