package com.github.rccookie.greenfoot.core;

/**
 * An abstract representation of a color, consisting of RED, GREEN, BLUE
 * and ALPHA (transparency).
 * <p>This is a subclass of {@link greenfoot.Color} that adds some conveniance
 * methods.
 */
public class Color extends greenfoot.Color implements Cloneable {



    /**
     * The color red.
     */
    public static final Color RED = new NamedColor(255, 0, 0, "red");

    /**
     * The color green.
     */
    public static final Color GREEN = new NamedColor(0, 255, 0, "green");

    /**
     * The color blue.
     */
    public static final Color BLUE = new NamedColor(0, 0, 255, "blue");

    /**
     * The color yellow.
     */
    public static final Color YELLOW = new NamedColor(255, 255, 0, "yellow");

    /**
     * The color magenta.
     */
    public static final Color MAGENTA = new NamedColor(255, 0, 255, "magenta");

    /**
     * The color cyan.
     */
    public static final Color CYAN = new NamedColor(0, 255, 255, "cyan");

    /**
     * The color orange.
     */
    public static final Color ORANGE = new NamedColor(255, 200, 0, "orange");

    /**
     * The color pink.
     */
    public static final Color PINK = new NamedColor(255, 175, 175, "pink");

    /**
     * The color white.
     */
    public static final Color WHITE = new NamedColor(255, 255, 255, "white");

    /**
     * The color light gray.
     */
    public static final Color LIGHT_GRAY = new NamedColor(192, 192, 192, "light gray");

    /**
     * The color gray.
     */
    public static final Color GRAY = new NamedColor(128, 128, 128, "gray");

    /**
     * The color dark gray.
     */
    public static final Color DARK_GRAY = new NamedColor(64, 64, 64, "drak gray");

    /**
     * The color black.
     */
    public static final Color BLACK = new NamedColor(0, 0, 0, "black");

    /**
     * The color transparent. In rgb space the color black.
     */
    public static final Color TRANSPARENT = new NamedColor(0, 0, 0, 0, "transparent");



    /**
     * Creates a copy of the given color.
     */
    public Color(Color copy) {
        this(copy.getRed(), copy.getGreen(), copy.getBlue(), copy.getAlpha());
    }

    /**
     * Creates a new color.
     * 
     * @param red The red value for this color, from {@code 0} to {@code 255}
     * @param green The green value for this color, from {@code 0} to {@code 255}
     * @param blue The blue value for this color, from {@code 0} to {@code 255}
     */
    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    /**
     * Creates a new color.
     * 
     * @param red The red value for this color, from {@code 0} to {@code 255}
     * @param green The green value for this color, from {@code 0} to {@code 255}
     * @param blue The blue value for this color, from {@code 0} to {@code 255}
     * @param alpha The transparency value for this color, from {@code 0} to {@code 255},
     *              where {@code 0} is transparent
     */
    public Color(int red, int green, int blue, int alpha) {
        super(red, green, blue, alpha);
    }



    /**
     * Creates a copy of this color.
     */
    @Override
    protected Color clone() {
        return new Color(this);
    }



    @Override
    public greenfoot.Color brighter() {
        return of(super.brighter());
    }

    @Override
    public greenfoot.Color darker() {
        return of(super.darker());
    }



    /**
     * Returns a representive string for this color. This will be it's color code
     * in hexadecimal, and, if not 255, its transparency added.
     * <p>For example, {@code new Color(255, 255, 255).toString()} will result in
     * {@code #FFFFFF} and {@code new Color(0,0,0,0).toString()} will return
     * {@code #000000 (0)}.
     */
    @Override
    public String toString() {
        String s = '#' + Integer.toHexString(getRed()).toUpperCase() + Integer.toHexString(getGreen()).toUpperCase() + Integer.toHexString(getBlue()).toUpperCase();
        int a = getAlpha();
        if(a != 255) s += " (" + a + ")";
        return s;
    }



    /**
     * Returns a new color based on this color with the specified red value.
     * 
     * @param red The red value of the new color
     * @return A new color with the given red value and all the other values like
     *         this color.
     */
    public Color setRed(int red) {
        return new Color(red, getGreen(), getBlue(), getAlpha());
    }

    /**
     * Returns a new color based on this color with the specified green value.
     * 
     * @param green The green value of the new color
     * @return A new color with the given green value and all the other values like
     *         this color.
     */
    public Color setGreen(int green) {
        return new Color(getRed(), green, getBlue(), getAlpha());
    }

    /**
     * Returns a new color based on this color with the specified blue value.
     * 
     * @param blue The blue value of the new color
     * @return A new color with the given blue value and all the other values like
     *         this color.
     */
    public Color setBlue(int blue) {
        return new Color(getRed(), getGreen(), blue, getAlpha());
    }

    /**
     * Returns a new color based on this color with the specified alpha value.
     * 
     * @param alpha The alpha value of the new color
     * @return A new color with the given alpha value and all the other values like
     *         this color.
     */
    public Color setAlpha(int alpha) {
        return new Color(getRed(), getGreen(), getBlue(), alpha);
    }



    /**
     * Creates a new color with the given red, green and blue value, given as percentage
     * between {@code 0} and {@code 1}.
     * 
     * @param red The color's red value
     * @param green The color's green value
     * @param blue The color's blue value
     * @return A color with the specified values
     */
    public static final Color relative(double red, double green, double blue) {
        return relative(red, green, blue, 1);
    }

    /**
     * Creates a new color with the given red, green, blue and alpha (transparency) value,
     * given as percentage between {@code 0} and {@code 1}.
     * 
     * @param red The color's red value
     * @param green The color's green value
     * @param blue The color's blue value
     * @param alpha The color's aphy value, where {@code 0} means transparent
     * @return A color with the specified values
     */
    public static final Color relative(double red, double green, double blue, double alpha) {
        return new Color((int)(red * 255), (int)(green * 255), (int)(blue * 255), (int)(alpha * 255));
    }



    /**
     * A special color that adds a predefined name to the return of {@link #toString()}.
     */
    private static final class NamedColor extends Color {

        /**
         * The name of this color.
         */
        private final String name;

        /**
         * Creates a new named color.
         * 
         * @param red The color's red value
         * @param green The color's green value
         * @param blue The color's blue value
         * @param name The color's name
         */
        private NamedColor(int red, int green, int blue, String name) {
            this(red, green, blue, 255, name);
        }

        /**
         * Creates a new named color.
         * 
         * @param red The color's red value
         * @param green The color's green value
         * @param blue The color's blue value
         * @param alpha The color's blue value
         * @param name The color's name
         */
        private NamedColor(int red, int green, int blue, int alpha, String name) {
            super(red, green, blue, alpha);
            this.name = name;
        }

        /**
         * Returns the super definition of {@link #toString()} and appends {@code "name"}
         * where {@code name} stands for the in the constructur specified name.
         */
        @Override
        public String toString() {
            return super.toString() + " \"" + name + '"';
        }
    }



    public static final Color of(greenfoot.Color gColor) {
        if(Color.class.isInstance(gColor)) return (Color)gColor;
        return new GreenfootColorColor(gColor);
    }

    private static final class GreenfootColorColor extends Color {

        private final greenfoot.Color gColor;

        private GreenfootColorColor(greenfoot.Color gColor) {
            super(0, 0, 0);
            this.gColor = gColor;
        }

        @Override
        public Color brighter() {
            return of(gColor.brighter());
        }

        @Override
        public Color darker() {
            return of(gColor.brighter());
        }

        @Override
        public int getAlpha() {
            return gColor.getAlpha();
        }

        @Override
        public int getBlue() {
            return gColor.getBlue();
        }

        @Override
        public int getGreen() {
            return gColor.getGreen();
        }

        @Override
        public int getRed() {
            return gColor.getRed();
        }

        @Override
        public int hashCode() {
            return gColor.hashCode();
        }
    }
}
