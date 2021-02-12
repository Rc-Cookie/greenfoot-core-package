package com.github.rccookie.greenfoot.core;

import com.github.rccookie.common.geometry.Vector;

import greenfoot.Color;
import greenfoot.Font;
import greenfoot.GreenfootImage;

public class Image extends GreenfootImage {

    public Image(int width, int height) {
        super(width, height);
    }

    public Image(GreenfootImage copy) {
        super(copy);
    }

    public Image(String filename) {
        super(filename);
    }

    private Image(String string, int fontsize, Color color) {
        super(string, fontsize, color, new Color(0, 0, 0, 0));
    }



    public void fill(Color color) {
        drawWithColor(() -> fill(), color);
    }

    public void fillOval(int x, int y, int width, int height, Color color) {
        drawWithColor(() -> fillOval(x, y, width, height), color);
    }

    public void fillRect(int x, int y, int width, int height, Color color) {
        drawWithColor(() -> fillRect(x, y, width, height), color);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, Color color) {
        drawWithColor(() -> fillPolygon(xPoints, yPoints), color);
    }

    public void fillPolygon(Color color, Vector... points) {
        drawWithColor(() -> fillPolygon(points), color);
    }

    public void fillPolygon(Vector... points) {
        int[] xPoints = new int[points.length], yPoints = new int[points.length];
        for(int i=0; i<points.length; i++) {
            xPoints[i] = (int)points[i].x();
            yPoints[i] = (int)points[i].y();
        }
        fillPolygon(xPoints, yPoints);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints) {
        fillPolygon(xPoints, yPoints, Math.min(xPoints.length, yPoints.length));
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        drawWithColor(() -> drawLine(x1, y1, x2, y2), color);
    }

    public void drawLine(Vector from, Vector to, Color color) {
        drawWithColor(() -> drawLine(from, to), color);
    }

    public void drawLine(Vector from, Vector to) {
        drawLine((int)from.x(), (int)from.y(), (int)to.x(), (int)to.y());
    }

    public void drawOval(int x, int y, int width, int height, Color color) {
        drawWithColor(() -> drawOval(x, y, width, height), color);
    }

    public void drawRect(int x, int y, int width, int height, Color color) {
        drawWithColor(() -> drawRect(x, y, width, height), color);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, Color color) {
        drawWithColor(() -> drawPolygon(xPoints, yPoints), color);
    }

    public void drawPolygon(Color color, Vector... points) {
        drawWithColor(() -> drawPolygon(points), color);
    }

    public void drawPolygon(Vector... points) {
        int[] xPoints = new int[points.length], yPoints = new int[points.length];
        for(int i=0; i<points.length; i++) {
            xPoints[i] = (int)points[i].x();
            yPoints[i] = (int)points[i].y();
        }
        drawPolygon(xPoints, yPoints);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints) {
        drawPolygon(xPoints, yPoints, Math.min(xPoints.length, yPoints.length));
    }

    public void drawString(String string, int x, int y, Color color, Font font) {
        setFont(font);
        drawWithColor(() -> drawString(string, x, y), color);
    }

    private void drawWithColor(Runnable operation, Color color) {
        Color current = getColor();
        setColor(color);
        operation.run();
        setColor(current);
    }

    public void scale(double factor) {
        scale((int)(factor * getWidth()), (int)(factor * getHeight()));
    }

    public CoreActor asActor() {
        return new CoreActor() { { setImage(Image.this); } };
    }



    public static Image block(int width, int height, Color color) {
        Image image = new Image(width, height);
        image.fill(color);
        return image;
    }

    public static Image oval(int width, int height, Color color) {
        Image image = new Image(width, height);
        image.fillOval(0, 0, width, height, color);
        return image;
    }

    public static Image text(String string, int fontsize, Color color) {
        return new Image(string, fontsize, color);
    }

    public static Image text(String string, Color color, FontStyle font) {
        Image image = new Image(Math.max(1, font.getWidth(string)), Math.max(1, font.getHeight(string)));
        //image.fill(Color.LIGHT_GRAY); // For debugging purposes
        image.drawString(string, 0, (int)(font.getSize() * 0.75), color, font);
        return image;
    }
}
