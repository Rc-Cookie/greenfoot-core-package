package com.github.rccookie.greenfoot.core;

import java.util.Collection;
import java.util.List;

interface PaintOrder {

    public List<GameObject> getInReverseOrder(Collection<GameObject> allObjects);
}
