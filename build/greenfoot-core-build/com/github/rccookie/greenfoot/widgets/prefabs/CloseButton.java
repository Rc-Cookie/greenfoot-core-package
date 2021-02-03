package com.github.rccookie.greenfoot.widgets.prefabs;

import com.github.rccookie.greenfoot.widgets.Dimension;
import com.github.rccookie.greenfoot.widgets.Icon;
import com.github.rccookie.greenfoot.widgets.ImageButton;

import greenfoot.GreenfootImage;

public class CloseButton extends Dimension {

    public CloseButton(Runnable onClick) {
        super(
            46,
            30,
            new ImageButton(
                Icon.loadIconImage("close_normal"),
                onClick
            ) {
                private final GreenfootImage touched = Icon.loadIconImage("close_touched");
                protected GreenfootImage renderPressed(GreenfootImage base) {
                    return super.renderDisabled(touched);
                };
            }.setTouched(Icon.loadIconImage("close_touched"))
        );
    }

    public void click() {
        find(ImageButton.class).click();
    }
}
