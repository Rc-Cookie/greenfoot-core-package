package com.github.rccookie.common.geometry.shapes;

import java.util.function.Supplier;

import com.github.rccookie.common.geometry.Edge;
import com.github.rccookie.common.geometry.Ray;
import com.github.rccookie.common.geometry.Vector;

public abstract class Shape {

    private final Supplier<Vector> locationSupplier;



    public Shape(Supplier<Vector> locationSupplier) {
        this.locationSupplier = locationSupplier;
    }



    public abstract Ray getCollision(Shape other);

    public abstract  boolean intersectsPoint(Vector point);

    public abstract boolean intersects(Edge edge);

    public final Vector getLocation() {
        return locationSupplier.get();
    }
}
