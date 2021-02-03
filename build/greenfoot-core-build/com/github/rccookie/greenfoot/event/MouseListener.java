package com.github.rccookie.greenfoot.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.function.Consumer;

import greenfoot.MouseInfo;

public class MouseListener implements EventListener {

    private boolean down = false;

    private final List<Consumer<MouseInfo>> listeners = new ArrayList<>();
    private final List<Consumer<MouseInfo>> releaseListeners = new ArrayList<>();

    public void update() {
        MouseInfo mouse = Input.mouseInfo();
        boolean down = (mouse != null) ? (mouse.getButton() == 1) : false;
        if(this.down == down) return;
        this.down = down;
        if(down) onPress(mouse);
        else onRelease(mouse);
    }

    private void onPress(MouseInfo mouse) {
        for(Consumer<MouseInfo> listener : listeners) listener.accept(mouse);
    }

    private void onRelease(MouseInfo mouse) {
        for(Consumer<MouseInfo> listener : releaseListeners) listener.accept(mouse);
    }


    public void addListener(Consumer<MouseInfo> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<MouseInfo> listener) {
        listeners.remove(listener);
    }

    public void addReleaseListener(Consumer<MouseInfo> listener) {
        releaseListeners.add(listener);
    }

    public void removeReleaseListener(Consumer<MouseInfo> listener) {
        releaseListeners.remove(listener);
    }
}
