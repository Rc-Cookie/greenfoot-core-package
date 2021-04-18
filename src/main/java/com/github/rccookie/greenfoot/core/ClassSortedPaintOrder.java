package com.github.rccookie.greenfoot.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ClassSortedPaintOrder implements PaintOrder {

    final Class<?>[] order;

    public ClassSortedPaintOrder(Class<?>[] order) {
        this.order = order; // Mustt not be null and should not be empty - paint order should be null!
    }

    @Override
    public List<GameObject> getInReverseOrder(Collection<GameObject> allObjects) {
        List<GameObject> objectsOrdered = new ArrayList<>(allObjects);
        for(int i = order.length-1; i>=0; i--) {
            List<GameObject> objectsFromClass = new ArrayList<>();
            for(GameObject o : allObjects) {
                if(order[i].isInstance(o)) objectsFromClass.add(o);
            }

            // Shift all those objects to the end (->front because reversed)
            objectsOrdered.removeAll(objectsFromClass);
            objectsOrdered.addAll(objectsFromClass);
        }

        return objectsOrdered;
    }
}
