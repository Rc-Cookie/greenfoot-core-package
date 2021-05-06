package com.github.rccookie.greenfoot.core;

/**
 * An abstract representation of a colour, consisting of RED, GREEN, BLUE
 * and ALPHA (transparency).
 * 
 * @author RcCookie
 * @version 1.0
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
public class Color implements Cloneable {

    static {
        Core.initialize();
    }

    /**
     * The effect of {@link #brighter()} and {@link #darker()}.
     */
    private static final double BRIGHTNESS_FACTOR = 0.7;

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
    public static final Color DARK_GRAY = new NamedColor(64, 64, 64, "dark gray");

    /**
     * The color black.
     */
    public static final Color BLACK = new NamedColor(0, 0, 0, "black");

    /**
     * The color transparent. In rgb space the color black.
     */
    public static final Color TRANSPARENT = new NamedColor(0, 0, 0, 0, "transparent");



    /**
     * The {@link greenfoot.Color} that backs this color.
     */
    private final greenfoot.Color gColor;



    /**
     * Creates a copy of the given color.
     */
    @SuppressWarnings("CopyConstructorMissesField")
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
        this(new greenfoot.Color(red, green, blue, alpha));
    }

    /**
     * Creates a new color based on the given greenfoot color.
     * 
     * @param gColor The {@link greenfoot.Color} to base this color on
     */
    private Color(greenfoot.Color gColor) {
        this.gColor = gColor;
    }



    /**
     * Creates a copy of this color.
     */
    @Override
    public Color clone() {
        return new Color(this);
    }



    /**
     * Returns new color that is brighter than this one.
     * <p>This is <i>not</i> an exact reverse operation to {@link #darker()}.
     * 
     * @return A new color that is brighter than this color (unless it already
     *         is white)
     */
    public Color brighter() {
        // Based on java.awt.Color.brighter() implementation
        int r = getRed();
        int g = getGreen();
        int b = getBlue();
        int alpha = getAlpha();

        int i = (int)(1 / (1 - BRIGHTNESS_FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(
            Math.min((int)(r / BRIGHTNESS_FACTOR), 255),
            Math.min((int)(g / BRIGHTNESS_FACTOR), 255),
            Math.min((int)(b / BRIGHTNESS_FACTOR), 255),
            alpha
        );
    }

    /**
     * Returns new color that is darker than this one.
     * <p>This is <i>not</i> an exact reverse operation to {@link #brighter()}.
     * 
     * @return A new color that is darker than this color (unless it already
     *         is black)
     */
    public Color darker() {
        // Based on java.awt.Color.darker() implementation
        return new Color(
            Math.max((int)(getRed() * BRIGHTNESS_FACTOR), 0),
            Math.max((int)(getGreen() * BRIGHTNESS_FACTOR), 0),
            Math.max((int)(getBlue() * BRIGHTNESS_FACTOR), 0),
            getAlpha()
        );
    }



    /**
     * Returns the alpha value (the transparency) of this color. {@code 0} means
     * fully transparent, {@code 255} means solid.
     * 
     * @return The alpha value of this color
     */
    public int getAlpha() {
        return gColor.getAlpha();
    }

    /**
     * Returns the blue value of this color.
     * 
     * @return The blue value of this color
     */
    public int getBlue() {
        return gColor.getBlue();
    }

    /**
     * Returns the green value of this color.
     * 
     * @return The green value of this color
     */
    public int getGreen() {
        return gColor.getGreen();
    }

    /**
     * Returns the red value of this color.
     * 
     * @return The red value of this color
     */
    public int getRed() {
        return gColor.getRed();
    }

    /**
     * Returns the hash code of this color. Only equal colors have the same color.
     * 
     * @return The hash code of this color
     */
    @Override
    public int hashCode() {
        return gColor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj instanceof Color || obj instanceof greenfoot.Color)
            return obj.hashCode() == hashCode();
        return false;
    }



    /**
     * Returns a representative string for this color. This will be it's color code
     * in hexadecimal, and, if not 255, its transparency added.
     * <p>For example, {@code new Color(255, 255, 255).toString()} will result in
     * {@code #FFFFFF} and {@code new Color(0,0,0,0).toString()} will return
     * {@code #000000 (0)}.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(7);
        s.append('#');
        s.append(String.format("%02X", getRed())).append(String.format("%02X", getGreen())).append(String.format("%02X", getBlue()));
        int a = getAlpha();
        if(a != 255) s.append(String.format("%02X", a));
        return s.toString();
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
    public static Color relative(double red, double green, double blue) {
        return relative(red, green, blue, 1);
    }

    /**
     * Creates a new color with the given red, green, blue and alpha (transparency) value,
     * given as percentage between {@code 0} and {@code 1}.
     * 
     * @param red The color's red value
     * @param green The color's green value
     * @param blue The color's blue value
     * @param alpha The color's alpha value, where {@code 0} means transparent
     * @return A color with the specified values
     */
    public static Color relative(double red, double green, double blue, double alpha) {
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
         * where {@code name} stands for the in the constructor specified name.
         */
        @Override
        public String toString() {
            return super.toString() + " \"" + name + '"';
        }

        @Override
        public NamedColor clone() {
            return new NamedColor(getRed(), getGreen(), getBlue(), getAlpha(), name);
        }
    }



    /**
     * Converts the given {@link greenfoot.Color} to a color.
     * 
     * @param gColor The {@link greenfoot.Color} to convert
     * @return The converted color
     */
    public static Color of(greenfoot.Color gColor) {
        return new Color(gColor);
    }

    /**
     * Converts the given color into a {@link greenfoot.Color}.
     * 
     * @param color The color to convert
     * @return The converted {@link greenfoot.Color}
     */
    public static greenfoot.Color asGColor(Color color) {
        return color.gColor;
    }
}
