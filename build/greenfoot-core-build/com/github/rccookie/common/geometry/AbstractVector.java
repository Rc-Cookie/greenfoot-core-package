package com.github.rccookie.common.geometry;

import java.util.Objects;

/**
 * A vector representing a distance in space. A vector has usually got at least 2 dimensions and
 * contains a handful of helpful methods to interact with it, like scaling and adding.
 * 
 * @see Vector2D
 * @see Vector3D
 */
public abstract class AbstractVector<V extends AbstractVector<? extends V>> implements Vector {



    // ------------------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------------------



    private static final long serialVersionUID = -3264753193349068427L;

    static final String SAVE_DIR = "geomentry\\vectors";




    // ------------------------------------------------------------------------------------
    // Instance fields
    // ------------------------------------------------------------------------------------




    private final double[] coordinates;

    private String saveName = null;




    // ------------------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------------------




    protected AbstractVector(Vector copy) {
        this(copy.toArray());
    }

    protected AbstractVector(double... initialCoordinates) {
        System.arraycopy(
            initialCoordinates, 0,
            coordinates = new double[initialCoordinates.length], 0,
            initialCoordinates.length
        );
    }





    // ------------------------------------------------------------------------------------
    // Base methods
    // ------------------------------------------------------------------------------------



    /**
     * @param dimension The dimension to get the coordinate for
     */
    @Override
    public double getDim(int dimension) {
        if(dimension >= coordinates.length) return 0;
        return coordinates[dimension];
    }

    /**
     * @param dimension The dimension to set the coordinate for
     * @param value The new coordinate in the specified dimension of this vector
     */
    @Override
    public V setDim(int dimension, double coordinate) throws UnsupportedOperationException, DimensionOutOfBoundsException {
        checkDim(dimension);
        coordinates[dimension] = coordinate;
        return getThis();
    }

    @Override
    public int size() {
        return coordinates.length;
    }

    @Override
    public abstract V clone();

    @Override
    public String toString() {
        if(size() == 0) return "[]";
        StringBuilder string = new StringBuilder().append('[');
        for(int i=0; i<size(); i++) {
            string.append(getDim(i));
            if(i < size() - 1) string.append('|');
        }
        return string.append(']').toString();
    }

    /**
     * @param obj The object to check for equality
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Vector)) return false;
        Vector vector = (Vector)obj;
        if(size() == vector.size()) {
            for(int i=0; i<size(); i++) if(getDim(i) != vector.getDim(i)) return false;
            return true;
        }
        if(size() < vector.size()) {
            for(int i=0; i<size(); i++) if(getDim(i) != vector.getDim(i)) return false;
            for(int i=size(); i<vector.size(); i++) if(vector.getDim(i) != 0) return false;
            return true;
        }
        for(int i=0; i<vector.size(); i++) if(getDim(i) != vector.getDim(i)) return false;
        for(int i=vector.size(); i<size(); i++) if(getDim(i) != 0) return false;
        return true;
    }

    @Override
    public int hashCode() {
        double hash = 1;
        for(double d : coordinates) hash *= d;
        return (int)hash;
    }




    // ------------------------------------------------------------------------------------
    // Get / set methods
    // ------------------------------------------------------------------------------------




    @Override
    public double x() throws DimensionOutOfBoundsException {
        return getDim(X);
    }

    @Override
    public double y() throws DimensionOutOfBoundsException {
        return getDim(Y);
    }

    @Override
    public double[] toArray() {
        int size = size();
        double[] array = new double[size];
        for(int i=0; i<size; i++) array[i] = getDim(i);
        return array;
    }

    /**
     * @param x The new x-coordinate for this vector
     */
    @Override
    public V setX(double x) throws UnsupportedOperationException, DimensionOutOfBoundsException {
        return setDim(X, x);
    }

    /**
     * @param y The new y-coordinate for this vector
     */
    @Override
    public V setY(double y) throws UnsupportedOperationException, DimensionOutOfBoundsException {
        return setDim(Y, y);
    }

    /**
     * @param vector The vector to set this vector's coordinates to
     */
    @Override
    public V set(Vector vector) throws NullPointerException {
        Objects.requireNonNull(vector, "The vector to set to must not be null");
        int max = Math.min(size(), vector.size());
        for(int i=0; i<max; i++) setDim(i, vector.getDim(i));
        return getThis();
    }




    // ------------------------------------------------------------------------------------
    // Property get methods
    // ------------------------------------------------------------------------------------




    @Override
    public double abs() {
        return Math.sqrt(sqrAbs());
    }

    @Override
    public double sqrAbs() {
        return dot(getThis());
    }

    @Override
    public abstract double angle();

    /**
     * @param vector The other vector
     */
    @Override
    public double dot(Vector vector) throws NullPointerException {
        Objects.requireNonNull(vector, "The vector to calculate the dot product for must not be null");
        double dot = 0;
        int size = Math.min(size(), vector.size());
        for(int i=0; i<size; i++) dot += getDim(i) * vector.getDim(i);
        return dot;
    }

    @Override
    public boolean isZero() {
        int size = size();
        for(int i=0; i<size; i++) if(getDim(i) != 0) return false;
        return true;
    }




    
    // ------------------------------------------------------------------------------------
    // Vector operation methods
    // ------------------------------------------------------------------------------------




    /**
     * @param scalar The factor to scale by
     */
    @Override
    public V scale(double scalar) throws UnsupportedOperationException {
        if(scalar == 1) return getThis();
        int size = size();
        for(int i=0; i<size; i++) setDim(i, getDim(i) * scalar);
        return getThis();
    }

    /**
     * @param denominator The denominator to divide by
     */
    @Override
    public V divide(double denominator) throws ArithmeticException, UnsupportedOperationException {
        if(denominator == 0) throw new ArithmeticException("Division by 0");
        return scale(1 / denominator);
    }

    @Override
    public V invert() throws UnsupportedOperationException {
        return scale(-1);
    }

    @Override
    public V norm() throws UnsupportedOperationException {
        if(isZero()) return getThis();
        return divide(abs());
    }

    @Override
    public V setZero() throws UnsupportedOperationException {
        return scale(0);
    }




    /**
     * @param vectors The vectors to add
     */
    @Override
    public V add(Vector... vectors) {
        Objects.requireNonNull(vectors, "The array of vectors to add must not be null");
        for(Vector vector : vectors) {
            if(vector == null) continue;
            int max = Math.min(size(), vector.size());
            for(int i=0; i<max; i++) setDim(i, getDim(i) + vector.getDim(i));
        }
        return getThis();
    }

    /**
     * @param vectors The vectors to subtract
     */
    @Override
    public V subtract(Vector... vectors) {
        Objects.requireNonNull(vectors, "The array of vectors to subtract must not be null");
        for(Vector vector : vectors) {
            if(vector == null) continue;
            int max = Math.min(size(), vector.size());
            for(int i=0; i<max; i++) setDim(i, getDim(i) - vector.getDim(i));
        }
        return getThis();
    }



    @Override
    public V floor() throws UnsupportedOperationException {
        int size = size();
        for(int i=0; i<size; i++) setDim(i, Math.floor(getDim(i)));
        return getThis();
    }

    @Override
    public V ceil() throws UnsupportedOperationException {
        int size = size();
        for(int i=0; i<size; i++) setDim(i, Math.ceil(getDim(i)));
        return getThis();
    }

    @Override
    public V round() throws UnsupportedOperationException {
        int size = size();
        for(int i=0; i<size; i++) setDim(i, Math.round(getDim(i)));
        return getThis();
    }




    // ------------------------------------------------------------------------------------
    // Cloned-operation methods
    // ------------------------------------------------------------------------------------




    /**
     * @param scalar The factor to scale by
     */
    @Override
    public V scaled(double scalar) {
        return clone().scale(scalar);
    }

    /**
     * @param denominator The denominator to divide by
     */
    @Override
    public V divided(double denominator) throws ArithmeticException {
        return clone().divide(denominator);
    }

    @Override
    public V inverted() {
        return clone().invert();
    }

    @Override
    public V normed() {
        return clone().norm();
    }

    /**
     * @param vectors The vectors to add
     */
    @Override
    public V added(Vector... vectors) {
        return clone().add(vectors);
    }

    /**
     * @param vectors The vectors to subtract
     */
    @Override
    public V subtracted(Vector...vectors) {
        return clone().subtract(vectors);
    }

    @Override
    public V floored() {
        return clone().floor();
    }

    @Override
    public V ceiled() {
        return clone().ceil();
    }

    @Override
    public V rounded() {
        return clone().round();
    }




    
    // ------------------------------------------------------------------------------------
    // Conversion methods
    // ------------------------------------------------------------------------------------





    @Override
    public Vector2D get2D() throws UnsupportedOperationException {
        int size = size();
        if(size == 0) return new Vector2D();
        if(size == 1) return new Vector2D(getDim(X));
        return new Vector2D(getDim(X), getDim(Y));
    }

    @Override
    public Vector3D get3D() throws UnsupportedOperationException {
        int size = size();
        if(size == 0) return new Vector3D();
        if(size == 1) return new Vector3D(getDim(X));
        if(size == 2) return new Vector3D(getDim(X), getDim(Y));
        return new Vector3D(getDim(X), getDim(Y), getDim(Z));
    }



    
    // ------------------------------------------------------------------------------------
    // Saving methods
    // ------------------------------------------------------------------------------------



    
    @Override
    public String getSaveName() {
        return saveName != null ? saveName : getClass().getSimpleName().toLowerCase() + hashCode();
    }

    /**
     * @param name The new save name
     */
    @Override
    public void setSaveName(String name) {
        saveName = name;
    }



    
    // ------------------------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------------------------



    private void checkDim(int dimension) {
        if(dimension < 0 || dimension >= size()) throw new DimensionOutOfBoundsException(dimension, size());
    }

    @SuppressWarnings("unchecked")
    private V getThis() {
        return (V)this;
    }
}
