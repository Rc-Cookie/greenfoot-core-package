package com.github.rccookie.greenfoot.java.util;

import java.util.Iterator;
import java.util.function.Predicate;

public final class Collections {

    private Collections() {
        throw new UnsupportedOperationException();
    }

    /**
     * Equivalent to {@link java.util.Collection#removeIf(Predicate)} because that method does not exist
     * online.
     *
     * @param collection The collection to remove objects from
     * @param filter Returns {@code true} for all items that should be removed
     * @param <T> The type of collection (i.e. List, Set...)
     * @param <C> The content type of the collection
     * @return The collection itself
     */
    @SuppressWarnings({"Java8CollectionRemoveIf", "UnusedReturnValue"})
    public static <T, C extends java.util.Collection<T>> C removeIf(C collection, Predicate<T> filter) {
        // Collection.removeIf does not exist online.
        final Iterator<T> each = collection.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next()))
                each.remove();
        }
        return collection;
    }
}
