package com.github.rccookie.greenfoot.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.function.Consumer;

import com.github.rccookie.greenfoot.core.MouseState;

public class MouseListener implements EventListener {

    private boolean down = false;

    private final List<Consumer<MouseState>> listeners = new ArrayList<>();
    private final List<Consumer<MouseState>> releaseListeners = new ArrayList<>();

    public void update() {
        MouseState mouse = MouseState.get();
        boolean down = (mouse != null) ? (mouse.button == 1) : false;
        if(this.down == down) return;
        this.down = down;
        if(down) onPress(mouse);
        else onRelease(mouse);
    }

    private void onPress(MouseState mouse) {
        for(Consumer<MouseState> listener : listeners) listener.accept(mouse);
    }

    private void onRelease(MouseState mouse) {
        for(Consumer<MouseState> listener : releaseListeners) listener.accept(mouse);
    }


    public void addListener(Consumer<MouseState> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<MouseState> listener) {
        listeners.remove(listener);
    }

    public void addReleaseListener(Consumer<MouseState> listener) {
        releaseListeners.add(listener);
    }

    public void removeReleaseListener(Consumer<MouseState> listener) {
        releaseListeners.remove(listener);
    }
}
