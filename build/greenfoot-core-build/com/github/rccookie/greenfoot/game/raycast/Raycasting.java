package com.github.rccookie.greenfoot.game.raycast;

import java.util.Objects;

import greenfoot.Actor;
import greenfoot.Color;
import com.github.rccookie.greenfoot.ui.Marker;
import com.github.rccookie.greenfoot.ui.util.Theme;

public class Raycasting {

    private final Actor actor;
    private double angle = 0;
    private double maxLength = Double.POSITIVE_INFINITY;
    @SuppressWarnings("rawtypes")
    private Class clazz = null;
    private Actor[] ignore;

    private boolean debug = false;
    private RaycastVisualizer visualizer;
    private Theme debugTheme = new Theme(Color.GREEN, Color.BLUE, Color.ORANGE);
    private Marker marker;

    private Raycast raycast;

    public Raycasting(final Actor actor) {
        Objects.requireNonNull(actor);
        this.actor = actor;
    }

    @SuppressWarnings("unchecked")
    public void update() {
        raycast = Raycast.raycast(actor, actor.getRotation() + angle, clazz, maxLength, ignore);
        drawDebug();
    }

    private void drawDebug() {
        if(debug) {
            if(visualizer == null) visualizer = new RaycastVisualizer(debugTheme, actor.getWorld());
            if(marker == null) {
                marker = new Marker(raycast.hit);
                marker.setTheme(debugTheme.subTheme(2));
                actor.getWorld().addObject(marker, 0, 0);
                marker.act();
            }
            marker.setActor(raycast.hit);
            visualizer.update(raycast);
        }
        else {
            if(visualizer != null) visualizer.setActive(false);
            if(marker != null) {
                marker.getWorld().removeObject(marker);
                marker = null;
            }
        }
    }


    public Raycast getRaycast() {
        return raycast;
    }



    public void setAngleOffset(double angle) {
        this.angle = angle;
    }

    @SuppressWarnings("rawtypes")
    public void setClass(Class clazz) {
        this.clazz = clazz;
    }

    public void setIgnore(Actor[] ignore) {
        this.ignore = ignore;
    }

    public void setMaxLength(double maxLength) {
        this.maxLength = maxLength;
    }

    public void setDebug(boolean debug) {
        if(this.debug = debug) return;
        this.debug = debug;
        if(raycast != null) drawDebug();
    }

    public void setDebugTheme(Theme debugTheme) {
        this.debugTheme = debugTheme;
    }


    public Actor getActor() {
        return actor;
    }

    public double getAngleOffset() {
        return angle;
    }

    @SuppressWarnings("rawtypes")
    public Class getClazz() {
        return clazz;
    }

    public Actor[] getIgnore() {
        return ignore;
    }

    public double getMaxLength() {
        return maxLength;
    }

    public boolean getDebug() {
        return debug;
    }
}
