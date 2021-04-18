package com.github.rccookie.greenfoot.core;

import java.util.function.Consumer;

public class KeyListener extends Listener {

    public KeyListener(Consumer<Listener> onPress, Consumer<Listener> onRelease, Consumer<Listener> onHold, String... keys) {
        super(() -> getState(keys), onPress, onRelease, onHold, true);
    }

    public KeyListener(Runnable onPress, Runnable onRelease, Runnable onHold, String... keys) {
        super(() -> getState(keys), onPress, onRelease, onHold);
    }

    public KeyListener(Runnable onPress, Runnable onRelease, String... keys) {
        this(onPress, onRelease, null, keys);
    }

    public KeyListener(Runnable onPress, String... keys) {
        this(onPress, null, keys);
    }
    


    private static boolean getState(String[] keys) {
        for(String key : keys) if(KeyState.of(key).down) return true;
        return false;
    }



    public static KeyListener onHold(Runnable onHold, String... keys) {
        return new KeyListener(null, null, onHold, keys);
    }

    public static KeyListener once(Runnable oneTimeOnPress, String... keys) {
        return new KeyListener(k -> {
            oneTimeOnPress.run();
            k.deactivate();
        }, null, null, keys);
    }
}
