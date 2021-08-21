package com.github.rccookie.greenfoot.core;

import com.github.rccookie.geometry.Vector;
import com.github.rccookie.util.Console;
import greenfoot.GreenfootImage;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An image that is offline represented by a {@link BufferedImage}. It is
 * wrapping {@link GreenfootImage} but adds more convenience methods and
 * support for {@link Color} and {@link Font}.
 *
 * @author RcCookie
 * @version 2.1
 */
public class Image implements Cloneable {

    static {
        Core.initialize();
    }

    public static final Image EMPTY = new Image(1, 1) {
        @Override
        public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTransparency(int t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setColorAt(int x, int y, Color color) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void scale(int width, int height) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void rotate(int degrees) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void mirrorVertically() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void mirrorHorizontally() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void fillRect(int x, int y, int width, int height) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void fillOval(int x, int y, int width, int height) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void fill() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void drawString(String string, int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void drawOval(int x, int y, int width, int height) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void drawLine(int x1, int y1, int x2, int y2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void drawImage(Image image, int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Image clone() {
            return this;
        }
    };

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
     * Creates a new transparent image of the given size.
     *
     * @param size The size of the image
     */
    public Image(Vector size) {
        this((int)size.x(), ((int) size.y()));
    }

    /**
     * Creates a new image by trying to load from an image file with the
     * specified file name and path. Supported file types are jpg, png and
     * gif.
     *
     * @param filename The path and name of the image to load, for example
     *                 {@code "images/Car.png"}
     */
    private Image(String filename) {
        gImage = new SupportGreenfootImage(filename);
    }

    /**
     * Only meant to use from classes overriding {@link #getGImage()}.
     *
     * @see CopyOnDemandImage
     */
    private Image() {
        gImage = null;
    }

    /**
     * Creates a new image with the given string printed onto it. The image
     * will automatically be sized to exactly fot the string onto it. Offline
     * this will be a Calibri styled bond font, online a not-bold times roman.
     * The background of the image will be transparent.
     *
     * @param string The string to print
     * @param fontSize The font size of string
     * @param color The text color
     */
    private Image(String string, int fontSize, Color color) {
        gImage = new SupportGreenfootImage(string, fontSize, Color.asGColor(color));
    }

    /**
     * Creates a new image from the given {@link GreenfootImage}. It will be
     * wrapped rather than copied.
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
    public Image clone() {
        return new CopyOnDemandImage(this);
    }



    /**
     * Fills the image with the given color.
     *
     * @param color The color to fill with
     */
    public void fill(Color color) {
        drawWithColor(this::fill, color);
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

    public void drawString(String string, int x, int y, Color color, Font font) {
        setFont(font);
        drawWithColor(() -> drawString(string, x, y), color);
    }

    private void drawWithColor(Runnable operation, Color color) {
        setColor(color);
        operation.run();
    }

    public void scale(double factor) {
        scale(getSize().scale(factor));
    }

    public Color getColor() {
        return Color.of(getGImage().superGetColor());
    }

    public Color getColorAt(int x, int y) {
        return Color.of(getGImage().superGetColorAt(x, y));
    }

    public Font getFont() {
        return Font.of(getGImage().superGetFont());
    }

    public void setColor(Color color) {
        getGImage().superSetColor(Color.asGColor(color));
    }

    public void setColorAt(int x, int y, Color color) {
        getGImage().superSetColorAt(x, y, Color.asGColor(color));
    }

    public void setColorAt(Vector location, Color color) {
        setColorAt((int) (location.x() + 0.5), (int) (location.y() + 0.5), color);
    }

    public void setFont(Font font) {
        getGImage().superSetFont(font);
    }

    public void clear() {
        getGImage().superClear();
    }

    public void drawImage(Image image, int x, int y) {
        getGImage().superDrawImage(Image.asGImage(image), x, y);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        getGImage().superDrawLine(x1, y1, x2, y2);
    }

    public void drawOval(int x, int y, int width, int height) {
        getGImage().superDrawOval(x, y, width, height);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        getGImage().superDrawPolygon(xPoints, yPoints, nPoints);
    }

    public void drawRect(int x, int y, int width, int height) {
        getGImage().superDrawRect(x, y, width, height);
    }

    public void drawShape(Shape shape) {
        getGImage().superDrawShape(shape);
    }

    public void drawString(String string, int x, int y) {
        getGImage().superDrawString(string, x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof Image) return getGImage().superEquals(((Image)obj).getGImage());
        if(obj instanceof GreenfootImage) return getGImage().superEquals(obj);
        return false;
    }

    public void fill() {
        getGImage().superFill();
    }

    public void fillOval(int x, int y, int width, int height) {
        getGImage().superFillOval(x, y, width, height);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        getGImage().superFillPolygon(xPoints, yPoints, nPoints);
    }

    public void fillRect(int x, int y, int width, int height) {
        getGImage().superFillRect(x, y, width, height);
    }

    public BufferedImage getAwtImage() {
        return getGImage().superGetAwtImage();
    }

    public int getHeight() {
        return getGImage().superGetHeight();
    }

    public int getTransparency() {
        return getGImage().superGetTransparency();
    }

    public int getWidth() {
        return getGImage().superGetWidth();
    }

    public Vector getSize() {
        return Vector.of(getWidth(), getHeight());
    }

    @Override
    public int hashCode() {
        return getGImage().superHashCode();
    }

    public void mirrorHorizontally() {
        getGImage().superMirrorHorizontally();
    }

    public void mirrorVertically() {
        getGImage().superMirrorVertically();
    }

    public void rotate(int degrees) {
        getGImage().superRotate(degrees);
    }

    public void scale(int width, int height) {
        getGImage().superScale(width, height);
    }

    public void scale(Vector size) {
        scale((int) (size.x() + 0.5), (int) (size.y() + 0.5));
    }

    public void setTransparency(int t) {
        getGImage().superSetTransparency(t);
    }

    SupportGreenfootImage getGImage() {
        return gImage;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getWidth() + "x" + getHeight() + ")";
    }



    public GameObject asGameObject() {
        return new GameObject() { { setImage(Image.this); } };
    }



    public static Image block(int width, int height, Color color) {
        Image image = new Image(width, height);
        image.fill(color);
        return image;
    }

    public static Image block(Vector size, Color color) {
        return block((int)size.x(), (int)size.y(), color);
    }

    public static Image oval(int width, int height, Color color) {
        Image image = new Image(width, height);
        image.fillOval(0, 0, width, height, color);
        return image;
    }

    public static Image oval(Vector size, Color color) {
        return oval((int)size.x(), (int)size.y(), color);
    }

    /**
     * Creates a new transparent image with the given text written on it. This will
     * use Greenfoot's default font, which differs across platforms.
     *
     * @param string The string to print onto the image
     * @param fontSize The size of the font for the text
     * @param color The text color
     * @return The rendered image
     *
     * @deprecated {@link #text(String, Color, Font)} should be used instead with
     *             the font {@link Font#standard(int)}.
     */
    @Deprecated
    public static Image text(String string, int fontSize, Color color) {
        return new Image(string, fontSize, color);
    }

    public static Image text(String string, Color color, Font font) {
        if("Standard".equals(font.getName())) return new Image(string, font.getSize(), color);

        Image image = new Image(Math.max(1, font.getWidth(string)), Math.max(1, font.getHeight(string)));
        //image.fill(Color.LIGHT_GRAY); // For debugging purposes
        image.drawString(string, 0, (int)(font.getSize() * 0.75), color, font);
        return image;
    }

    public static Image load(String fileName) {
        return new Image(fileName);
    }

    public static Image of(GreenfootImage gImage) {
        return new Image(gImage);
    }

    public static GreenfootImage asGImage(Image image) {
        return image != null ? image.getGImage() : null;
    }


    /**
     * This class represents an image that was created by copying another image. The
     * copied image does not get copied right away, it only gets copied if this image
     * should be modified. This way copying an image is cheap and can still be used to
     * return an image without the ability to modify the old image.
     */
    private static class CopyOnDemandImage extends Image {

        private SupportGreenfootImage gImage;
        private boolean modified = false;

        private CopyOnDemandImage(Image copy) {
            gImage = copy.getGImage();
        }

        @Override
        SupportGreenfootImage getGImage() {
            if(!modified) {
                Console.debug("Copying underlying image from copied image");
                modified = true;
                // Create copy of underlying GreenfootImage
                gImage = new SupportGreenfootImage(new GreenfootImage(gImage));
            }
            return gImage;
        }

        @Override
        public Color getColor() {
            return Color.of(gImage.superGetColor());
        }

        @Override
        public Color getColorAt(int x, int y) {
            return Color.of(gImage.superGetColorAt(x, y));
        }

        @Override
        public Font getFont() {
            return Font.of(gImage.superGetFont());
        }
    }

    private class SupportGreenfootImage extends GreenfootImage {

        private SupportGreenfootImage(GreenfootImage image) {
            super(image);
        }

        private SupportGreenfootImage(int width, int height) {
            super(width, height);
        }

        private SupportGreenfootImage(String string, int fontSize, greenfoot.Color color) {
            super(string, fontSize, color, new greenfoot.Color(0,0,0,0));
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

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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
            return getGImage() != null ? Image.this.getAwtImage() : superGetAwtImage();
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
        public greenfoot.Font getFont() {
            return Image.this.getFont();
        }

        greenfoot.Font superGetFont() {
            return super.getFont();
        }

        @Override
        public int getHeight() {
            return getGImage() != null ? Image.this.getHeight() : superGetHeight();
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
            return getGImage() != null ? Image.this.getWidth() : superGetWidth();
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
        public void setFont(greenfoot.Font f) {
            Image.this.setFont(Font.of(f));
        }

        void superSetFont(greenfoot.Font f) {
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
        greenfoot.Font superGetFont() {
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
        void superSetFont(greenfoot.Font f) {
            wrapped.setFont(f);
        }

        @Override
        void superSetTransparency(int t) {
            wrapped.setTransparency(t);
        }
    }
}
