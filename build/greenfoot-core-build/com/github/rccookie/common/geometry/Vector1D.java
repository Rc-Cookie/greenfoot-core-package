package com.github.rccookie.common.geometry;

public class Vector1D extends AbstractVector<Vector1D> {

    private static final long serialVersionUID = 1119286347023474308L;

    public Vector1D(double x) {
        super(x);
    }

    @Override
    public Vector1D clone() {
        return new Vector1D(x());
    }

    @Override
    public double angle() {
        return x() >= 0 ? 0 : 180;
    }

    @Override
    public double abs() {
        return x();
    }

    @Override
    public Vector1D invert() {
        return setX(-x());
    }
    
    @Override
    public int size() {
        return 1;
    }

    @Override
    public double[] toArray() {
        return new double[] { x() };
    }

    @Override
    public Vector1D setY(double y) throws UnsupportedOperationException, DimensionOutOfBoundsException {
        throw new DimensionOutOfBoundsException(Y, 1);
    }

    @Override
    public double y() {
        return 0;
    }

    @Override
    public String toString() {
        return "[" + x() + "]";
    }

    @Override
    public double sqrAbs() {
        return x() * x();
    }
}
