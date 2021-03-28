package com.github.rccookie.greenfoot.event;

import greenfoot.Actor;
import greenfoot.Greenfoot;
import greenfoot.MouseInfo;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.common.util.Cache;
import com.github.rccookie.common.util.CachedItem;

@Deprecated
public abstract class Input {

    private static final long DEF_KEY_CACHE_DURATION = 15l;
    private static final long DEF_MOUSE_CACHE_DURATION = 15l;

    private static final Cache<String, Boolean> KEY_CACHE = new Cache<>(DEF_KEY_CACHE_DURATION);
    private static final CachedItem<MouseInfo> MOUSE_INFO_CACHE = new CachedItem<>(DEF_MOUSE_CACHE_DURATION);
    private static final CachedItem<MouseState> MOUSE_STATE_CACHE = new CachedItem<>(DEF_MOUSE_CACHE_DURATION);


    public static final void setKeyCacheDuration(final long duration) {
        KEY_CACHE.setCacheDuration(duration);
    }

    public static final void setMouseCacheDuration(final long duration) {
        MOUSE_INFO_CACHE.setCacheDuration(duration);
    }




    public static final boolean keyState(String key) {
        return KEY_CACHE.getOrPutNew(key, () -> Greenfoot.isKeyDown(key));
    }

    public static final MouseInfo mouseInfo() {
        return MOUSE_INFO_CACHE.getOrSetNew(() -> Greenfoot.getMouseInfo());
    }

    public static final MouseState mouseState() {
        return MOUSE_STATE_CACHE.getOrSetNew(() -> new MouseState(Greenfoot.getMouseInfo()));
    }


    public static class MouseState {
        private final MouseInfo mouse;

        private MouseState(MouseInfo mouse) {
            this.mouse = mouse;
            mouse.getButton();
            mouse.getClickCount();
            mouse.getX();
        }

        public Vector2D getLocation() {
            return new Vector2D(mouse.getX(), mouse.getY());
        }

        public int getX() {
            return mouse.getX();
        }

        public int getY() {
            return mouse.getY();
        }

        public Actor getActor() {
            return mouse.getActor();
        }

        public boolean pressed() {
            return getButton() == 1;
        }

        public int getButton() {
            return mouse.getButton();
        }

        public int getClickCount() {
            return mouse.getClickCount();
        }
    }
}
