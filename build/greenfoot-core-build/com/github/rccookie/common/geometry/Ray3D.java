package com.github.rccookie.common.geometry;

public class Ray3D extends AbstractRay<Ray3D, Vector3D> {

    private static final long serialVersionUID = -8650630289610786798L;

    public Ray3D(Vector3D direction) {
        this(new Vector3D(), direction);
    }

    public Ray3D(Ray3D copy) {
        this(copy.root(), copy.direction());
    }

    public Ray3D(Vector3D root, Vector3D direction) {
        super(root, direction);
    }


    @Override
    protected Ray3D clone() {
        return new Ray3D(this);
    }
}
