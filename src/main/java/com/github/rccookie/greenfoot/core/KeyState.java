package com.github.rccookie.greenfoot.core;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

import greenfoot.Greenfoot;
import greenfoot.core.WorldHandler;
import greenfoot.gui.input.KeyboardManager;

public class KeyState {

    public final String key;

    public final boolean pressed;

    public final boolean emulated;



    private KeyState(String key) {
        this(key, Greenfoot.isKeyDown(key), false);
    }

    private KeyState(String key, boolean pressed, boolean emulated) {
        this.key = key;
        this.pressed = pressed;
        this.emulated = emulated;
    }



    public static final KeyState of(String keyName) {
        return new KeyState(keyName);
    }

    public static final Optional<KeyState> latest() {
        String latestKey = Greenfoot.getKey();
        if(latestKey == null) return Optional.empty();
        return Optional.of(KeyState.of(latestKey));
    }

    public static final void emulate(String keyName, boolean pressed) {
        if(pressed) emulatePress(keyName);
        else emulateRelease(keyName);
    }

    @SuppressWarnings("unchecked")
    public static final void emulatePress(String keyName) {
        String key = keyName.toLowerCase();
        try {
            Field f = KeyboardManager.class.getDeclaredField("keyDown");
            f.trySetAccessible();
            ((Set<String>)f.get(WorldHandler.getInstance().getKeyboardManager())).add(key);
            f = KeyboardManager.class.getDeclaredField("keyLatched");
            f.trySetAccessible();
            ((Set<String>)f.get(WorldHandler.getInstance().getKeyboardManager())).add(key);
            f = KeyboardManager.class.getDeclaredField("lastKeyTyped");
            f.trySetAccessible();
            f.set(WorldHandler.getInstance().getKeyboardManager(), key);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static final void emulateRelease(String keyName) {
        String key = keyName.toLowerCase();
        try {
            Field f = KeyboardManager.class.getDeclaredField("keyDown");
            f.trySetAccessible();
            ((Set<String>)f.get(WorldHandler.getInstance().getKeyboardManager())).remove(key);
            f = KeyboardManager.class.getDeclaredField("lastKeyTyped");
            f.trySetAccessible();
            f.set(WorldHandler.getInstance().getKeyboardManager(), key);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static final void emulateType(String keyName) {
        String key = keyName.toLowerCase();
        try {
            Field f = KeyboardManager.class.getDeclaredField("keyLatched");
            f.trySetAccessible();
            ((Set<String>)f.get(WorldHandler.getInstance().getKeyboardManager())).add(key);
            f = KeyboardManager.class.getDeclaredField("lastKeyTyped");
            f.trySetAccessible();
            f.set(WorldHandler.getInstance().getKeyboardManager(), key);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
