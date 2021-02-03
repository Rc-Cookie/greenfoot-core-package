package com.github.rccookie.common.geometry;

import java.util.Objects;

public abstract class AbstractRay<R extends AbstractRay<R,V>, V extends AbstractVector<V>> implements Ray {

    private static final long serialVersionUID = 9028705762521913495L;

    private final V root;
    private final V direction;

    public AbstractRay(AbstractRay<R,V> copy) {
        this(copy.root(), copy.direction());
    }

    public AbstractRay(V root, V direction) {
        this.root = root.clone();
        this.direction = direction.clone();
    }



    @Override
    public V get(double r) {
        return direction().scaled(r).add(root());
    }

    @Override
    public V root() { return root; }

    @Override
    public V direction() { return direction; }

    @Override
    public int size() {
        return root.size();
    }



    @Override
    protected abstract R clone();



    @Override
    public String toString() {
        return "ray from " + root() + " towards " + direction();
    }

    @Override
    public int hashCode() {
        return root().hashCode() * direction().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Ray3D)) return false;
        return Objects.equals(root(), ((Ray3D)obj).root()) && Objects.equals(direction(), ((Ray3D)obj).direction());
    }
}
