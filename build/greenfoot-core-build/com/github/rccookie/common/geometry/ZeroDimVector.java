package com.github.rccookie.common.geometry;

public class ZeroDimVector extends AbstractVector<ZeroDimVector> {

    private static final long serialVersionUID = -7810951969992884041L;

    private static final ZeroDimVector INSTANCE = new ZeroDimVector();

    private ZeroDimVector() { }

    @Override
    public ZeroDimVector clone() {
        return get();
    }

    @Override
    public double angle() {
        return Double.NaN;
    }

    @Override
    public double abs() {
        return 0;
    }

    @Override
    public ZeroDimVector add(Vector... vectors) {
        return this;
    }

    @Override
    public ZeroDimVector ceil() {
        return this;
    }

    @Override
    public ZeroDimVector divide(double denominator) {
        return this;
    }

    @Override
    public double dot(Vector vector) throws NullPointerException {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Vector)) return false;
        return ((Vector)obj).isZero();
    }

    @Override
    public ZeroDimVector floor() {
        return this;
    }

    @Override
    public double getDim(int dimension) {
        return 0;
    }

    @Override
    public Vector2D get2D() {
        return new Vector2D();
    }

    @Override
    public Vector3D get3D() {
        return new Vector3D();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public ZeroDimVector invert() {
        return this;
    }

    @Override
    public boolean isZero() {
        return true;
    }

    @Override
    public ZeroDimVector norm() {
        return this;
    }

    @Override
    public ZeroDimVector round() {
        return this;
    }

    @Override
    public ZeroDimVector scale(double scalar) {
        return this;
    }

    @Override
    public ZeroDimVector set(Vector vector) {
        return this;
    }

    @Override
    public ZeroDimVector setDim(int dimension, double coordinate)
            throws UnsupportedOperationException, DimensionOutOfBoundsException {
        throw new DimensionOutOfBoundsException(dimension, 0);
    }

    @Override
    public ZeroDimVector setX(double x) throws UnsupportedOperationException, DimensionOutOfBoundsException {
        throw new DimensionOutOfBoundsException(X, 0);
    }

    @Override
    public ZeroDimVector setY(double y) throws UnsupportedOperationException, DimensionOutOfBoundsException {
        throw new DimensionOutOfBoundsException(Y, 0);
    }

    @Override
    public ZeroDimVector setZero() throws UnsupportedOperationException {
        return this;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public double sqrAbs() {
        return 0;
    }

    @Override
    public ZeroDimVector subtract(Vector... vectors) {
        return this;
    }

    @Override
    public double[] toArray() {
        return new double[0];
    }

    @Override
    public double x() {
        return 0;
    }

    @Override
    public double y() {
        return 0;
    }

    @Override
    public String toString() {
        return "[]";
    }



    public static final ZeroDimVector get() {
        return INSTANCE;
    }
}
