package com.github.rccookie.common.geometry;

public final class Vectors {
    private Vectors() { }

    public static final Vector immutableVector(Vector base) {
        return new ImmutableVector(base);
    }

    public static final Vector2D immutableVector(Vector2D base) {
        return new ImmutableVector2D(base);
    }

    public static final Vector3D immutableVector(Vector3D base) {
        return new ImmutableVector3D(base);
    }



    private static class ImmutableVector implements Vector {

        private static final long serialVersionUID = -3209379792678205620L;

        private final Vector base;

        public ImmutableVector(Vector base) {
            this.base = base;
        }

        @Override
        public double get(int dimension) {
            return base.get(dimension);
        }

        @Override
        public Vector setDim(int dimension, double coordinate)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return base.size();
        }

        @Override
        public Vector clone() throws CloneNotSupportedException {
            return new ImmutableVector(base);
        }

        @Override
        public double x() {
            return base.x();
        }

        @Override
        public double y() {
            return base.y();
        }

        @Override
        public double[] toArray() {
            return base.toArray();
        }

        @Override
        public Vector setX(double x) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector setY(double y) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector set(Vector vector) throws NullPointerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public double abs() {
            return base.abs();
        }

        @Override
        public double sqrAbs() {
            return base.sqrAbs();
        }

        @Override
        public double angle() {
            return base.angle();
        }

        @Override
        public double dot(Vector vector) throws NullPointerException {
            return base.dot(vector);
        }

        @Override
        public boolean isZero() {
            return base.isZero();
        }

        @Override
        public Vector scale(double scalar) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector divide(double denominator) throws ArithmeticException, UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector invert() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector norm() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector setZero() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector add(Vector... vectors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector subtract(Vector... vectors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector floor() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector ceil() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector round() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector scaled(double scalar) {
            return new ImmutableVector(base.scaled(scalar));
        }

        @Override
        public Vector divided(double denominator) throws ArithmeticException {
            return new ImmutableVector(base.divided(denominator));
        }

        @Override
        public Vector inverted() {
            return new ImmutableVector(base.inverted());
        }

        @Override
        public Vector normed() {
            return new ImmutableVector(base.normed());
        }

        @Override
        public Vector added(Vector... vectors) {
            return new ImmutableVector(base.added(vectors));
        }

        @Override
        public Vector subtracted(Vector... vectors) {
            return new ImmutableVector(base.subtracted(vectors));
        }

        @Override
        public Vector floored() {
            return new ImmutableVector(base.floored());
        }

        @Override
        public Vector ceiled() {
            return new ImmutableVector(base.ceiled());
        }

        @Override
        public Vector rounded() {
            return new ImmutableVector(base.rounded());
        }

        @Override
        public Vector2D get2D() throws UnsupportedOperationException {
            return new ImmutableVector2D(base.get2D());
        }

        @Override
        public Vector3D get3D() throws UnsupportedOperationException {
            return new ImmutableVector3D(base.get3D());
        }
    }


    private static class ImmutableVector2D extends Vector2D {

        private static final long serialVersionUID = -570640519112991578L;

        private final Vector2D base;

        public ImmutableVector2D(Vector2D base) {
            this.base = base;
        }

        @Override
        public double get(int dimension) {
            return base.get(dimension);
        }

        @Override
        public Vector2D setDim(int dimension, double coordinate)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return base.size();
        }

        @Override
        public Vector2D clone() {
            return new ImmutableVector2D(base);
        }

        @Override
        public double x() {
            return base.x();
        }

        @Override
        public double y() {
            return base.y();
        }

        @Override
        public double[] toArray() {
            return base.toArray();
        }

        @Override
        public Vector2D setX(double x) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D setY(double y) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D set(Vector2D vector) throws NullPointerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public double abs() {
            return base.abs();
        }

        @Override
        public double sqrAbs() {
            return base.sqrAbs();
        }

        @Override
        public double angle() {
            return base.angle();
        }

        @Override
        public double dot(Vector vector) throws NullPointerException {
            return base.dot(vector);
        }

        @Override
        public boolean isZero() {
            return base.isZero();
        }

        @Override
        public Vector2D scale(double scalar) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D divide(double denominator) throws ArithmeticException, UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D invert() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D norm() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D setZero() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D add(Vector... vectors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D subtract(Vector... vectors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D floor() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D ceil() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D round() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector2D scaled(double scalar) {
            return new ImmutableVector2D(base.scaled(scalar));
        }

        @Override
        public Vector2D divided(double denominator) throws ArithmeticException {
            return new ImmutableVector2D(base.divided(denominator));
        }

        @Override
        public Vector2D inverted() {
            return new ImmutableVector2D(base.inverted());
        }

        @Override
        public Vector2D normed() {
            return new ImmutableVector2D(base.normed());
        }

        @Override
        public Vector2D added(Vector... vectors) {
            return new ImmutableVector2D(base.added(vectors));
        }

        @Override
        public Vector2D subtracted(Vector... vectors) {
            return new ImmutableVector2D(base.subtracted(vectors));
        }

        @Override
        public Vector2D floored() {
            return new ImmutableVector2D(base.floored());
        }

        @Override
        public Vector2D ceiled() {
            return new ImmutableVector2D(base.ceiled());
        }

        @Override
        public Vector2D rounded() {
            return new ImmutableVector2D(base.rounded());
        }

        @Override
        public Vector2D get2D() throws UnsupportedOperationException {
            return new ImmutableVector2D(base.get2D());
        }

        @Override
        public Vector3D get3D() throws UnsupportedOperationException {
            return new ImmutableVector3D(base.get3D());
        }
    }


    private static class ImmutableVector3D extends Vector3D {

        private static final long serialVersionUID = -570640519112991578L;

        private final Vector3D base;

        public ImmutableVector3D(Vector3D base) {
            this.base = base;
        }

        @Override
        public double get(int dimension) {
            return base.get(dimension);
        }

        @Override
        public Vector3D setDim(int dimension, double coordinate)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return base.size();
        }

        @Override
        public Vector3D clone() {
            return new ImmutableVector3D(base);
        }

        @Override
        public double x() {
            return base.x();
        }

        @Override
        public double y() {
            return base.y();
        }

        @Override
        public double[] toArray() {
            return base.toArray();
        }

        @Override
        public Vector3D setX(double x) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D setY(double y) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D set(Vector vector) throws NullPointerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public double abs() {
            return base.abs();
        }

        @Override
        public double sqrAbs() {
            return base.sqrAbs();
        }

        @Override
        public double angle() {
            return base.angle();
        }

        @Override
        public double dot(Vector vector) throws NullPointerException {
            return base.dot(vector);
        }

        @Override
        public boolean isZero() {
            return base.isZero();
        }

        @Override
        public Vector3D scale(double scalar) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D divide(double denominator) throws ArithmeticException, UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D invert() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D norm() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D setZero() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D add(Vector... vectors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D subtract(Vector... vectors) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D floor() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D ceil() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D round() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Vector3D scaled(double scalar) {
            return new ImmutableVector3D(base.scaled(scalar));
        }

        @Override
        public Vector3D divided(double denominator) throws ArithmeticException {
            return new ImmutableVector3D(base.divided(denominator));
        }

        @Override
        public Vector3D inverted() {
            return new ImmutableVector3D(base.inverted());
        }

        @Override
        public Vector3D normed() {
            return new ImmutableVector3D(base.normed());
        }

        @Override
        public Vector3D added(Vector... vectors) {
            return new ImmutableVector3D(base.added(vectors));
        }

        @Override
        public Vector3D subtracted(Vector... vectors) {
            return new ImmutableVector3D(base.subtracted(vectors));
        }

        @Override
        public Vector3D floored() {
            return new ImmutableVector3D(base.floored());
        }

        @Override
        public Vector3D ceiled() {
            return new ImmutableVector3D(base.ceiled());
        }

        @Override
        public Vector3D rounded() {
            return new ImmutableVector3D(base.rounded());
        }

        @Override
        public ImmutableVector2D get2D() throws UnsupportedOperationException {
            return new ImmutableVector2D(base.get2D());
        }

        @Override
        public Vector3D get3D() throws UnsupportedOperationException {
            return new ImmutableVector3D(base.get3D());
        }
    }
}
