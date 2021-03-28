package com.github.rccookie.greenfoot.game.physics2d;

import java.util.Objects;

public enum CollisionDetection {

    /**
     * Calculates collisions from movement by checking collision of a ray
     * starting at the initial position until the target position. This
     * system will never 'glitch through' other objects, but may be
     * computationally more intense.
     * <p><b>Examples:</b>
     * <p>{@code s} means start location, {@code x} means target location,
     * {@code e} means actual ending position.
     * <pre>
     * OK:
     *    +---+      +---+
     *    |   |      |   |
     * s  | x |  ->  e   |
     *    |   |      |   |
     *    +---+      +---+
     * Also fine:
     *    +-+        +-+
     *    | |        | |
     * s  | | x  ->  e |
     *    | |        | |
     *    +-+        +-+
     * </pre>
     */
    CONTINUOUS,

    /**
     * Calculates collisions from movement by only checking the ray's
     * collision if the object intersects something at the target position.
     * This does (propably) reduce cpu load but objects may 'glitch through'
     * each other when moving fast / if they are thin.
     * <p><b>Examples:</b>
     * <p>{@code s} means start location, {@code x} means target location,
     * {@code e} means actual ending position.
     * <pre>
     * OK:
     *    +---+      +---+
     *    |   |      |   |
     * s  | x |  ->  e   |
     *    |   |      |   |
     *    +---+      +---+
     * Incorrect:
     *    +-+        +-+
     *    | |        | |
     * s  | | x  ->  | | e
     *    | |        | |
     *    +-+        +-+
     * </pre>
     */
    DISTINCT;



    private static CollisionDetection current = CONTINUOUS;

    public static CollisionDetection current() {
        return current;
    }

    public static void setCurrent(CollisionDetection newCurrent) {
        current = Objects.requireNonNull(newCurrent);
    }
}
