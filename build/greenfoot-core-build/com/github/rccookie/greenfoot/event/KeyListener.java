package com.github.rccookie.greenfoot.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import com.github.rccookie.common.event.Time;
import com.github.rccookie.common.util.Updatable;
import com.github.rccookie.greenfoot.core.KeyState;

public class KeyListener implements EventListener, Updatable {

    private static final long MIN_MULTICLICK_DELAY = 500;
    private static final double MULTICLICK_DELAY = 0.02;

    public final String key;
    private boolean down, pressed, released;
    private int count = 0;

    public boolean allowHolding = false;

    private long pressTimeStart;
    private double timeSinceLastPress;
    private Time time = new Time();

    private List<Runnable> listeners = new ArrayList<Runnable>();
    private List<Runnable> releaseListeners = new ArrayList<Runnable>();

    public KeyListener(String key){
        this.key = key;
        update();
    }

    @Override
    public void update() {
        boolean temp = down;
        down = KeyState.of(key).pressed;
        if(temp != down) {
            pressed = down;
            released = !down;
            if(down) {
                count++;
                pressTimeStart = System.currentTimeMillis();
                timeSinceLastPress = 0;
                onPress();
            }
            else onRelease();
        }
        else{
            pressed = false;
            if(allowHolding && down && System.currentTimeMillis() - pressTimeStart >= MIN_MULTICLICK_DELAY) {
                timeSinceLastPress += time.deltaTime();
                while(timeSinceLastPress >= MULTICLICK_DELAY) {
                    timeSinceLastPress -= MULTICLICK_DELAY;
                    onRelease();
                    onPress();
                }
            }
        }
    }

    public void click(int count){
        for(int i=0; i<count; i++){
            click();
        }
    }
    public void click(){
        count++;
        onPress();
        onRelease();
    }

    public boolean down(){
        return down;
    }
    public boolean pressed(){
        return pressed;
    }
    public boolean released(){
        return released;
    }
    public int count(){
        return count;
    }


    private void onPress() {
        for(Runnable listener : listeners) listener.run();
    }

    private void onRelease() {
        for(Runnable listener : releaseListeners) listener.run();
    }


    public KeyListener addListener(Runnable listener) {
        listeners.add(listener);
        return this;
    }

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public KeyListener addReleaseListener(Runnable listener) {
        releaseListeners.add(listener);
        return this;
    }

    public void removeReleaseListener(Runnable listener) {
        releaseListeners.remove(listener);
    }
}