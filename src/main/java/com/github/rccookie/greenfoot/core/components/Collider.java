package com.github.rccookie.greenfoot.core.components;

import com.github.rccookie.geometry.Border;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Vectors;
import com.github.rccookie.greenfoot.core.Component;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.java.util.Optional;
import com.github.rccookie.util.Arguments;

import java.util.Set;

public abstract class Collider extends Component {

    /**
     * Creates a new component attached to the given gameobject.
     *
     * @param gameObject The gameobject to attach this component to.
     *                   Must not be {@code null}.
     */
    public Collider(GameObject gameObject) {
        super(gameObject, true, true);
    }



    /**
     * Returns all game objects of the specified class that intersect this
     * object.
     *
     * @param <A> The type of object
     * @param cls The class of the objects to return
     * @return All intersecting game objects of the specified class
     */
    public abstract <A> Set<A> findAllIntersecting(Class<A> cls);

    /**
     * Finds an object of the given class that graphically intersects this object.
     *
     * @param <A> The type of object
     * @param cls The class of the object to return
     * @return An intersecting object, or {@code null}
     */
    public abstract <A> A findIntersecting(Class<A> cls);

    /**
     * Finds an object of the given class that intersects this object.
     *
     * @param <A> The type of object
     * @param cls The class of the object to return
     * @return An optional with an intersecting object, or an empty optional
     */
    protected <A> Optional<A> tryFindIntersecting(Class<A> cls) {
        Arguments.checkNull(cls);
        return Optional.ofNullable(findIntersecting(cls));
    }

    /**
     * Checks weather this collider intersects the given collider.
     *
     * @param other The object to check collisions for
     * @return {@code true} if the two objects intersect, {@code false} otherwise
     */
    public abstract boolean intersects(Collider other);



    public abstract boolean contains(Vector point);

    public abstract Bounds getBounds();

    public abstract Border[] getBorders(Vector towards);



    public static final class Bounds {

        public final Vector center;

        public final Vector size;

        private Vector min, max;

        public Bounds(Vector location, Vector size) {
            this.center = Vectors.immutableVector(location);
            this.size = Vectors.immutableVector(size);
        }

        public Vector getMin() {
            if(min == null) min = center.added(size.scaled(-0.5));
            return min;
        }

        public Vector getMax() {
            if(max == null) max = center.added(size.scaled(0.5));
            return max;
        }
    }
}
