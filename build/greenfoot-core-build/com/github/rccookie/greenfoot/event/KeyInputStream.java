package com.github.rccookie.greenfoot.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.rccookie.common.event.InputStream;

/**
 * An InputStream of key strings that automatically checks
 * for specified keys.
 */
public class KeyInputStream extends InputStream {

    private final List<KeyListener> listeners = new ArrayList<>();

    /**
     * Creates a new KeyInptuStream with the given capacity.
     * 
     * @param capacity The capacity of the InputStream
     */
    public KeyInputStream(int capacity) {
        this(capacity, Collections.emptyList());
    }

    /**
     * Creates a new KeyInputStream with the given capacity
     * and the specified keys to listen for.
     * 
     * @param capacity The capacity of the InputStream
     * @param keys The keys to listen for
     */
    public KeyInputStream(int capacity, String... keys) {
        this(capacity, Arrays.asList(keys));
    }

    /**
     * Creates a new KeyInputStream with the given capacity
     * and the specified keys to listen for.
     * 
     * @param capacity The capacity of the InputStream
     * @param keys The keys to listen for
     */
    public KeyInputStream(int capacity, Collection<String> keys) {
        super(capacity);
        addKeys(keys);
    }

    /**
     * Adds the given key to listen for. If the key is already
     * listened for it will be moved to the last place.
     * 
     * @param key The key to add
     */
    public void addKey(String key) {
        Objects.requireNonNull(key);
        removeKey(key);
        listeners.add(new KeyListener(key).addListener(() -> add(key)));
    }

    /**
     * Adds the given keys to listen for. If a key is already
     * listened for it will be moved to the last place.
     * 
     * @param keys The keys to add
     */
    public void addKeys(Collection<String> keys) {
        Objects.requireNonNull(keys);
        keys.stream().forEachOrdered(k -> addKey(k));
    }

    /**
     * Stops listening for the given key, if it did.
     * 
     * @param key The key not to listen for
     */
    public void removeKey(String key) {
        for(int i=0; i<listeners.size();) {
            if(listeners.get(i).key.equals(key)) listeners.remove(i);
            else i++;
        }
    }

    /**
     * Stops listening for the given keys, if it did.
     * 
     * @param key The keys not to listen for
     */
    public void removeKeys(Collection<String> keys) {
        Objects.requireNonNull(keys);
        for(int i=0; i<listeners.size();) {
            if(keys.contains(listeners.get(i).key)) listeners.remove(i);
            else i++;
        }
    }

    /**
     * Updates this KeyInputStream's key listeners. Needs to be
     * called to ensure that keys are automatically updated.
     */
    public void update() {
        for(KeyListener listener : listeners) listener.update();
    }
}
