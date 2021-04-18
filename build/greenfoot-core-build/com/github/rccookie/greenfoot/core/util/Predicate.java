package com.github.rccookie.greenfoot.core.util;

@FunctionalInterface
public interface Predicate<T> {

    public boolean test(T t);
}
