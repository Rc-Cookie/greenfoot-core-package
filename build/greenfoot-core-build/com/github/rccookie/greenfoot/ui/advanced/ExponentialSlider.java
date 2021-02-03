package com.github.rccookie.greenfoot.ui.advanced;

import greenfoot.Color;
import com.github.rccookie.greenfoot.ui.basic.Slider;

public class ExponentialSlider extends Slider {
    
    private final int exponent;


    public ExponentialSlider(double min, double max, int length, Color sliderCol, Color handleCol) {
        this(min, max, length, sliderCol, handleCol, 2);
    }

    public ExponentialSlider(double min, double max, int length, Color sliderCol, Color handleCol, int exponent) {
        super(Math.sqrt(min), Math.sqrt(max), length);
        this.exponent = exponent;
    }

    @Override
    public double getValue() {
        return Math.pow(super.getValue(), exponent);
    }

    @Override
    public void setValue(double value) {
        super.setValue(Math.pow(value, 1d / exponent));
    }
}
