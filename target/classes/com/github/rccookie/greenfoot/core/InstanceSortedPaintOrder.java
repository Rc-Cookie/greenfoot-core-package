package com.github.rccookie.greenfoot.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
