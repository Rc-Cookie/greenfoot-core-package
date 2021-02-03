package com.github.rccookie.common.geometry.shapes;

import java.util.function.Supplier;

import com.github.rccookie.common.geometry.Edge;
import com.github.rccookie.common.geometry.Ray;
import com.github.rccookie.common.geometry.Vector;

public class Box2D extends Shape {

    public Box2D(Supplier<Vector> locationSupplier) {
        super(locationSupplier);
    }

    @Override
    public Ray getCollision(Shape other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean intersectsPoint(Vector point) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean intersects(Edge edge) {
        // TODO Auto-generated method stub
        return false;
    }
}
