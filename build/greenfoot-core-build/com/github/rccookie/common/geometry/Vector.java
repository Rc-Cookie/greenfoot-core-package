package com.github.rccookie.common.geometry;

import java.util.Objects;

import com.github.rccookie.common.data.Saveable;

public interface Vector extends Cloneable, Saveable {



    // ------------------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------------------

    /**
     * The index of the coordinate X.
     */
    public static final int X = 0;

    /**
     * The index of the coordinate Y.
     */
    public static final int Y = 1;

    /**
     * The index of the coordinate Z.
     */
    public static final int Z = 2;



    // ------------------------------------------------------------------------------------
    // Base methods
    // ------------------------------------------------------------------------------------



    /**
     * Returns the coordinate in the specified dimension of this vector. If this vector does not specify
     * the given dimension, {@code 0} will be returned.
     * 
     * @param dimension The dimension to get the coordinate for
     * @return The coordinate in the specified dimension
     * @see #x()
     * @see #y()
     * @see #setDim(int, double)
     */
    public double get(int dimension);

    /**
     * Sets the coordinate in the specified dimension of this vector to the specified value.
     * 
     * @param dimension The dimension to set the coordinate for
     * @param value The new coordinate in the specified dimension of this vector
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #setX()
     * @see #setY()
     * @see #get(int)
     */
    public Vector setDim(int dimension, double coordinate) throws UnsupportedOperationException, DimensionOutOfBoundsException;

    /**
     * Returns the number of dimensions stored in this vector.
     * 
     * @return The number of dimensions supported by this vector
     */
    public int size();

    /**
     * Returns a copy of this vector so that for a vector {@code v} the statement
     * <blockquote>
     * <pre>v.equals(v.clone())</pre>
     * </blockquote>
     * is {@code true} and
     * <blockquote>
     * <pre>v == v.clone()</pre>
     * </blockquote>
     * is {@code false}.
     * 
     * @return A copy of this vector
     */
    Vector clone() throws CloneNotSupportedException;

    

    /**
     * Returns a string representation of this vector with the followin syntax:
     * <blockquote><pre>"[x|y|...]"</pre></blockquote>
     * For example, {@code new Vector2D(2, 3).toString();} would return
     * <blockquote><pre>"[2.0|3.0]"</pre></blockquote>
     * 
     * @return A string representation of this vector
     */
    @Override
    public String toString();

    /**
     * Returns, weather the given object is equal to this vector.
     * <p>An object is considered to be equal to a vector if that object ia a subclass of
     * {@link AbstractVector}, they have the same coordinates for every dimension they share and
     * any other dimension of a possibly differently sized vector is {@code 0}.
     * 
     * @param obj The object to check for equality
     * @return Weather the object is equal to this vector
     */
    @Override
    public boolean equals(Object obj);

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
     * <p>
     * The hashcode of a vector is defined as the (afterwards) rounded sum
     * of its coordinates.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link java.lang.Object#equals(java.lang.Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     * </ul>
     * 
     * @return A hash code value for this vector
     * @see java.lang.Object#equals(Object) Object.equals(Object)
     * @see java.lang.System#identityHashCode
     */
    @Override
    public int hashCode();




    // ------------------------------------------------------------------------------------
    // Get / set methods
    // ------------------------------------------------------------------------------------




    /**
     * Returns the x-coordinate of this vector.
     * 
     * @return The x-coordinate of this vector
     * @see #get(int)
     */
    public double x();

    /**
     * Returns the y-coordinate of this vector.
     * 
     * @return The y-coordinate of this vector
     * @see #get(int)
     */
    public double y();

    /**
     * Returns an array with the length corresponding to the number of dimensions in
     * this vector and filled with the coordinates in each dimensions.
     * <p>This array should not be the actual array that may be used to store the
     * dimensions of the vector internally.
     * 
     * @return An array containing the coordinates of this vector
     */
    public double[] toArray();

    /**
     * Sets the x-coordinate of this vector to the specified one.
     * 
     * @param x The new x-coordinate for this vector
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @throws DimensionOutOfBoundsException If this vector does not have any dimensions
     * @see #setDim(int, double)
     */
    public Vector setX(double x) throws UnsupportedOperationException, DimensionOutOfBoundsException;

    /**
     * Sets the y-coordinate of this vector to the specified one.
     * 
     * @param y The new y-coordinate for this vector
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @throws DimensionOutOfBoundsException If this vector does not have 2 dimensions
     * @see #setDim(int, double)
     */
    public Vector setY(double y) throws UnsupportedOperationException, DimensionOutOfBoundsException;

    /**
     * Sets all of this vector's coordinates to the coordinates of the given vector. If the given
     * vector has less coordinates than this vector, those coordinates will stay unchanged.
     * 
     * @param vector The vector to set this vector's coordinates to
     * @return This vector
     * @throws NullPointerException If the specified vector is {@code null}
     */
    public Vector set(Vector vector) throws NullPointerException;




    // ------------------------------------------------------------------------------------
    // Property get methods
    // ------------------------------------------------------------------------------------




    /**
     * Returns the absolute length of this vector, also known as its magnitude.
     * 
     * @return The length of this vector
     * @see #sqrAbs()
     */
    public double abs();

    /**
     * Returns the squared length of this vector. This method is intended for comparing
     * the sizes of two vectors without having to take a square root.
     * 
     * @return The squared/ un-rooted length of this vector
     * @see #abs()
     */
    public double sqrAbs();

    /**
     * Returns the angle between this vector and the x-axis, in degrees. This is
     * direction-sensitive so the result should have a range from -180° to 180°.
     * 
     * @return The angle between this vector and the x-axis
     */
    public abstract double angle();

    /**
     * Returns the dot product of this and the given vector.
     * 
     * @param vector The other vector
     * @return The dot protuct of this and the other vector
     * @throws NullPointerException If the given vector is null
     */
    public double dot(Vector vector) throws NullPointerException;

    /**
     * Returns {@code true} if and only if all coordinates of this vector and thus
     * also its magnitude is {@code 0}.
     * 
     * @return Weather this vector is a zero vector
     */
    public boolean isZero();




    
    // ------------------------------------------------------------------------------------
    // Vector operation methods
    // ------------------------------------------------------------------------------------




    /**
     * Scales this vector by the given scalar.
     * 
     * @param scalar The factor to scale by
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #scaled(double)
     */
    public Vector scale(double scalar) throws UnsupportedOperationException;

    /**
     * Divides this vector by the given denominator and throws an ArithmeticException
     * if the denominator is {@code 0}.
     * 
     * @param denominator The denominator to divide by
     * @return This vector
     * @throws ArithmeticException If the denominator is {@code 0}
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #divided(double)
     */
    public Vector divide(double denominator) throws ArithmeticException, UnsupportedOperationException;

    /**
     * Invertes this vector so that its length stays identical but all of its coordinates
     * are scaled by {@code -1}.
     * 
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #inverted()
     */
    public Vector invert() throws UnsupportedOperationException;

    /**
     * Normalizes this vector so that its orientation stays unchanged and its legnth is
     * set to {@code 1}. If this vector is a zero vector than it will stay a zero vector.
     * 
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #isZero()
     * @see #normed()
     */
    public Vector norm() throws UnsupportedOperationException;

    /**
     * Sets all of this vector's coordinates to {@code 0}. Has the same effect as scaling
     * by {@code 0}.
     * 
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #scale(double)
     */
    public Vector setZero() throws UnsupportedOperationException;




    /**
     * Adds the given vectors onto this one by adding each of the other vector's coordinates
     * onto this vector's coordinates. If a given vector has less dimensions than this
     * vector, this vector's corresponding coordinate will stay unchanged.
     * 
     * @param vectors The vectors to add
     * @return This vector
     * @see #added(AbstractVector...)
     */
    public Vector add(Vector... vectors);

    /**
     * Subtracts the given vectors from this one by subtracting each of the other vector's
     * coordinates from this vector's coordinates. If a given vector has less dimensions than
     * this vector, this vector's corresponding coordinate will stay unchanged.
     * 
     * @param vectors The vectors to subtract
     * @return This vector
     * @see #subtracted(AbstractVector...)
     */
    public Vector subtract(Vector... vectors);



    /**
     * Floors all components of this vector.
     * 
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #floored()
     */
    public Vector floor() throws UnsupportedOperationException;

    /**
     * Ceils all components of this vector.
     * 
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #ceiled()
     */
    public Vector ceil() throws UnsupportedOperationException;

    /**
     * Rounds all components of this vector.
     * 
     * @return This vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #rounded()
     */
    public Vector round() throws UnsupportedOperationException;




    // ------------------------------------------------------------------------------------
    // Cloned-operation methods
    // ------------------------------------------------------------------------------------




    /**
     * Returns a scaled copy of this vector.
     * 
     * @param scalar The factor to scale by
     * @return A scaled version of this vector
     * @see #scale(double)
     * @see #clone()
     */
    public Vector scaled(double scalar);

    /**
     * Returns a divided copy of this vector.
     * 
     * @param denominator The denominator to divide by
     * @return A divided version of this vector
     * @throws ArithmeticException If the denominator is {@code 0}
     * @see #divide(double)
     * @see #clone()
     */
    public Vector divided(double denominator) throws ArithmeticException;

    /**
     * Returns a inverted copy of this vector.
     * 
     * @return A inverted version of this vector
     * @see #invert()
     * @see #clone()
     */
    public Vector inverted();

    /**
     * Returns a normalized copy of this vector. If this vector is a zero vector, then the
     * result will be another zero vector.
     * 
     * @return A normalized version of this vector
     * @see #isZero()
     * @see #norm()
     * @see #clone()
     */
    public Vector normed();

    /**
     * Returns a copy of this vector and all the given vectors added together.
     * 
     * @param vectors The vectors to add
     * @return A copy of this vectors with the vectors added
     * @see #add(AbstractVector...)
     * @see #clone()
     */
    public Vector added(Vector... vectors);

    /**
     * Returns a copy of this vector with all the given vectors subtracted from it.
     * 
     * @param vectors The vectors to subtract
     * @return A copy of this vectors with the vectors subtracted
     * @see #subtract(AbstractVector...)
     * @see #clone()
     */
    public Vector subtracted(Vector...vectors);

    /**
     * Returns a floored copy of this vector.
     * 
     * @return A floored version of this vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #floor()
     * @see #clone()
     */
    public Vector floored();

    /**
     * Returns a ceiled copy of this vector.
     * 
     * @return A ceiled version of this vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #ceil()
     * @see #clone()
     */
    public Vector ceiled();

    /**
     * Returns a rounded copy of this vector.
     * 
     * @return A rounded version of this vector
     * @throws UnsupportedOperationException If this vector is immutable
     * @see #round()
     * @see #clone()
     */
    public Vector rounded();




    
    // ------------------------------------------------------------------------------------
    // Conversion methods
    // ------------------------------------------------------------------------------------





    /**
     * Returns a Vector2D representing this vector as good as possible.
     * 
     * @return A Vector2D representing this vector
     * @throws UnsupportedOperationException If this vector does not support 2D conversion
     * @see Vector2D
     */
    public Vector2D get2D() throws UnsupportedOperationException;

    /**
     * Returns a Vector3D representing this vector as good as possible.
     * 
     * @return A Vector3D representing this vector
     * @throws UnsupportedOperationException If this vector does not support 3D conversion
     * @see Vector3D
     */
    public Vector3D get3D() throws UnsupportedOperationException;



    
    // ------------------------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------------------------




    /**
     * An exception indicating that a dimension of a vector was requested that was either negative
     * or out of range for the vectors dimensions.
     */
    public static class DimensionOutOfBoundsException extends IndexOutOfBoundsException {
        private static final long serialVersionUID = -7930115924262565512L;
        /**
         * Creates a new DimensionOutOfBoundsException.
         * 
         * @param dimension The dimension that was requested
         * @param size The actual size of the vector
         */
        protected DimensionOutOfBoundsException(int dimension, int size) {
            super("The dimension " + dimension + " is not available for a vector with " + size + " dimensions");
        }
    }




    // ------------------------------------------------------------------------------------
    // Statics
    // ------------------------------------------------------------------------------------




    /**
     * Returns the vector that starts at the location of {@code from} and points at the location
     * of {@code to}.
     * 
     * @param from The starting location
     * @param to The ending location
     * @return The vector connecting the two vectors
     */
    public static Vector between(Vector from, Vector to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if(from.size() > to.size()) return from.inverted().add(to);
        return to.subtracted(from);
    }
}
