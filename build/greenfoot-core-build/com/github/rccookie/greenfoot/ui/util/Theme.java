package com.github.rccookie.greenfoot.ui.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import greenfoot.Color;

/**
 * Stores a color theme with differently prioritized colors. If a color lower
 * than the lowermost specified color of the theme is requested, the lowest
 * color will be returned.
 */
public class Theme implements Cloneable, Serializable {

    private static final long serialVersionUID = 4691314200865049554L;

    public static final Theme DARK_MODE = new Theme(new Color(30, 30, 30), new Color(60, 60, 60), new Color(0, 154, 124), Color.RED);
    public static final Theme LIGHT_MODE = new Theme(new Color(204, 204, 204), Color.WHITE, new Color(204, 93, 41), Color.RED);
    public static final Theme DEBUG = new Theme(Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN);
    public static final Theme ERROR = new Theme(Color.RED, Color.BLACK, Color.RED, Color.BLACK);

    public static final Theme DARK_MODE_TEXT = new Theme(new Color(204, 204, 172), Color.WHITE);
    public static final Theme LIGHT_MODE_TEXT = new Theme(new Color(50, 50, 50), new Color(123, 123, 123));
    public static final Theme DEBUG_TEXT = new Theme(Color.RED, Color.YELLOW);
    public static final Theme ERROR_TEXT = new Theme(Color.BLACK, Color.RED);

    /**
     * A fully transparent color. When not transparent this has the color black.
     */
    public static final Color C_TRANSPARENT = new Color(0, 0, 0, 0);




    /**
     * The different colors of this theme.
     */
    private final Color[] colors;

    /**
     * Creates a new theme. The main color must not be null, more colors may be
     * {@code null} or an empty array.
     * 
     * @param mainColor The main color of this theme. Must not be null
     * @param moreColors Less often used colors, sorted in priority
     */
    public Theme(Color mainColor, Color... moreColors) {
        this(generateArray(mainColor, moreColors));
    }

    /**
     * Creates a new theme from the given theme.
     * 
     * @param copy The theme to copy
     */
    public Theme(Theme copy) {
        this(copy.colors);
    }

    private Theme(Color[] colors) {
        this.colors = colors;
    }

    private static final Color[] generateArray(Color mainColor, Color[] moreColors) {
        Objects.requireNonNull(mainColor);
        if(moreColors == null) return new Color[] {mainColor};
        Color[] colors = new Color[moreColors.length + 1];
        colors[0] = mainColor;
        System.arraycopy(moreColors, 0, colors, 1, moreColors.length);
        return colors;
    }

    /**
     * Creates a clone of this theme.
     * 
     * @return a clone of this theme
     */
    @Override
    protected Theme clone() {
        return new Theme(this);
    }

    /**
     * Returns the main color of this theme.
     * 
     * @return The main color
     */
    public Color main() {
        return colors[0];
    }

    /**
     * Returns the second color of this theme. If a second color was not
     * specified the main color will be returned.
     * 
     * @return The second color
     */
    public Color second() {
        return get(1);
    }

    /**
     * Returns the third color of this theme. If a third color was not
     * specified the second color will be returned. If that was not
     * specified either the main color will be returned.
     * 
     * @return The third color
     */
    public Color third() {
        return get(2);
    }

    /**
     * Returns the color at the specified index. If that color was not specified
     * the lowest specified color will be returned.
     * <p>The color at index {@code 0} will be the main color, the color at
     * index  {@code 1} will be the second color and so on.
     * 
     * @param colorIndex The index of the color
     * @return The color at that index
     */
    public Color get(int colorIndex) {
        if(colorIndex < colors.length) return colors[colorIndex];
        return colors[colors.length - 1];
    }

    /**
     * Returns the color at the specified location, or the given default color
     * if that color was not specificly specified.
     * 
     * @param colorIndex the index of the color
     * @param defaultC The default color
     * @return The color at that index, or the given default color
     */
    public Color getOrDefault(int colorIndex, Color defaultC) {
        return specified(colorIndex) ? get(colorIndex) : defaultC;
    }

    /**
     * Returns weather the color at the specified index is specificly specified.
     * {@code 0} should always return {@code true}, negative numbers will return
     * {@code false} in any way.
     * 
     * @param index The color index
     * @return {@code true} if the color was specified
     */
    public boolean specified(int index) {
        if(index < 0) return false;
        return index < size();
    }

    /**
     * Returns the number of colors specificly specified in this theme.
     * 
     * @return The number of colors in this theme
     */
    public int size() {
        return colors.length;
    }

    /**
     * Returns an array containing all specificly specified colors of this theme.
     * 
     * @return An array with this theme's colors
     */
    public Color[] toArray() {
        Color[] array = new Color[colors.length];
        System.arraycopy(colors, 0, array, 0, colors.length);
        return array;
    }

    /**
     * Creates and returns a new theme containing only the colors beginning at
     * the specified index. If the color at the index was not specificly specified,
     * the last specified color will be chosen as the main and only color.
     * 
     * @param fromColor The index of the color to start from
     * @return A new theme containing only the colors beginning at that index
     */
    public Theme subTheme(int fromColor) {
        if(!specified(fromColor)) return new Theme(get(fromColor));
        Color[] colors = new Color[size() - fromColor];
        System.arraycopy(this.colors, fromColor, colors, 0, colors.length);
        return new Theme(colors);
    }

    /**
     * Returns a new Theme where the color at the specified position is replaced by
     * the given color. If that color was not specificly specified before, all colors
     * until there will be specified in the new theme as the last color and the final
     * color will be the new one.
     * 
     * @param modifiedColorIndex The index of the color to be modified
     * @param newColor The new color at the specified index
     * @return A new, modified theme
     */
    public Theme modified(int modifiedColorIndex, Color newColor) {
        Color[] newColors = new Color[Math.max(colors.length, modifiedColorIndex + 1)];
        for(int i=0; i<newColors.length; i++) {
            if(i == modifiedColorIndex) newColors[i] = newColor;
            else newColors[i] = get(i);
        }
        return new Theme(newColors);
    }



    public static final Theme getDefaultTheme() {
        return LIGHT_MODE;
    }

    public static final Theme getDefaultTextTheme() {
        return LIGHT_MODE_TEXT;
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Theme)) return false;
        return Arrays.equals(colors, ((Theme) obj).colors);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(colors);
    }
}
