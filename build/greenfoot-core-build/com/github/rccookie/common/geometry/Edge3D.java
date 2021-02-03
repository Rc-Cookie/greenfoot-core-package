package com.github.rccookie.common.geometry;

public class Edge3D extends AbstractEdge<Edge3D, Ray3D, Vector3D> {

    private static final long serialVersionUID = 1108198518872572190L;


    public Edge3D(Vector3D root, Vector3D edge) {
        this(new Ray3D(root, edge));
    }
    public Edge3D(Edge3D copy) {
        this(copy.ray());
    }
    public Edge3D(Ray3D ray) {
        super(ray);
    }


    @Override
    protected Edge3D clone() {
        return new Edge3D(this);
    }
}
