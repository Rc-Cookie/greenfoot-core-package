package com.github.rccookie.greenfoot.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

interface PaintOrder {

    List<GameObject> getInReverseOrder(Collection<GameObject> allObjects);
}

class InstanceSortedPaintOrder implements PaintOrder {

    final GameObject[] order;

    public InstanceSortedPaintOrder(GameObject[] order) {
        this.order = order;
    }

    @Override
    public List<GameObject> getInReverseOrder(Collection<GameObject> allObjects) {
        List<GameObject> objectsOrdered = new ArrayList<>(allObjects.size());
        for(GameObject o : order) {
            if(allObjects.remove(o)) objectsOrdered.add(o);
        }
        objectsOrdered.addAll(allObjects);

        Collections.reverse(objectsOrdered);
        return objectsOrdered;
    }
}

class ClassSortedPaintOrder implements PaintOrder {

    final Class<?>[] order;

    public ClassSortedPaintOrder(Class<?>[] order) {
        this.order = order; // Must not be null and should not be empty - paint order should be null!
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
