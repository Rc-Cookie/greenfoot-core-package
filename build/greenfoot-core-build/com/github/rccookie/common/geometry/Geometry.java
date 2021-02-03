package com.github.rccookie.common.geometry;

import java.util.Objects;

public final class Geometry {
    private Geometry() {}

    /**
     * Floors the given {@code double} to the next closest one from the specified digit
     * on. This means that {@code floor(x, 0)} returns exactly the same as {@code Math.floor(x)}.
     * If the given digit index is less than 0, the number will be rounded at the according
     * past-comma digit.
     * 
     * @param x The value to round
     * @param floorDigits The digit index of the digit to start rounding at
     * @return The rounded value
     */
    public static final double floor(double x, int floorDigits) {
        double factor = Math.pow(10, floorDigits);
        return Math.floor(x / factor) * factor;
    }

    /**
     * Ceils the given {@code double} to the next closest one from the specified digit
     * on. This means that {@code ceil(x, 0)} returns exactly the same as {@code Math.ceil(x)}.
     * If the given digit index is less than 0, the number will be rounded at the according
     * past-comma digit.
     * 
     * @param x The value to round
     * @param ceilDigits The digit index of the digit to start rounding at
     * @return The rounded value
     */
    public static final double ceil(double x, int ceilDigits) {
        double factor = Math.pow(10, ceilDigits);
        return Math.ceil(x / factor) * factor;
    }

    /**
     * Rounds the given {@code double} to the next closest one from the specified digit
     * on. This means that {@code round(x, 0)} returns exactly the same as {@code Math.round(x)}.
     * If the given digit index is less than 0, the number will be rounded at the according
     * past-comma digit.
     * 
     * @param x The value to round
     * @param roundDigits The digit index of the digit to start rounding at
     * @return The rounded value
     */
    public static final double round(double x, int roundDigits) {
        double factor = Math.pow(10, roundDigits);
        return Math.round(x / factor) * factor;
    }


    public static final long min(long... numbers) {
        Objects.requireNonNull(numbers);
        if(numbers.length == 0) return 0;
        long min = numbers[0];
        for(int i=1; i<numbers.length; i++)
            if(numbers[i] < min) min = numbers[i];
        return min;
    }

    public static final long max(long... numbers) {
        Objects.requireNonNull(numbers);
        if(numbers.length == 0) return 0;
        long max = numbers[0];
        for(int i=1; i<numbers.length; i++)
            if(numbers[i] > max) max = numbers[i];
        return max;
    }





    /**
     * Returns the highest exponent of the given {@code double} which's digit is greater
     * than 0 (the absolute value). For example, {@code exponentOf(12)} would be {@code 2},
     * {@code exponentOf(0.45)} would be {@code -1} and {@code exponentOf(0)} is {@code 0}.
     * <p>Special cases:
     * <ul><li>If the argument is NaN, the result will be {@code 0}.
     * <li>If the argument is positive or negative infinity, the result will be
     * {@code Integer.MAX_VALUE} and {@code Integer.MIN_VALUE}.
     * </ul>
     * 
     * @param x The double to find the exponent of
     * @return The highest exponent of the number
     */
    public static int exponentOf(double x) {
        if(x == 0 || Double.isNaN(x)) return 0;
        if(x == Double.POSITIVE_INFINITY) return Integer.MAX_VALUE;
        if(x == Double.NEGATIVE_INFINITY) return Integer.MIN_VALUE;
        x = Math.abs(x);
        if(Math.floor(x) > 0) {
            if(floor(x, 1) > 0) return exponentOf(x / 10) + 1;
            else return 1;
        }
        else {
            if(floor(x, -1) == 0) return exponentOf(x * 10) - 1;
            return -1;
        }
    }



    /**
     * Floors the number so that is has at most the specified number of digits
     * greater than {@code 0}. In case of a {@code double} that would be printed in an
     * exponential from the factor will be shortened to only have so many digits
     * which can improve the readability.
     * 
     * @param x The number to floor
     * @param length The number of digits to be kept
     * @return The floored number
     */
    public static double floorToLength(double x, int length) {
        return floor(x, exponentOf(x) - length);
    }

    /**
     * Ceils the number so that is has at most the specified number of digits
     * greater than {@code 0}. In case of a {@code double} that would be printed in an
     * exponential from the factor will be shortened to only have so many digits
     * which can improve the readability.
     * 
     * @param x The number to ceil
     * @param length The number of digits to be kept
     * @return The ceiled number
     */
    public static double ceilToLength(double x, int length) {
        return ceil(x, exponentOf(x) - length);
    }

    
    /**
     * Rounds the number so that is has at most the specified number of digits
     * greater than {@code 0}. In case of a {@code double} that would be printed in an
     * exponential from the factor will be shortened to only have so many digits
     * which can improve the readability.
     * 
     * @param x The number to round
     * @param length The number of digits to be kept
     * @return The rounded number
     */
    public static double roundToLength(double x, int length) {
        return round(x, exponentOf(x) - length);
    }
}
