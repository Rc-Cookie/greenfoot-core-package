package com.github.rccookie.greenfoot.core.components;

import com.github.rccookie.geometry.*;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.Image;

import java.util.Set;

public class CircleCollider extends Collider {

    static {
        registerPrefab(CircleCollider.class, (g, a) -> new CircleCollider(g, (double) a[0]));
    }

    private double radius;

    /**
     * Creates a new component attached to the given gameobject.
     *
     * @param gameObject The gameobject to attach this component to.
     *                   Must not be {@code null}.
     */
    public CircleCollider(GameObject gameObject, double radius) {
        super(gameObject);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public <A> Set<A> findAllIntersecting(Class<A> cls) {
        return Set.of(); // TODO: Implement
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
        Image image = gameObject.getImage();
        if(image == null || (image.getWidth() == 0 && image.getHeight() == 0)) return new Border[0];
        final double rotation = gameObject.rotation();
        final Vector loc = gameObject.location();

        return new Border[] { new Circle(loc, getRadius(), rotation, false) };
    }
}
