package com.github.rccookie.common.geometry;

public class Size2D extends Size {

    private static final long serialVersionUID = 3256906860871142183L;



    public Size2D(Size copy) {
        this(copy.get(0), copy.get(1));
    }

    public Size2D(double width, double height) {
        super(width, height);
    }



    public double width() {
        return get(0);
    }

    public double height() {
        return get(1);
    }

    @Override
    public double[] toArray() {
        return new double[] { width(), height() };
    }

    @Override
    public double getSpan() {
        return width() * height();
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public Size scaled(double factor) {
        return new Size2D(width() * factor, height() * factor);
    }
}
