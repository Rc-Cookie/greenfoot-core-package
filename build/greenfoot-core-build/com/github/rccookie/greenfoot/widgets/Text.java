package com.github.rccookie.greenfoot.widgets;

import greenfoot.Color;
import greenfoot.GreenfootImage;

import static com.github.rccookie.greenfoot.widgets.Color.TRANSPARENT;

import java.util.Objects;

public class Text extends Visual {

    protected static final double TEXT_SCALE = 0.625;

    private String title;
    private Color color;
    private boolean scaleIfNeeded = true;

    public Text(String title) {
        this(title, Color.BLACK);
    }

    public Text(String title, Color color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public Color getColor() {
        return color;
    }

    public boolean scalesIfNeeded() {
        return scaleIfNeeded;
    }

    public Text setTitle(String title) {
        if(Objects.equals(this.title, title)) return this;
        modify(() -> this.title = title);
        return this;
    }

    public Text setColor(Color color) {
        if(Objects.equals(this.color, color)) return this;
        modify(() -> this.color = color);
        return this;
    }

    public Text setScaleIfNeeded(boolean flag) {
        if(scaleIfNeeded == flag) return this;
        modify(() -> scaleIfNeeded = flag);
        return this;
    }



    @Override
    protected GreenfootImage createImage(Size maxSize) {
        GreenfootImage text = new GreenfootImage(getTitle(), (int)(maxSize.height * TEXT_SCALE), getColor(), TRANSPARENT);
        if(text.getWidth() > maxSize.width) {
            if(scalesIfNeeded())
                text = new GreenfootImage(title, (int)(maxSize.height * TEXT_SCALE * (maxSize.width / (double)text.getWidth())), color, TRANSPARENT);
            else {
                GreenfootImage cut = new GreenfootImage(maxSize.width, maxSize.height);
                cut.drawImage(text, cut.getWidth() / 2 - text.getWidth() / 2, cut.getHeight() / 2 - text.getHeight() / 2);
                text = cut;
            }
        }
        if(text.getHeight() > maxSize.height)
            text.scale((int)(text.getWidth() * maxSize.height / (double)text.getHeight()), maxSize.height);
        return text;
    }

    @Override
    public String toString() {
        return super.toString() + " (\"" + title + "\")";
    }
}
