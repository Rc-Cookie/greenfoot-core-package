package com.github.rccookie.common.geometry;

import java.io.Serializable;
import java.util.Objects;

public class Size implements Serializable {

    private static final long serialVersionUID = 3238947272367667859L;



    private final double[] sizes;



    public Size(Size copy) {
        this(copy.sizes);
    }

    public Size(double... sizes) {
        Objects.requireNonNull(sizes);
        this.sizes = new double[sizes.length];
        System.arraycopy(sizes, 0, this.sizes, 0, sizes.length);
    }



    public double get(int dimension) {
        return sizes[dimension];
    }

    public double[] toArray() {
        double[] out = new double[sizes.length];
        System.arraycopy(sizes, 0, out, 0, sizes.length);
        return out;
    }

    public int size() {
        return sizes.length;
    }

    public double getSpan() {
        if(sizes.length == 0) return 0;
        double span = sizes[0];
        for(int i=1; i<sizes.length; i++) span *= sizes[i];
        return span;
    }



    public Size scaled(double factor) {
        double[] scaledSizes = toArray();
        for(int i=0; i<scaledSizes.length; i++) scaledSizes[i] = scaledSizes[i] * factor;
        return new Size(scaledSizes);
    }
}
