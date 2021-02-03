package com.github.rccookie.greenfoot.widgets;

import greenfoot.Color;
import greenfoot.GreenfootImage;

import static com.github.rccookie.greenfoot.widgets.Color.TRANSPARENT;

public class BigText extends Text {

    public BigText(String title) {
        super(title);
    }

    public BigText(String title, Color color) {
        super(title, color);
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        String title = getTitle();
        Color color = getColor();
        GreenfootImage text = new GreenfootImage(title, maxSize.height, color, TRANSPARENT);
        if(text.getWidth() > maxSize.width)
            text = new GreenfootImage(title, (int)(maxSize.height * (maxSize.width / (double)text.getWidth())), color, TRANSPARENT);
        if(text.getHeight() > maxSize.height)
            text.scale((int)(text.getWidth() * maxSize.height / (double)text.getHeight()), maxSize.height);
        return text;
    }
}
