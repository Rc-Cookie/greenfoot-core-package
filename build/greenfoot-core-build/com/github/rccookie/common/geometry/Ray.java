package com.github.rccookie.common.geometry;

import com.github.rccookie.common.data.Saveable;

public interface Ray extends Cloneable, Saveable {

    public Vector get(double r);

    public Vector root();

    public Vector direction();

    public int size();



    public default Ray2D get2D() {
        return new Ray2D(root().get2D(), direction().get2D());
    }

    public default Ray3D get3D() {
        return new Ray3D(root().get3D(), direction().get3D());
    }



    /**
     * Calculates weather and if so where the two given rays intersect.
     * 
     * @param a The first ray
     * @param b The second ray
     * @return A two dimensional array containing the two distances from the rays' roots measured
     * in their direction vectors, or {@code null} if they are parallel
     */
    public static double[] intersection(Ray a, Ray b) {
        double aHit = a.direction().x() * a.root().y() - a.direction().y() * a.root().x();
        aHit += a.direction().y() * b.root().x() - a.direction().x() * b.root().y();
        aHit /= a.direction().x() * b.direction().y() - a.direction().y() * b.direction().x();

        if(Double.isNaN(aHit)) return null;

        double bHit = b.direction().x() * b.root().y() - b.direction().y() * b.root().x();
        bHit += b.direction().y() * a.root().x() - b.direction().x() * a.root().y();
        bHit /= b.direction().x() * a.direction().y() - b.direction().y() * a.direction().x();

        return new double[] {bHit, aHit};
    }
}
