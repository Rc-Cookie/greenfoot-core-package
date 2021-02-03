package com.github.rccookie.greenfoot.widgets;

import greenfoot.GreenfootImage;

public class Image extends Visual {

    private GreenfootImage image;

    public Image(GreenfootImage image) {
        this.image = image;
    }

    public GreenfootImage getImage() {
        return image;
    }

    public Image setImage(GreenfootImage image) {
        modify(() -> this.image = image);
        return this;
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        if(image == null) return null;
        GreenfootImage sizedImage = new GreenfootImage(maxSize.width, maxSize.height);
        sizedImage.drawImage(
            image,
            sizedImage.getWidth() / 2 - image.getWidth() / 2,
            sizedImage.getHeight() / 2 - image.getHeight() / 2
        );
        return sizedImage;
    }
}
