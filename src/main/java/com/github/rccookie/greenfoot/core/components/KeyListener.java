package com.github.rccookie.greenfoot.core.components;

import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.KeyState;
import com.github.rccookie.greenfoot.core.Listener;
import com.github.rccookie.util.Arguments;

import java.util.function.Consumer;

public class KeyListener extends Listener {

    static {
        registerPrefab(KeyListener.class, (gameObject, arguments) -> {
                Arguments.checkNull(arguments, "arguments");
                if(arguments.length == 0) throw new IllegalArgumentException("Missing arguments");

                if(arguments[0] instanceof Consumer) {
                    //noinspection unchecked
                    return new KeyListener(gameObject,
                            (Consumer<Listener>) arguments[0],
                            (Consumer<Listener>) arguments[1],
                            (Consumer<Listener>) arguments[2],
                            getKeys(arguments, 3)
                    );
                }
                Runnable onPress = (Runnable) arguments[0];
                if(arguments.length == 1 || !(arguments[1] instanceof Runnable))
                    return new KeyListener(gameObject, onPress, getKeys(arguments, 1));

                Runnable onRelease = (Runnable) arguments[1];
                if(arguments.length == 2 || !(arguments[2] instanceof Runnable))
                    return new KeyListener(gameObject, onPress, onRelease, getKeys(arguments, 2));

                return new KeyListener(gameObject, onPress, onRelease, (Runnable) arguments[2], getKeys(arguments, 3));
            }
        );
    }



    public KeyListener(GameObject gameObject, Consumer<Listener> onPress, Consumer<Listener> onRelease, Consumer<Listener> onHold, String... keys) {
        super(gameObject, () -> getState(keys), onPress, onRelease, onHold, true);
    }

    public KeyListener(GameObject gameObject, Runnable onPress, Runnable onRelease, Runnable onHold, String... keys) {
        super(gameObject, () -> getState(keys), onPress, onRelease, onHold);
    }

    public KeyListener(GameObject gameObject, Runnable onPress, Runnable onRelease, String... keys) {
        this(gameObject, onPress, onRelease, null, keys);
    }

    public KeyListener(GameObject gameObject, Runnable onPress, String... keys) {
        this(gameObject, onPress, null, keys);
    }



    private static boolean getState(String[] keys) {
        for(String key : keys) if(KeyState.of(key).down) return true;
        return false;
    }



    public static KeyListener onHold(GameObject gameObject, Runnable onHold, String... keys) {
        return new KeyListener(gameObject, null, null, onHold, keys);
    }

    public static KeyListener once(GameObject gameObject, Runnable oneTimeOnPress, String... keys) {
        return new KeyListener(gameObject, k -> {
            oneTimeOnPress.run();
            gameObject.removeComponent(k);
        }, null, null, keys);
    }



    private static String[] getKeys(Object[] arguments, int offset) {
        if(arguments.length == offset) return new String[0];
        if(arguments[offset] instanceof String[])
            return (String[]) arguments[offset];

        String[] keys = new String[arguments.length - 3];
        // Can't use System.arraycopy because that requires both arrays to be from the same type
        for(int i=0; i<keys.length; i++)
            keys[i] = (String) arguments[i+3];
        return keys;
    }
}
