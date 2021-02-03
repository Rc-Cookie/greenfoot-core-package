package com.github.rccookie.greenfoot.widgets;

import greenfoot.GreenfootImage;

public class Color extends Visual {

    public static final greenfoot.Color BLACK = greenfoot.Color.BLACK;
    public static final greenfoot.Color BLUE = greenfoot.Color.BLUE;
    public static final greenfoot.Color CYAN = greenfoot.Color.CYAN;
    public static final greenfoot.Color DARK_GRAY = greenfoot.Color.DARK_GRAY;
    public static final greenfoot.Color GRAY = greenfoot.Color.GRAY;
    public static final greenfoot.Color GREEN = greenfoot.Color.GREEN;
    public static final greenfoot.Color LIGHT_GRAY = greenfoot.Color.LIGHT_GRAY;
    public static final greenfoot.Color MAGENTA = greenfoot.Color.MAGENTA;
    public static final greenfoot.Color ORANGE = greenfoot.Color.ORANGE;
    public static final greenfoot.Color PINK = greenfoot.Color.PINK;
    public static final greenfoot.Color RED = greenfoot.Color.RED;
    public static final greenfoot.Color WHITE = greenfoot.Color.WHITE;
    public static final greenfoot.Color YELLOW = greenfoot.Color.YELLOW;
    public static final greenfoot.Color TRANSPARENT = new greenfoot.Color(0, 0, 0, 0);

    private greenfoot.Color color;

    public Color(greenfoot.Color color) {
        this.color = color;
    }

    public greenfoot.Color getColor() {
        return color;
    }

    public void setColor(greenfoot.Color color) {
        modify(() -> this.color = color);
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        GreenfootImage image = new GreenfootImage(maxSize.width, maxSize.height);
        image.setColor(color);
        image.fill();
        return image;
    }

    @Override
    public String toString() {
        greenfoot.Color color = getColor();
        return super.toString() + " (r: " + color.getRed() + ", g: " + color.getGreen() + ", b:" + color.getBlue() + ", \u03B1: " + color.getAlpha() + ")";
    }
}
