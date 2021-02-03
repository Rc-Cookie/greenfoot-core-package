package com.github.rccookie.greenfoot.widgets;

import java.util.function.Consumer;

import greenfoot.GreenfootImage;

public class ImageButton extends Button {

    private GreenfootImage image, touched, pressed, disabled;
    private boolean scale = true;

    public ImageButton(GreenfootImage image, Runnable onClick) {
        this(image, onClick != null ? b -> onClick.run() : null);
    }

    public ImageButton(GreenfootImage image, Consumer<Button> onClick) {
        super(onClick);
        this.image = image;
    }

    public GreenfootImage getImage() {
        return image;
    }

    public GreenfootImage getTouched() {
        return touched;
    }

    public GreenfootImage getPressed() {
        return pressed;
    }

    public GreenfootImage getDisabled() {
        return disabled;
    }

    public boolean getScale() {
        return scale;
    }

    public ImageButton setImage(GreenfootImage image) {
        modify(() -> this.image = image);
        return this;
    }

    public ImageButton setTouched(GreenfootImage touched) {
        modify(() -> this.touched = touched);
        return this;
    }

    public ImageButton setPressed(GreenfootImage pressed) {
        modify(() -> this.pressed = pressed);
        return this;
    }

    public ImageButton setDisabled(GreenfootImage disabled) {
        modify(() -> this.disabled = disabled);
        return this;
    }

    public ImageButton setScale(boolean flag) {
        if(scale == flag) return this;
        modify(() -> scale = flag);
        return this;
    }



    @Override
    protected GreenfootImage createImage(Size maxSize) {
        if(image == null) return null;
        if(getScale()) {
            GreenfootImage image = new GreenfootImage(this.image);
            image.scale(maxSize.width, maxSize.height);
            return image;
        }
        else {
            GreenfootImage image = new GreenfootImage(maxSize.width, maxSize.height);
            image.drawImage(this.image, image.getWidth() / 2 - this.image.getWidth() / 2, image.getHeight() / 2 - this.image.getHeight() / 2);
            return image;
        }
    }

    @Override
    protected GreenfootImage renderTouched(GreenfootImage base) {
        if(touched != null) return touched;
        return super.renderTouched(base);
    }

    @Override
    protected GreenfootImage renderPressed(GreenfootImage base) {
        if(pressed != null) return pressed;
        return super.renderPressed(base);
    }

    @Override
    protected GreenfootImage renderDisabled(GreenfootImage base) {
        if(disabled != null) return disabled;
        return super.renderDisabled(base);
    }
}
