package com.github.rccookie.greenfoot.core;

import com.github.rccookie.greenfoot.java.util.Optional;
import greenfoot.Greenfoot;
import greenfoot.core.WorldHandler;
import greenfoot.gui.input.KeyboardManager;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Represents the state of a keyboard key at a specific point in
 * time.
 * 
 * @author RcCookie
 * @version 1.0
 */
public class KeyState {

    static {
        Core.initialize();
    }

    /**
     * The key that this key state belongs to.
     */
    public final String key;

    /**
     * Weather the key is pressed down.
     */
    public final boolean down;

    /**
     * Weather this key state was emulated directly using
     * {@link #emulate(String, boolean).
     */
    public final boolean emulated;



    /**
     * Initializes a new key state for the specified key by measuring the current state.
     */
    private KeyState(String key) {
        this(key, Greenfoot.isKeyDown(key), false);
    }

    /**
     * Creates a new key state.
     * 
     * @param key The key this key state is for
     * @param pressed Weather the key is currently pressed down
     * @param emulated Weather this key state should be marked as emulated
     */
    private KeyState(String key, boolean pressed, boolean emulated) {
        this.key = key;
        this.down = pressed;
        this.emulated = emulated;
    }



    /**
     * Returns the current key state of the given key.
     * 
     * @param keyName The name of the key
     * @return The key state of the key
     */
    public static KeyState of(String keyName) {
        return new KeyState(keyName);
    }

    /**
     * Returns the key state of the key that was released most lately
     * since the last call of this method or {@link Greenfoot#getKey()}.
     * This key state may not be pressed anymore at this point.
     * If no key was typed since the last request this method will return an
     * an empty optional.
     * 
     * @return The current key state of the last released key.
     */
    public static Optional<KeyState> latest() {
        String latestKey = Greenfoot.getKey();
        if(latestKey == null) return Optional.empty();
        return Optional.of(KeyState.of(latestKey));
    }

    /**
     * Returns an enulated key state for the given key at the given state.
     * 
     * @param keyName The name of the key to emulate a key state of
     * @param pressed Weather the key should be emulated as pressed or not
     * @return The emulated key state
     */
    public static KeyState emulate(String keyName, boolean pressed) {
        return new KeyState(keyName, pressed, true);
    }

    /**
     * Emulates pressing down the given key from inside the greenfoot framework.
     * The key will be released the next time the key gets released "in real
     * life" or the release is emulated.
     * If the specified key is pressed down anyways, this will do nothing.
     * 
     * @param keyName The key to press down
     */
    @SuppressWarnings("unchecked")
    public static void emulatePress(String keyName) {
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
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Emulates releasing the given key from inside the greenfoot framework.
     * If the specified key is not pressed down, this will do nothing.
     * 
     * @param keyName The key to release
     */
    @SuppressWarnings("unchecked")
    public static void emulateRelease(String keyName) {
        String key = keyName.toLowerCase();
        try {
            Field f = KeyboardManager.class.getDeclaredField("keyDown");
            f.trySetAccessible();
            ((Set<String>)f.get(WorldHandler.getInstance().getKeyboardManager())).remove(key);
            f = KeyboardManager.class.getDeclaredField("lastKeyTyped");
            f.trySetAccessible();
            f.set(WorldHandler.getInstance().getKeyboardManager(), key);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Emulates typing the given key from inside the greenfoot framework.
     * 
     * @param keyName The key to type
     */
    @SuppressWarnings("unchecked")
    public static void emulateType(String keyName) {
        String key = keyName.toLowerCase();
        try {
            Field f = KeyboardManager.class.getDeclaredField("keyLatched");
            f.trySetAccessible();
            ((Set<String>)f.get(WorldHandler.getInstance().getKeyboardManager())).add(key);
            f = KeyboardManager.class.getDeclaredField("lastKeyTyped");
            f.trySetAccessible();
            f.set(WorldHandler.getInstance().getKeyboardManager(), key);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
