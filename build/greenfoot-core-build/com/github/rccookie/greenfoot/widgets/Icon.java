package com.github.rccookie.greenfoot.widgets;

import java.util.HashMap;

import com.github.rccookie.common.util.Console;

import greenfoot.GreenfootImage;

public class Icon extends Visual {

    private GreenfootImage icon;

    private Icon(GreenfootImage icon) {
        this.icon = icon;
    }

    public GreenfootImage getIconImage() {
        return icon;
    }

    public void setIconImage(GreenfootImage icon) {
        modify(() -> this.icon = icon);
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        double scale = Math.min(maxSize.width / (double)icon.getWidth(), maxSize.height / (double)icon.getHeight());
        GreenfootImage image = new GreenfootImage(icon);
        image.scale((int)(image.getWidth() * scale), (int)(image.getHeight() * scale));
        return image;
    }






    private static final HashMap<String, GreenfootImage> ICONS = new HashMap<>();
    private static final GreenfootImage LOADING_ERROR = new GreenfootImage("NO FILE", 20, Color.BLACK, Color.ORANGE);

    public static final GreenfootImage loadIconImage(String name) {
        if(ICONS.containsKey(name)) return ICONS.get(name);
        try {
            GreenfootImage icon = new GreenfootImage("images/recources/" + name + ".png");
            ICONS.put(name, icon);
            return icon;
        } catch(Exception e) { }
        try {
            GreenfootImage icon = new GreenfootImage("images/recources/" + name + ".jpg");
            ICONS.put(name, icon);
            return icon;
        } catch(Exception e) {
            Console.log("Could not load image " + name);
            return LOADING_ERROR;
        }
    }

    private static final Icon create(GreenfootImage icon) {
        return new Icon(new GreenfootImage(icon));
    }

    public static Icon checkbox(boolean checked) {
        return create(loadIconImage("checkbox_" + (checked ? "ticked" : "unticked")));
    }

    public static Icon load(String name) {
        return create(loadIconImage(name));
    }
}
