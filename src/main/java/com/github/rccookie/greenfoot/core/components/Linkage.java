package com.github.rccookie.greenfoot.core.components;

import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Vectors;
import com.github.rccookie.greenfoot.core.Component;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.util.Arguments;

/**
 * Component used to link this gameobject's transform to the transform
 * of another gameobject, optional with an offset.
 */
public class Linkage extends Component {

    static {
        registerPrefab(Linkage.class, Linkage::new);
    }


    /**
     * The location offset.
     */
    private Vector offset = Vector.ZERO;

    /**
     * The rotation offset.
     */
    private double rotationOffset = 0;

    /**
     * The gameobject to follow.
     */
    private GameObject target = null;


    /**
     * Creates a new linkage attached to the specified gameobject. By default,
     * the object will not be following any object.
     *
     * @param gameObject The gameobject this component should be attached to
     */
    public Linkage(GameObject gameObject) {
        super(gameObject);
    }


    /**
     * Returns the currently used offset that this object is located away
     * from the target object's location.
     *
     * @return The currently used offset
     */
    public Vector getOffset() {
        return Vectors.immutableVector(offset);
    }

    /**
     * Sets the offset at which this object will be following the target
     * object.
     *
     * @param offset The offset to set
     */
    public void setOffset(Vector offset) {
        this.offset = Arguments.checkNull(offset);
    }

    /**
     * Returns the rotation offset from this object to the target object's
     * rotation.
     *
     * @return The current rotation offset
     */
    public double getRotationOffset() {
        return rotationOffset;
    }

    /**
     * Sets the rotation offset at which this object will be following the
     * target object's rotation.
     *
     * @param rotationOffset The offset to set
     */
    public void setRotationOffset(double rotationOffset) {
        this.rotationOffset = rotationOffset;
    }

    /**
     * Returns the object that this object is currently following. A return
     * value of {@code null} indicates that the object is currently not
     * following any object.
     *
     * @return The current target object
     */
    public GameObject getTarget() {
        return target;
    }

    /**
     * Sets the object that this object should follow. Passing {@code null}
     * will cause the object not to follow any object.
     *
     * @param target The object to follow
     */
    public void setTarget(GameObject target) {
        this.target = target;
    }


    @Override
    public void lateUpdate() {
        GameObject target = getTarget();
        if(target == null || target.getMap() == null) return;
        gameObject.setRotation(getRotationOffset() + target.rotation());
        gameObject.location().set(target.location().added(offset.get2D().rotated(target.rotation())));
    }
}
