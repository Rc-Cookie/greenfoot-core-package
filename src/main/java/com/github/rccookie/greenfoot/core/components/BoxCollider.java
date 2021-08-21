package com.github.rccookie.greenfoot.core.components;

import com.github.rccookie.geometry.Border;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Vectors;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.util.Arguments;

import java.util.Set;

public class BoxCollider extends Collider {

    static {
        registerPrefab(BoxCollider.class, (gameObject, arguments) -> arguments.length == 0 ?
                new BoxCollider(gameObject) :
                new BoxCollider(gameObject, (Vector) arguments[0])
        );
    }



    private Vector size;

    /**
     * Creates a new component attached to the given gameobject.
     *
     * @param gameObject The gameobject to attach this component to.
     *                   Must not be {@code null}.
     */
    public BoxCollider(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public <A> Set<A> findAllIntersecting(Class<A> cls) {
        return null;
    }

    @Override
    public <A> A findIntersecting(Class<A> cls) {
        return null;
    }

    public BoxCollider(GameObject gameObject, Vector size) {
        super(gameObject);
        this.size = size;
    }



    public void setSize(Vector size) {
        this.size = size;
    }

    public Vector getSize() {
        return Vectors.immutableVector(size);
    }

    public Vector getCurrentSize() {
        return size != null ? size : gameObject.getSize();
    }

    public Vector[] getCorners() {
        Vector cornerOffset = size.scaled(0.5);
        Vector invertedCornerOffset = cornerOffset.clone().setX(-cornerOffset.x());

        Vector worldLocation = gameObject.location();
        double worldRotation = gameObject.rotation();

        cornerOffset = cornerOffset.get2D().rotated(worldRotation);
        invertedCornerOffset = invertedCornerOffset.get2D().rotated(worldRotation);

        return new Vector[] {
                worldLocation.added(cornerOffset),
                worldLocation.added(invertedCornerOffset),
                worldLocation.subtracted(cornerOffset),
                worldLocation.subtracted(invertedCornerOffset)
        };
    }



    @Override
    public boolean contains(Vector point) {
        // Rotate the point's location around the center point, then the box must be axis aligned
        Vector dif = Vector.between(gameObject.location(), point).get2D().rotated(gameObject.rotation());
        Vector range = size.scaled(0.5);
        return dif.x() < -range.x() || dif.x() > range.x() || dif.y() < -range.y() || dif.y() > range.y();
    }

    @Override
    public boolean intersects(Collider other) {
        Arguments.checkNull(other);
        if(other instanceof BoxCollider) {
            for(Vector corner : ((BoxCollider) other).getCorners())
                if(contains(corner)) return true;
            return false;
        }
        return other.intersects(this);
    }

    @Override
    public Bounds getBounds() {
        /*double rotation = gameObject.transform().worldRotation();

        double ct = Math.cos(rotation);
        double st = Math.sin(rotation);

        hct = h * ct;
        wct = w * ct;
        hst = h * st;
        wst = w * st;

        if ( theta > 0 )
        {
            if ( theta < 90)
            {
                // 0 < theta < 90
                y_min = A_y;
                y_max = A_y + hct + wst;
                x_min = A_x - hst;
                x_max = A_x + wct;
            }
            else
            {
                // 90 <= theta <= 180
                y_min = A_y + hct;
                y_max = A_y + wst;
                x_min = A_x - hst + wct;
                x_max = A_x;
            }
        }
        else
        {
            if ( theta > -90 )
            {
                // -90 < theta <= 0
                y_min = A_y + wst;
                y_max = A_y + hct;
                x_min = A_x;
                x_max = A_x + wct - hst;
            }
            else
            {
                // -180 <= theta <= -90
                y_min = A_y + wst + hct;
                y_max = A_y;
                x_min = A_x + wct;
                x_max = A_x - hst;
            }
        }
        return new Bounds(gameObject.transform().worldLocation(), getSize().get2D().rotate(gameObject.transform().worldRotation());*/
        return null;
    }

    @Override
    public Border[] getBorders(Vector towards) {
        return new Border[0];
    }
}
