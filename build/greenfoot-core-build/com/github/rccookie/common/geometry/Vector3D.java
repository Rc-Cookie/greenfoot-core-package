package com.github.rccookie.common.geometry;

/**
 * A vector represents a change in location, often also expressed as
 * an arrow or a movement. Every vector consists of a <i>x</i>, <i>y</i> and
 * <i>z</i> coordinate which represent the difference in the corresponding
 * direction.
 * <p>Vectors allow easy manipulation and share all common mathmatical
 * interactions such as absolute(length), normalization, addition and subtraction,
 * scalar and vector multiplication and more. Also the angle of the vector can
 * be measured relative to another vector or the x axis in form of a rotation
 * object.
 * <p>Vectors are commonly used for any kind of movement representation, but
 * also for other geometric effects. One example is to save the location of
 * an object using a vector which represents the distance from the point of
 * origin to the object.
 * 
 * @version 2.0
 */
public class Vector3D extends AbstractVector<Vector3D> {

    private static final long serialVersionUID = -4497796665392993666L;

    /**
     * A vector with the length zero.
     */
    public static final Vector3D ZERO_VECTOR = new Vector3D();

    /**
     * A vector pointing along the x axis with the length of 1. Representation
     * of the x axis.
     */
    public static final Vector3D UNIT_VECTOR_X = new Vector3D(1);

    /**
     * A vector pointing along the y axis with the length of 1. Representation
     * of the y axis.
     */
    public static final Vector3D UNIT_VECTOR_Y = new Vector3D(0, 1);

    /**
     * A vector pointing along the z axis with the length of 1. Representation
     * of the zaxis.
     */
    public static final Vector3D UNIT_VECTOR_Z = new Vector3D(0, 0, 1);

    





    /**
     * Creates a new zero vector.
     */
    public Vector3D() {
        this(0, 0, 0);
    }

    /**
     * Creates a new vector with the length {@code x} in the direction of the x axis.
     * <p>This does exactly the same as invoking {@code new Vector(x, 0, 0);}.
     * 
     * @param x The length of the vector in x direction
     */
    public Vector3D(double x) {
        this(x, 0, 0);
    }

    /**
     * Creates a new vector with the given x and y magnitude. This essentially creates
     * a 2D vector.
     * <p>This does exactly the same as invoking {@code new Vector(x, y, 0);}.
     * 
     * @param x The length of the vector in x direction
     * @param y The length of the vector in y direction
     */
    public Vector3D(double x, double y) {
        this(x, y, 0);
    }

    /**
     * Creates a new vector eith the given x, y and z magnitude.
     * 
     * @param x The length of the vector in x direction
     * @param y The length of the vector in y direction
     * @param z The length of the vector in z direction
     */
    public Vector3D(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Creates a new Vector from the given vector. In other words, this
     * creates a copy of the given vector so that for {@code Vector v}
     * {@code v.equals(new Vector(v))} will be true and {@code v == new Vector(v)}
     * will be false.
     * 
     * @param copy The vector to create a copy from
     */
    public Vector3D(AbstractVector<?> copy) {
        // 3D conversion is essential for super class to create a 3 long array
        super(copy.get3D());
    }









    @Override
    public double angle() {
        return angleBetween(this, UNIT_VECTOR_X);
    }

    @Override
    public Vector3D clone() {
        return new Vector3D(this);
    }







    public double z() { return get(Z); }

    public Vector3D setZ(double z) {
        return setDim(Z, z);
    }

    public Vector3D set(double x, double y, double z) {
        return setX(x).setY(y).setZ(z);
    }









    /**
     * Returns a rotation object containing information about the direction the vector is pointing
     * in 3D space.
     * <p>Both z and y rotation will be within {@code -180}(exclusive) and {@code 180}(inclusive).
     * <p>The x rotation of the vector will always be {@code 0}.
     * 
     * @return The rotation of this vector
     */
    public Rotation rotation() {
        return rotation(this);
    }


    /**
     * Returns the <b>smallest</b> angle between this and the given vector in degrees.
     * <p>The angle returned will be within {@code 0} and {@code 180}(inclusive).
     * 
     * @param v The vector to get the angle to
     * @return The angle between this and {@code v}
     */
    public double angleTo(Vector3D v) {
        return smallestAngle(this, v);
    }









    public Vector3D rotate(Rotation rotation) {
        if(rotation != null) {
            Vector3D rotated = rotated(rotation);
            set(rotated);
        }
        return this;
    }

    public Vector3D rotate(double angle) {
        if(angle % 360 == 0) return this;
        if(angle % 180 == 0) return invert();
        return rotated(angle);
    }

    


    public Vector3D rotated(double angle) {
        return angledVector(angle() + angle, abs());
    }

    public Vector3D rotated(Rotation rotation) {
        return angledVector(rotation().add(rotation), abs());
    }

    public Vector3D orthogonal() {
        if(equals(ZERO_VECTOR)) return new Vector3D(1);
        if(rotation().equals(new Rotation())) {
            return UNIT_VECTOR_Y.scaled(abs());
        }
        return cross(this, UNIT_VECTOR_X).norm().scale(abs());
    }









    public static Vector3D angledVector(Rotation rotation, double length) {
        return angledVector(rotation.z, rotation.y, length);
    }

    public static Vector3D angledVector(double angle, double length) {
        return angledVector(angle, 0, length);
    }

    public static Vector3D angledVector(double angleZ, double angleY, double length) {
        return new Vector3D(
            length * Math.cos(Math.toRadians(angleZ)) * Math.cos(Math.toRadians(angleY)),
            length * Math.sin(Math.toRadians(angleZ)) * Math.cos(Math.toRadians(angleY)),
            length * Math.sin(Math.toRadians(angleY))
        );
    }


    public static Vector3D between(Vector3D from, Vector3D to) {
        return to.subtracted(from);
    }

    public static double distance(Vector3D v, Vector3D w) {
        return between(v, w).abs();
    }

    
    public static Vector3D average(Vector3D... vectors) {
        if(vectors.length == 0) return null;
        Vector3D average = new Vector3D();
        for(Vector3D v : vectors) average.add(v);
        return average.divide(vectors.length);
    }




    public static double abs(Vector3D v) {
        return Math.sqrt(dot(v, v));
    }

    public static Rotation rotation(Vector3D v) {
        Vector3D onX = new Vector3D(v.x(), v.y());
        return new Rotation(angleBetween(onX, UNIT_VECTOR_X), angleBetween(onX, v));
    }

    public static double angleBetween(Vector3D v, Vector3D w) {
        Vector3D crossP = cross(v, w);
        return crossP.x() + crossP.y() + crossP.z() > 0 ? -smallestAngle(v, w) : smallestAngle(v, w);
    }

    public static double smallestAngle(Vector3D v, Vector3D w) {
        if(v == null || w == null) throw new NullPointerException();
        if(v.equals(ZERO_VECTOR) || w.equals(ZERO_VECTOR)) return 0;
        return Math.toDegrees(Math.acos(dot(v, w) / (v.abs() * w.abs())));
    }



    public static Vector3D add(Vector3D v, Vector3D w) {
        return v.added(w);
    }

    public static double dot(Vector3D v, Vector3D w) {
        return v.dot(w);
    }

    public static Vector3D cross(Vector3D v, Vector3D w) {
        return new Vector3D(
            v.y() * w.z() - v.z() * w.y(),
            v.z() * w.x() - v.x() * w.z(),
            v.x() * w.y() - v.y() * w.x()
        );
    }

    public static double area(Vector3D v, Vector3D w){
        return cross(v, w).abs();
    }

    public static Vector3D shortestBetween(Ray3D ray, Vector3D point) {
        double r = (
            ray.direction().x() * (point.x() - ray.root().x()) + 
            ray.direction().y() * (point.y() - ray.root().y()) + 
            ray.direction().z() * (point.z() - ray.root().z())) /
            dot(ray.direction(), ray.direction()
        );
        return ray.root().added(ray.direction().scaled(r)).subtract(point);
    }

    public static double distanceBetween(Ray3D line, Vector3D point) {
        return shortestBetween(line, point).abs();
    }

    public static Vector3D mirror(Vector3D mirroredVector, Vector3D mirror) {
        return mirroredVector.added(shortestBetween(new Ray3D(mirror), mirroredVector).scale(2));
    }

    public static Vector3D reflect(Vector3D base, Vector3D reflect){
        return mirror(reflect, base).invert();
    }

    public static Vector3D project(Vector3D vectorToProject, Vector3D onto){
        return new Vector3D(onto).scale(dot(vectorToProject, onto) / (onto.abs() * onto.abs()));
    }
}
