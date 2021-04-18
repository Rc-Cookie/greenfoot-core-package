package com.github.rccookie.common.geometry;

public class Rectangle {

    private final Transform2D transform = new Transform2D();

    public final Size2D size;



    public Rectangle(Size2D size) {
        this.size = size;
    }



    public Vector2D location() {
        return transform.location;
    }

    public double getAngle() {
        return transform.rotation;
    }

    public void setAngle(double angle) {
        transform.rotation = angle;
    }



    public Vector2D intersects(Vector2D point) {
        double hw = size.width() / 2, hh = size.height() / 2;
        Vector2D[] c = {
            new Vector2D(-hw, -hh),
            new Vector2D(hw, -hh),
            new Vector2D(-hw, hh),
            new Vector2D(hw, hh)
        };
        for(Vector2D corner : c) corner.rotate(getAngle()).add(location());

        double angleSum = 0;
        for(int i=0; i<c.length; i++) angleSum += Vector2D.smallestAngle(c[i], c[(i+1)%c.length]);
        if(angleSum < 360) return null;

        return null;
    }
}
