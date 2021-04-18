package com.github.rccookie.greenfoot.core.util;

@FunctionalInterface
public interface Consumer<T> {

    public void accept(T t);
}
