package com.github.rccookie.common.event;

import java.util.NoSuchElementException;

import com.github.rccookie.common.util.Stream;

/**
 * A stream of string input that has a limited capacity.
 */
public class InputStream implements Stream<String> {
    
    private String[] data;
    private int size = 0;

    /**
     * Creates an empty InputStream with the given capacity.
     * 
     * @param capacity The capacity of the stream
     */
    public InputStream(int capacity) {
        data = new String[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(String element) {
        if(size == data.length) return false;
        data[size++] = element;
        return true;
    }

    @Override
    public String next() {
        if(size == 0) throw new NoSuchElementException();
        String element = data[0];
        System.arraycopy(data, 1, data, 0, --size);
        return element;
    }

    @Override
    public String peek(int index) {
        if(index >= size) throw new NoSuchElementException();
        return data[index];
    }

    /**
     * Returns the capacity of this InputStream.
     * 
     * @return The capacity of this InputStream
     */
    public int getCapacity() {
        return data.length;
    }

    /**
     * Sets the capacity of this InputStream possibly whiping
     * out values outside of the new capacity
     * 
     * @param capacity The new capacity
     * @throws NegativeArraySizeException If the capacity is negative
     */
    public void setCapacity(int capacity) {
        if(capacity < 0) throw new NegativeArraySizeException();
        size = Math.min(size, capacity);
        String[] newData = new String[capacity];
        System.arraycopy(data, 0, newData, 0, size);
    }
}
