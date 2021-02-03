package com.github.rccookie.common.geometry;

import java.util.Arrays;
import java.util.Objects;

public final class Raycast {
    private Raycast() { }

    /**
     * Calculates a raycast from the given ray taking all given edges into consideration. This
     * method will never return null but rather a result describing a non-hit.
     * 
     * @param ray The ray cast
     * @param maxLength The maximum distance of the raycast. Has no performance impact
     * @param edges The edges that the ray may hit
     * @return A RaycastResult2D describing the outcome of the raycast
     * @throws NullPointerException If {@code ray} is {@code null}
     * @see Raycast2D
     */
    public static final Raycast2D raycast2D(final Ray2D ray, double maxLength, final Iterable<Edge2D> edges) {
        Objects.requireNonNull(ray, "The given ray must not be null.");
        Raycast2D closest = new Raycast2D(Double.NaN, Double.NaN, null, ray, maxLength);
        if(edges == null) return closest;

        for(Edge2D edge : edges) {
            Raycast2D thisResult = raycast2D(ray, edge, maxLength);
            if(thisResult != null && thisResult.length < closest.length) closest = thisResult;
        }

        return closest;
    }

    /**
     * Calculates a raycast from the given ray taking all given edges into consideration. This
     * method will never return null but rather a result describing a non-hit.
     * 
     * @param ray The ray cast
     * @param maxLength The maximum distance of the raycast. Has no performance impact
     * @param edges The edges that the ray may hit
     * @return A RaycastResult2D describing the outcome of the raycast
     * @throws NullPointerException If {@code ray} is {@code null}
     * @see Raycast2D
     */
    public static final Raycast2D raycast2D(final Ray2D ray, double maxLength, final Edge2D... edges) {
        return raycast2D(ray, maxLength, Arrays.asList(edges));
    }


    private static final Raycast2D raycast2D(final Ray2D ray, final Edge2D edge, double maxLength) {
        double[] intersection = Edge.intersection(edge, ray);
        if(intersection == null || intersection[1] < 0) return null;
        return new Raycast2D(intersection[0], intersection[1], edge, ray, maxLength);
    }



    public static final class Raycast2D {
        /**
         * The edge that was hit by the ray. May be {@code null} if the hit did
         * not hit anything.
         */
        public final Edge2D hitEdge;

        /**
         * A ray describing the actual ray starting at the root and ending at the
         * hit location. May be {@code null} if the ray did not hit anything.
         */
        public final Edge2D connection;

        /**
         * The ray that this raycast was based on.
         */
        public final Ray2D ray;

        /**
         * The length of the ray from its root until the hit location. If the ray
         * did not hit anything, this will have the value of {@link Double#POSITIVE_INFINITY}
         */
        public final double length;

        /**
         * The index of the edge where it was hit. If the ray did not hit anything
         * this will have the value of {@link Double#NaN}.
         */
        public final double edgeIndex;

        /**
         * The index of the ray where it hit an edge. If the ray did not hit anything
         * this will have the value of {@link Double#POSITIVE_INFINITY}.
         */
        public final double rayIndex;

        /**
         * A vector describing the location of the ray hit. May be {@code null} if
         * the ray did not hit anything.
         */
        public final Vector2D hitLoc;

        /**
         * The root of the ray.
         */
        public final Vector2D root;

        private Raycast2D(double edgeIndex, double rayIndex, Edge2D hitEdge, Ray2D ray, double maxLength) {
            this.root = ray.root();
            this.ray = ray;
            this.hitEdge = hitEdge;
            this.edgeIndex = edgeIndex;
            this.rayIndex = rayIndex;
            hitLoc = hitEdge != null ? hitEdge.get(edgeIndex) : null;
            connection = hitLoc != null ? new Edge2D(root, Vector2D.between(root, hitLoc)) : null;
            length = connection != null ? connection.length() : maxLength;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Raycast2D)) return false;
            Raycast2D o = (Raycast2D)obj;
            return Objects.equals(root, o.root) && Objects.equals(hitEdge, o.hitEdge) && edgeIndex == o.edgeIndex && rayIndex == o.rayIndex;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "Result{Edge hit: " + hitEdge + ", location: " + hitLoc + ", length: " + length + "}";
        }


        private Raycast2D(Ray2D ray, double length) {
            this.root = ray.root();
            this.ray = ray;
            hitEdge = null;
            edgeIndex = Double.NaN;
            rayIndex = Double.NaN;
            hitLoc = null;
            connection = null;
            this.length = length;
        }

        public static final Raycast2D emptyResult(Ray2D ray, double length) {
            return new Raycast2D(Double.NaN, Double.NaN, null, ray, length);
        }
    }
}
