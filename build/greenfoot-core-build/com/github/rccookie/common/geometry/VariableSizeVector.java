package com.github.rccookie.common.geometry;

class VariableSizeVector extends AbstractVector<VariableSizeVector> {

    private static final long serialVersionUID = 7925371843942953657L;

    public VariableSizeVector(double... coordinates) {
        super(coordinates);
    }

    @Override
    public VariableSizeVector clone() {
        return new VariableSizeVector(toArray());
    }

    @Override
    public double angle() {
        throw new UnsupportedOperationException("Variable size vectors do not support calculating the angle");
    }
}
