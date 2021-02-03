package com.github.rccookie.common.geometry.shapes;

import java.util.function.Supplier;

import com.github.rccookie.common.geometry.Edge;
import com.github.rccookie.common.geometry.Ray;
import com.github.rccookie.common.geometry.Vector;

public class Sphere extends Shape {

    private final double radius, sqrRadius;

    public Sphere(double radius, Supplier<Vector> locationSupplier) {
        super(locationSupplier);
        this.radius = radius;
        sqrRadius = radius * radius;
    }

    @Override
    public Ray getCollision(Shape other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean intersectsPoint(Vector point) {
        return Vector.between(getLocation(), point).sqrAbs() <= sqrRadius;
    }

    @Override
    public boolean intersects(Edge edge) {
        return false;
    }

    public double getRadius() {
        return radius;
    }
}
