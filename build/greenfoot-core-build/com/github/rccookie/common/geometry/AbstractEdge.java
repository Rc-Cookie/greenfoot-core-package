package com.github.rccookie.common.geometry;

import java.util.Objects;

public abstract class AbstractEdge<E extends AbstractEdge<E,R,V>, R extends AbstractRay<R,V>, V extends AbstractVector<V>> implements Edge {

    private static final long serialVersionUID = 3414466976248623695L;


    private final R ray;


    public AbstractEdge(AbstractEdge<E,R,V> copy) {
        this(copy.ray());
    }
    public AbstractEdge(AbstractRay<R,V> ray) {
        this.ray = ray.clone();
    }


    @Override
    public R ray() { return ray; }

    @Override
    public V root() { return ray().root(); }

    @Override
    public V edge() { return ray().direction(); }

    @Override
    public V get(double index) { return ray.get(index); }

    @Override
    public double length() { return edge().abs(); }


    @Override
    protected abstract E clone();


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Edge3D)) return false;
        return Objects.equals(ray(), ((Edge3D)obj).ray());
    }

    @Override
    public int hashCode() {
        return ray().hashCode() + 1;
    }

    @Override
    public String toString() {
        return "{root: " + root() + ", edge: " + edge() + " (" + edge().abs() + " units)}";
    }
}
