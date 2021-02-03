package com.github.rccookie.common.geometry;

public class Ray2D extends AbstractRay<Ray2D, Vector2D> {

    private static final long serialVersionUID = -8650630289610786798L;

    public Ray2D(Vector2D direction) {
        this(new Vector2D(), direction);
    }

    public Ray2D(Ray2D copy) {
        this(copy.root(), copy.direction());
    }

    public Ray2D(Vector2D root, Vector2D direction) {
        super(root, direction);
    }


    @Override
    protected Ray2D clone() {
        return new Ray2D(this);
    }
}
