package com.github.rccookie.greenfoot.core.components;

import com.github.rccookie.geometry.Vector;
import com.github.rccookie.greenfoot.core.Component;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.Time;

/**
 * A simple component that allows to give the gameobject a continuous
 * velocity.
 */
public class Velocity extends Component {

    static {
        registerPrefab(Velocity.class, Velocity::new);
    }

    /**
     * The object's velocity. Modifications <b>will</b> affect the
     * velocity of the object.
     */
    public final Vector velocity = Vector.of();


    /**
     * Creates a new velocity object on the given gameobject.
     *
     * @param gameObject The gameobject to add this component to
     */
    public Velocity(GameObject gameObject) {
        super(gameObject);
    }


    @Override
    public void update() {
        gameObject.location().add(velocity.scaled(Time.deltaTime()));
    }
}
