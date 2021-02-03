package com.github.rccookie.greenfoot.widgets;

import com.github.rccookie.common.geometry.Vector2D;

import greenfoot.GreenfootImage;

/**
 * Usually invisible widgets that are used to structure their child widgets.
 */
public class Structure extends Container {
    public Structure(Widget... children) {
        super(children);
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        return null;
    }

    @Override
    boolean contains(Vector2D loc) {
        for(Widget child : getChildren()) if(child.contains(loc)) return true;
        return false;
    }
}
