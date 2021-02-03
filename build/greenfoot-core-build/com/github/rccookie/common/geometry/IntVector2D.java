package com.github.rccookie.common.geometry;

/**
 * A special Vectr2D that always rounds all its components to integers,
 * using proper mathmatical rounding.
 */
public class IntVector2D extends Vector2D {

    private static final long serialVersionUID = -2612254444869039355L;

    /**
     * Creates a new zero vector.
     */
    public IntVector2D(){
        this(0, 0);
    }

    /**
     * Creates a new vector with the given length parallel to the x axis.
     * 
     * @param x The x length of the vector
     */
    public IntVector2D(double x){
        this(x, 0);
    }

    /**
     * Creates a new vector with the given x and y distances.
     * 
     * @param x The x distance of the new vector
     * @param y The y distance of the new vector
     */
    public IntVector2D(double x, double y){
        super(Math.round(x), Math.round(y));
    }

    /**
     * Creates a new vector from the given one.
     * The new Vector will be identical with the given one, but is a
     * different object.
     * 
     * @param copy The Vector to create the new vector from
     */
    public IntVector2D(Vector copy) {
        this(copy.x(), copy.y());
    }










    @Override
    public double angle() {
        return Math.toDegrees(Math.atan2(y(), x()));
    }

    @Override
    public IntVector2D clone(){
        return new IntVector2D(this);
    }

    public int intX() {
        return (int)x();
    }

    public int intY() {
        return (int)y();
    }







    /**
     * Sets this vectors coordinates to the specified ones.
     * 
     * @param x The new x-coordinate for this vector
     * @param y the new y-coordinate for this vector
     * @return This vector
     */
    public Vector2D set(double x, double y) {
        return setX(Math.round(x)).setY(Math.round(y));
    }

    /**
     * Sets this vectors coordinates to the ones from the given vector.
     * 
     * @param vector The vector to set this vectors coordinates to
     * @return This vector
     */
    public Vector2D set(Vector2D vector) {
        return set(vector.x(), vector.y());
    }
}
