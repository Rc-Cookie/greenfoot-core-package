package com.github.rccookie.greenfoot.ui.basic;

import java.util.Objects;

import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;
import com.github.rccookie.greenfoot.core.CoreActor;
import com.github.rccookie.greenfoot.ui.util.Theme;
 
public class Marker extends CoreActor {

    private static final int BORDER = 2;

    private Actor actor;
    
    private GreenfootImage lastImage = null;
    private int lastAngle;

    private Theme theme = new Theme(Color.RED);


    public Marker(Actor actor) {
        Objects.requireNonNull(actor);
        this.actor = actor;
        lastAngle = actor.getRotation();
        
        update();
    }


    @Override
    public void update() {

        if(actor == null) {
            if(getWorld() != null) getWorld().removeObject(this);
            return;
        }

        if(actor.getWorld() != getWorld()) {
            if(actor.getWorld() == null) {
                setImage((GreenfootImage)null);
                return;
            }
            actor.getWorld().addObject(this, 0, 0);
        }
        setLocation(actor);
        
        if(actor.getRotation() != lastAngle || actor.getImage() != lastImage) {

            GreenfootImage image;
            if(actor.getImage() == null) image = null;
            else {
                double sin = Math.sin(Math.toRadians(actor.getRotation())), cos = Math.cos(Math.toRadians(actor.getRotation()));
                int w = (int)(Math.abs(cos * actor.getImage().getWidth())) + (int)(Math.abs(sin * actor.getImage().getHeight()));
                int h = (int)(Math.abs(sin * actor.getImage().getWidth())) + (int)(Math.abs(cos * actor.getImage().getHeight()));

                image = new GreenfootImage(w + BORDER * 2, h + BORDER * 2);
                image.setColor(theme.main());
                image.drawRect(0, 0, w - 1 + BORDER * 2, h - 1 + BORDER * 2);
            }
            setImage(image);
            lastImage = actor.getImage();
            lastAngle = actor.getRotation();
        }
    }


    public void setActor(Actor actor) {
        this.actor = actor;
        act();
    }
    
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
