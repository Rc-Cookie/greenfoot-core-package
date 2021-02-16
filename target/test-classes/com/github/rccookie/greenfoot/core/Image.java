package com.github.rccookie.greenfoot.core;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import com.github.rccookie.common.geometry.Vector;

import greenfoot.Font;
import greenfoot.GreenfootImage;

public class Image extends GreenfootImage implements Cloneable {

    public Image(int width, int height) {
        super(width, height);
    }

    public Image(Image copy) {
        super(copy);
    }

    public Image(String filename) {
        super(filename);
    }

    private Image(String string, int fontsize, Color color) {
        super(string, fontsize, color, new Color(0, 0, 0, 0));
    }



    @Override
    protected Image clone() {
        return new Image(this);
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

    @Override
    public Color getColor() {
        return Color.of(super.getColor());
    }

    @Override
    public greenfoot.Color getColorAt(int x, int y) {
        return Color.of(super.getColorAt(x, y));
    }

    @Override
    public FontStyle getFont() {
        return FontStyle.of(super.getFont());
    }

    @Override
    public void setColor(greenfoot.Color color) {
        super.setColor(Color.of(color));
    }

    @Override
    public void setColorAt(int x, int y, greenfoot.Color color) {
        super.setColorAt(x, y, Color.of(color));
    }

    @Override
    public void setFont(Font f) {
        super.setFont(FontStyle.of(f));
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

    public static Image of(GreenfootImage gImage) {
        return new GreenfootImageImage(gImage);
    }

    private static final class GreenfootImageImage extends Image {

        private final GreenfootImage gImage;

        private GreenfootImageImage(GreenfootImage gImage) {
            super(1, 1);
            this.gImage = gImage;
        }

        @Override
        public void clear() {
            gImage.clear();
        }

        @Override
        public void drawImage(GreenfootImage image, int x, int y) {
            gImage.drawImage(image, x, y);
        }

        @Override
        public void drawLine(int x1, int y1, int x2, int y2) {
            gImage.drawLine(x1, y1, x2, y2);
        }

        @Override
        public void drawOval(int x, int y, int width, int height) {
            gImage.drawOval(x, y, width, height);
        }

        @Override
        public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            gImage.drawPolygon(xPoints, yPoints, nPoints);
        }

        @Override
        public void drawRect(int x, int y, int width, int height) {
            gImage.drawRect(x, y, width, height);
        }

        @Override
        public void drawShape(Shape shape) {
            gImage.drawShape(shape);
        }

        @Override
        public void drawString(String string, int x, int y) {
            gImage.drawString(string, x, y);
        }

        @Override
        public boolean equals(Object obj) {
            return gImage.equals(obj);
        }

        @Override
        public void fill() {
            gImage.fill();
        }

        @Override
        public void fillOval(int x, int y, int width, int height) {
            gImage.fillOval(x, y, width, height);
        }

        @Override
        public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            gImage.fillPolygon(xPoints, yPoints, nPoints);
        }

        @Override
        public void fillRect(int x, int y, int width, int height) {
            gImage.fillRect(x, y, width, height);
        }

        @Override
        public BufferedImage getAwtImage() {
            return gImage.getAwtImage();
        }

        @Override
        public Color getColor() {
            return Color.of(gImage.getColor());
        }

        @Override
        public Color getColorAt(int x, int y) {
            return Color.of(gImage.getColorAt(x, y));
        }

        @Override
        public FontStyle getFont() {
            return FontStyle.of(gImage.getFont());
        }

        @Override
        public int getHeight() {
            return gImage.getHeight();
        }

        @Override
        public int getTransparency() {
            return gImage.getTransparency();
        }

        @Override
        public int getWidth() {
            return gImage.getTransparency();
        }

        @Override
        public int hashCode() {
            return gImage.hashCode();
        }

        @Override
        public void mirrorHorizontally() {
            gImage.mirrorHorizontally();
        }

        @Override
        public void mirrorVertically() {
            gImage.mirrorVertically();
        }

        @Override
        public void rotate(int degrees) {
            gImage.rotate(degrees);
        }

        @Override
        public void scale(int width, int height) {
            gImage.scale(width, height);
        }

        @Override
        public void setColor(greenfoot.Color color) {
            gImage.setColor(color);
        }

        @Override
        public void setColorAt(int x, int y, greenfoot.Color color) {
            gImage.setColorAt(x, y, color);
        }

        @Override
        public void setFont(Font f) {
            gImage.setFont(f);
        }

        @Override
        public void setTransparency(int t) {
            gImage.setTransparency(t);
        }
    }
}
