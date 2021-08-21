package com.github.rccookie.greenfoot.core;

import com.github.rccookie.geometry.Border;
import com.github.rccookie.geometry.Edge;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.greenfoot.core.components.Collider;

import java.util.HashSet;
import java.util.Set;

public class MapCollider extends Collider {

    static {
        registerPrefab(MapCollider.class, g -> new MapCollider((Map)g));
    }

    /**
     * Creates a new component attached to the given map.
     *
     * @param map The map to attach this component to.
     *                   Must not be {@code null}.
     */
    public MapCollider(Map map) {
        super(map);
    }

    @Override
    public <A> Set<A> findAllIntersecting(Class<A> cls) {
        return null;
    }

    @Override
    public <A> A findIntersecting(Class<A> cls) {
        return null;
    }

    @Override
    public boolean intersects(Collider other) {
        return false;
    }

    @Override
    public boolean contains(Vector point) {
        return false;
    }

    @Override
    public Bounds getBounds() {
        return null;
    }

    @Override
    public Border[] getBorders(Vector towards) {
        // The world bounds should be visible from the inside, so the walls have to be facing counterclockwise
        Vector topLeft = Vector.of(-0.5, -0.5),
                topRight = Vector.of(gameObject.getWidth() - 0.5, -0.5),
                bottomLeft = Vector.of(-0.5, gameObject.getHeight() - 0.5),
                bottomRight = Vector.of(gameObject.getWidth() - 0.5, gameObject.getHeight() - 0.5);
        Set<Border> borders = new HashSet<>();
        if(towards.x() <= gameObject.getWidth() - 0.5)
            borders.add(new Edge(bottomRight, topRight));
        if(towards.x() >= 0.5)
            borders.add(new Edge(topLeft, bottomLeft));
        if(towards.y() <= gameObject.getHeight() - 0.5)
            borders.add(new Edge(bottomLeft, bottomRight));
        if(towards.y() >= 0.5)
            borders.add(new Edge(topRight, topLeft));

        //noinspection ToArrayCallWithZeroLengthArrayArgument
        return borders.toArray(new Border[borders.size()]);
    }
}
