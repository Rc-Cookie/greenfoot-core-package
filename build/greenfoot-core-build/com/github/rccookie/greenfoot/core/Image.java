package com.github.rccookie.greenfoot.core;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import com.github.rccookie.common.geometry.Vector;

import greenfoot.Font;
import greenfoot.GreenfootImage;

/**
 * An image that is offline represented by a {@link BufferedImage}. It is
 * wrapping {@link GreenfootImage} but adds more conveniance methods and
 * support for {@link Color} and {@link FontStyle}.
 * 
 * @author RcCookie
 * @version 2.1
 */
public class Image implements Cloneable {

    static {
        Core.initialize();
    }

    /**
     * The underlying GreenfootImage. Cannot use BufferedImage directly
     * because that is not available online.
     */
    private final SupportGreenfootImage gImage;

    /**
     * Creates a new transparent image of the given size.
     * 
     * @param width The width of the image
     * @param height The height of the image
     */
    public Image(int width, int height) {
        gImage = new SupportGreenfootImage(width, height);
    }

    /**
     * Creates a new image copying the given one.
     * 
     * @param copy The image to copy
     */
    public Image(Image copy) {
        gImage = new SupportGreenfootImage(copy.gImage);
    }

    /**
     * Creates a new image by trying to load from an image file with the
     * specified file name and path. Supported file types are jpg, png and
     * gif.
     * 
     * @param filename The path and name of the image to load, for example
     *                 {@code "images/Car.png"}
     */
    public Image(String filename) {
        gImage = new SupportGreenfootImage(filename);
    }

    /**
     * Creates a new image with the given string printed onto it. The image
     * will automatically be sized to exactly fot the string onto it. Offline
     * this will be a Calibri styled bond font, online a not-bold times roman.
     * The background of the image will be transparent.
     * 
     * @param string The string to print
     * @param fontsize The font size of string
     * @param color The text color
     */
    private Image(String string, int fontsize, Color color) {
        gImage = new SupportGreenfootImage(string, fontsize, Color.asGColor(color));
    }

    /**
     * Creates a new image from the given {@link GreenfootImage}. It will be
     * wrapped rether than copied.
     * 
     * @param gImage The greenfootImage to be wrapped.
     */
    private Image(GreenfootImage gImage) {
        this.gImage = new SupportGreenfootImageWrapper(gImage);
    }



    /**
     * Creates a copy of this image.
     */
    @Override
    protected Image clone() {
        return new Image(this);
    }



    /**
     * Fills the image with the given color.
     * 
     * @param color The color to fill with
     */
    public void fill(Color color) {
        drawWithColor(() -> fill(), color);
    }

    /**
     * Fills an oval with the top left corner of its bounding rectangle located
     * at the specified coordinates and with the given width and height, with
     * the given color.
     * 
     * @param x The x coordinate of the top loft corner of the ovals bounding
     *          rectangle
     * @param y The y coordinate of the top loft corner of the ovals bounding
     *          rectangle
     * @param width The width of the oval
     * @param height The height of the oval
     * @param color The color to fill with
     */
    public void fillOval(int x, int y, int width, int height, Color color) {
        drawWithColor(() -> fillOval(x, y, width, height), color);
    }

    /**
     * Fills a rectangle with the coordinates describing its top left corner
     * with the given color on this image.
     * 
     * @param x The top left corner's x coordinate
     * @param y The top left corner's y coordinate
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color to fill with
     */
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

    public void drawString(String string, int x, int y, Color color, FontStyle font) {
        setFont(font);
        drawWithColor(() -> drawString(string, x, y), color);
    }

    private void drawWithColor(Runnable operation, Color color) {
        setColor(color);
        operation.run();
    }

    public void scale(double factor) {
        scale((int)(factor * getWidth()), (int)(factor * getHeight()));
    }

    public Color getColor() {
        return Color.of(gImage.superGetColor());
    }

    public Color getColorAt(int x, int y) {
        return Color.of(gImage.superGetColorAt(x, y));
    }

    public FontStyle getFont() {
        return FontStyle.of(gImage.superGetFont());
    }

    public void setColor(Color color) {
        gImage.superSetColor(Color.asGColor(color));
    }

    public void setColorAt(int x, int y, Color color) {
        gImage.superSetColorAt(x, y, Color.asGColor(color));
    }

    public void setFont(FontStyle font) {
        gImage.superSetFont(font);
    }

    public void clear() {
        gImage.superClear();
    }

    public void drawImage(Image image, int x, int y) {
        gImage.superDrawImage(Image.asGImage(image), x, y);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        gImage.superDrawLine(x1, y1, x2, y2);
    }

    public void drawOval(int x, int y, int width, int height) {
        gImage.superDrawOval(x, y, width, height);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        gImage.superDrawPolygon(xPoints, yPoints, nPoints);
    }

    public void drawRect(int x, int y, int width, int height) {
        gImage.superDrawRect(x, y, width, height);
    }

    public void drawShape(Shape shape) {
        gImage.superDrawShape(shape);
    }

    public void drawString(String string, int x, int y) {
        gImage.superDrawString(string, x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof Image) return gImage.superEquals(((Image)obj).gImage);
        if(obj instanceof GreenfootImage) return gImage.superEquals((GreenfootImage)obj);
        return false;
    }

    public void fill() {
        gImage.superFill();
    }

    public void fillOval(int x, int y, int width, int height) {
        gImage.superFillOval(x, y, width, height);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        gImage.superFillPolygon(xPoints, yPoints, nPoints);
    }

    public void fillRect(int x, int y, int width, int height) {
        gImage.superFillRect(x, y, width, height);
    }

    public BufferedImage getAwtImage() {
        return gImage.superGetAwtImage();
    }

    public int getHeight() {
        return gImage.superGetHeight();
    }

    public int getTransparency() {
        return gImage.superGetTransparency();
    }

    public int getWidth() {
        return gImage.superGetWidth();
    }

    @Override
    public int hashCode() {
        return gImage.superHashCode();
    }

    public void mirrorHorizontally() {
        gImage.superMirrorHorizontally();
    }

    public void mirrorVertically() {
        gImage.superMirrorVertically();
    }

    public void rotate(int degrees) {
        gImage.superRotate(degrees);
    }

    public void scale(int width, int height) {
        gImage.superScale(width, height);
    }

    public void setTransparency(int t) {
        gImage.superSetTransparency(t);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getWidth() + "x" + getHeight() + ")";
    }



    public GameObject asActor() {
        return new GameObject() { { setImage(Image.this); } };
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
        return new Image(gImage);
    }

    public static GreenfootImage asGImage(Image image) {
        return image != null ? image.gImage : null;
    }



    private class SupportGreenfootImage extends GreenfootImage {



        private SupportGreenfootImage(GreenfootImage gImage) {
            super(gImage);
            
        }

        private SupportGreenfootImage(int width, int height) {
            super(width, height);
            
            //Console.printStackTrace();
        }

        private SupportGreenfootImage(String string, int fontsize, greenfoot.Color color) {
            super(string, fontsize, color, new greenfoot.Color(0,0,0,0));
            
        }

        private SupportGreenfootImage(String filename) {
            super(filename);
            
        }


        @Override
        public void clear() {
            Image.this.clear();
            
        }

        void superClear() {
            super.clear();
            
        }


        @Override
        public void drawImage(GreenfootImage image, int x, int y) {
            Image.this.drawImage(of(image), x, y);
            
        }

        void superDrawImage(GreenfootImage image, int x, int y) {
            super.drawImage(image, x, y);
            
        }


        @Override
        public void drawLine(int x1, int y1, int x2, int y2) {
            Image.this.drawLine(x1, y1, x2, y2);
            
        }

        void superDrawLine(int x1, int y1, int x2, int y2) {
            super.drawLine(x1, y1, x2, y2);
            
        }


        @Override
        public void drawOval(int x, int y, int width, int height) {
            Image.this.drawOval(x, y, width, height);
            
        }

        void superDrawOval(int x, int y, int width, int height) {
            super.drawOval(x, y, width, height);
            
        }


        @Override
        public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            Image.this.drawPolygon(xPoints, yPoints, nPoints);
            
        }

        void superDrawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            super.drawPolygon(xPoints, yPoints, nPoints);
            
        }


        @Override
        public void drawRect(int x, int y, int width, int height) {
            Image.this.drawRect(x, y, width, height);
            
        }

        void superDrawRect(int x, int y, int width, int height) {
            super.drawRect(x, y, width, height);
            
        }


        @Override
        public void drawShape(Shape shape) {
            Image.this.drawShape(shape);
            
        }

        void superDrawShape(Shape shape) {
            super.drawShape(shape);
            
        }


        @Override
        public void drawString(String string, int x, int y) {
            Image.this.drawString(string, x, y);
            
        }

        void superDrawString(String string, int x, int y) {
            super.drawString(string, x, y);
            
        }


        @Override
        public boolean equals(Object obj) {
            
            return Image.this.equals(obj);
        }

        boolean superEquals(Object obj) {
            
            return super.equals(obj);
        }

        
        @Override
        public void fill() {
            Image.this.fill();
            
        }

        void superFill() {
            super.fill();
            
        }


        @Override
        public void fillOval(int x, int y, int width, int height) {
            Image.this.fillOval(x, y, width, height);
            
        }

        void superFillOval(int x, int y, int width, int height) {
            super.fillOval(x, y, width, height);
            
        }


        @Override
        public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            Image.this.fillPolygon(xPoints, yPoints, nPoints);
            
        }

        void superFillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            super.fillPolygon(xPoints, yPoints, nPoints);
            
        }


        @Override
        public void fillRect(int x, int y, int width, int height) {
            Image.this.fillRect(x, y, width, height);
            
        }

        void superFillRect(int x, int y, int width, int height) {
            super.fillRect(x, y, width, height);
            
        }


        @Override
        public BufferedImage getAwtImage() {
            
            return Image.this.getAwtImage();
        }

        BufferedImage superGetAwtImage() {
            
            return super.getAwtImage();
        }


        @Override
        public greenfoot.Color getColor() {
            
            return Color.asGColor(Image.this.getColor());
        }

        greenfoot.Color superGetColor() {
            
            return super.getColor();
        }


        @Override
        public greenfoot.Color getColorAt(int x, int y) {
            
            return Color.asGColor(Image.this.getColorAt(x, y));
        }

        greenfoot.Color superGetColorAt(int x, int y) {
            
            return super.getColorAt(x, y);
        }


        @Override
        public Font getFont() {
            
            return Image.this.getFont();
        }

        Font superGetFont() {
            
            return super.getFont();
        }


        @Override
        public int getHeight() {
            
            return Image.this.getHeight();
        }

        int superGetHeight() {
            
            return super.getHeight();
        }


        @Override
        public int getTransparency() {
            
            return Image.this.getTransparency();
        }

        int superGetTransparency() {
            
            return super.getTransparency();
        }


        @Override
        public int getWidth() {
            
            return Image.this.getWidth();
        }

        int superGetWidth() {
            
            return super.getWidth();
        }


        @Override
        public int hashCode() {
            
            return Image.this.hashCode();
        }

        int superHashCode() {
            
            return super.hashCode();
        }


        @Override
        public void mirrorHorizontally() {
            
            Image.this.mirrorHorizontally();
        }

        void superMirrorHorizontally() {
            
            super.mirrorHorizontally();
        }


        @Override
        public void mirrorVertically() {
            
            Image.this.mirrorVertically();
        }

        void superMirrorVertically() {
            
            super.mirrorVertically();
        }


        @Override
        public void rotate(int degrees) {
            
            Image.this.rotate(degrees);
        }

        void superRotate(int degrees) {
            
            super.rotate(degrees);
        }


        @Override
        public void scale(int width, int height) {
            
            Image.this.scale(width, height);
        }

        void superScale(int width, int height) {
            
            super.scale(width, height);
        }


        @Override
        public void setColor(greenfoot.Color color) {
            
            Image.this.setColor(Color.of(color));
        }

        void superSetColor(greenfoot.Color color) {
            
            super.setColor(color);
        }


        @Override
        public void setColorAt(int x, int y, greenfoot.Color color) {
            
            Image.this.setColorAt(x, y, Color.of(color));
        }

        void superSetColorAt(int x, int y, greenfoot.Color color) {
            
            super.setColorAt(x, y, color);
        }


        @Override
        public void setFont(Font f) {
            
            Image.this.setFont(FontStyle.of(f));
        }

        void superSetFont(Font f) {
            
            super.setFont(f);
        }


        @Override
        public void setTransparency(int t) {
            
            Image.this.setTransparency(t);
        }

        void superSetTransparency(int t) {
            
            super.setTransparency(t);
        }
    }


    private class SupportGreenfootImageWrapper extends SupportGreenfootImage {

        private final GreenfootImage wrapped;

        private SupportGreenfootImageWrapper(GreenfootImage wrapped) {
            super(1, 1);
            this.wrapped = wrapped;
        }

        @Override
        void superClear() {
            
            wrapped.clear();
        }

        @Override
        void superDrawImage(GreenfootImage image, int x, int y) {
            
            wrapped.drawImage(image, x, y);
        }

        @Override
        void superDrawLine(int x1, int y1, int x2, int y2) {
            
            wrapped.drawLine(x1, y1, x2, y2);
        }

        @Override
        void superDrawOval(int x, int y, int width, int height) {
            
            wrapped.drawOval(x, y, width, height);
        }

        @Override
        void superDrawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            
            wrapped.drawPolygon(xPoints, yPoints, nPoints);
        }

        @Override
        void superDrawRect(int x, int y, int width, int height) {
            
            wrapped.drawRect(x, y, width, height);
        }

        @Override
        void superDrawShape(Shape shape) {
            
            wrapped.drawShape(shape);
        }

        @Override
        void superDrawString(String string, int x, int y) {
            
            wrapped.drawString(string, x, y);
        }

        @Override
        boolean superEquals(Object obj) {
            
            return wrapped.equals(obj);
        }

        @Override
        void superFill() {
            
            wrapped.fill();
        }

        @Override
        void superFillOval(int x, int y, int width, int height) {
            
            wrapped.fillOval(x, y, width, height);
        }

        @Override
        void superFillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            
            wrapped.fillPolygon(xPoints, yPoints, nPoints);
        }

        @Override
        void superFillRect(int x, int y, int width, int height) {
            
            wrapped.fillRect(x, y, width, height);
        }

        @Override
        BufferedImage superGetAwtImage() {
            
            return wrapped.getAwtImage();
        }

        @Override
        greenfoot.Color superGetColor() {
            
            return wrapped.getColor();
        }

        @Override
        greenfoot.Color superGetColorAt(int x, int y) {
            
            return wrapped.getColorAt(x, y);
        }

        @Override
        Font superGetFont() {
            
            return wrapped.getFont();
        }

        @Override
        int superGetHeight() {
            
            return wrapped.getHeight();
        }

        @Override
        int superGetTransparency() {
            
            return wrapped.getTransparency();
        }

        @Override
        int superGetWidth() {
            
            return wrapped.getWidth();
        }

        @Override
        int superHashCode() {
            
            return wrapped.hashCode();
        }

        @Override
        void superMirrorHorizontally() {
            
            wrapped.mirrorHorizontally();
        }

        @Override
        void superMirrorVertically() {
            
            wrapped.mirrorVertically();
        }

        @Override
        void superRotate(int degrees) {
            
            wrapped.rotate(degrees);
        }

        @Override
        void superScale(int width, int height) {
            
            wrapped.scale(width, height);
        }

        @Override
        void superSetColor(greenfoot.Color color) {
            
            wrapped.setColor(color);
        }

        @Override
        void superSetColorAt(int x, int y, greenfoot.Color color) {
            
            wrapped.setColorAt(x, y, color);
        }

        @Override
        void superSetFont(Font f) {
            
            wrapped.setFont(f);
        }

        @Override
        void superSetTransparency(int t) {
            
            wrapped.setTransparency(t);
        }
    }
}
