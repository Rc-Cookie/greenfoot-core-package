package com.github.rccookie.greenfoot.ui;

import greenfoot.GreenfootImage;
import com.github.rccookie.greenfoot.ui.util.Interactable;

/**
 * A button that shows an icon rather than a text.
 */
public class IconButton extends Interactable {

    private GreenfootImage icon;

    public IconButton(GreenfootImage icon) {
        this.icon = icon;
    }

    @Override
    protected GreenfootImage createMainImage() {
        return icon;
    }

    public GreenfootImage getIcon() {
        return icon;
    }

    public void setIcon(GreenfootImage icon) {
        this.icon = icon;
        imageChanged();
    }

    @Override
    protected void assignDefaultColorMappings() { }
}
