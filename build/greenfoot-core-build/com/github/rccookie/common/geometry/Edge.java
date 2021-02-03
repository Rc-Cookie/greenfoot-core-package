package com.github.rccookie.common.geometry;

import com.github.rccookie.common.data.Saveable;

public interface Edge extends Cloneable, Saveable {

    public Ray ray();

    public Vector root();

    public Vector edge();

    public Vector get(double r);

    public double length();

    public default Edge2D get2D() {
        return new Edge2D(ray().get2D());
    }

    public default Edge3D get3D() {
        return new Edge3D(ray().get3D());
    }



    public static double[] intersection(Edge e, Ray r) {
        double[] intersection = Ray.intersection(e.ray(), r);
        return (intersection == null || intersection[0] < 0 || intersection[0] > 1) ? null : intersection;
    }

    public static double[] intersection(Edge e, Edge f) {
        double[] intersection = Ray.intersection(e.ray(), f.ray());
        return (intersection == null || intersection[0] < 0 || intersection[0] > 1 || intersection[1] < 0 || intersection[1] > 1) ? null : intersection;
    }

    public static Double intersection(Edge e, Vector p) {
        double posA = (p.x() - e.root().x()) / e.edge().x();
        return (posA >= 0 && posA <= 1 && (p.y() - e.root().y()) / e.edge().y() == posA) ? posA : null;
    }
}
