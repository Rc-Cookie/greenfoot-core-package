package com.github.rccookie.common.geometry;

public class Edge2D extends AbstractEdge<Edge2D, Ray2D, Vector2D> {

    private static final long serialVersionUID = 463412350567874758L;

    public Edge2D(Vector2D root, Vector2D edge) {
        this(new Ray2D(root, edge));
    }
    public Edge2D(Edge2D copy) {
        this(copy.ray());
    }
    public Edge2D(Ray2D ray) {
        super(ray);
    }


    @Override
    protected Edge2D clone() {
        return new Edge2D(this);
    }
}
