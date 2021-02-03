package com.github.rccookie.greenfoot.ui.basic;

import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;
import com.github.rccookie.greenfoot.ui.basic.Slider;
import com.github.rccookie.common.geometry.Edge2D;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.common.util.ClassTag;
import com.github.rccookie.greenfoot.event.Input;
import com.github.rccookie.greenfoot.ui.util.Interactable;
import com.github.rccookie.greenfoot.ui.util.UIElement;

/**
 * A slider that lets you select a value in a given range. May be set to only allow integers.
 * 
 * @author RcCookie
 * @version 2.0
 */
public class Slider extends UIElement {

    static {
        ClassTag.tag(Slider.class, "ui");
    }


    public static final int WIDTH = 4;

    //private boolean useFractions = true;
    private double min, max;
    private int length;

    private Edge2D edge;
    private final Handle handle;

    public Slider(){
        this(0, 1, 100);
    }

    public Slider(double min, double max){
        this(min, max, 100);
    }

    public Slider(double min, double max, int length){
        this.min = min;
        this.max = max;
        this.length = length;
        if(length < 10) length = 10;

        handle = addSubElement(new Handle());
        Vector2D slideVector = Vector2D.angledVector(0, length);
        edge = new Edge2D(getLocation().subtract(slideVector.scaled(0.5)), slideVector);

        addPressAction(() -> {
            setLocation(edge.get(getPercentage(Input.mouseState().getLocation())));
            setValue(handle.value);
        });
        addAddedAction(world -> world.addObject(handle, getX(), getY()));
    }


    private double getPercentage(Vector2D loc) {
        double r = edge.edge().x() * (loc.x() - edge.root().x()) + edge.edge().y() * (loc.y() - edge.root().y());
        r /= edge.edge().x() * edge.edge().x() + edge.edge().y() * edge.edge().y();
        if(r < 0) r = 0;
        else if(r > 1) r = 1;
        return r;
    }

    private double range() {
        return max - min;
    }

    public double getValue() {
        return min + getPercentage(handle.getLocation()) * range();
    }

    public void allowFractions(boolean useFractions){
        //this.useFractions = useFractions;
    }

    public int getIntValue(){
        return (int)(getValue() + 0.5);
    }

    /*public double getValue(){
        if(handle.getWorld() == null) getWorld().addObject(handle, getX(), getY());
        double range = max - min;
        double sliderRange;
        double locFromSlider0;
        if(maxX() - minX() > maxY() - minY()){
            sliderRange = maxX() - minX();
            locFromSlider0 = handle.getX() - minX();
        }
        else{
            sliderRange = maxY() - minY();
            locFromSlider0 = handle.getY() - minY();
        }
        double percentage = locFromSlider0 / sliderRange;
        
        if(useFractions) return min + percentage * range;
        return (int)(min + percentage * range + 0.5);
    }*/

    @Override
    public void setRotation(int angle){
        super.setRotation(angle);
        handle.setRotation(angle);
        double oldValue = getValue();
        Vector2D slideVector = Vector2D.angledVector(0, length);
        edge = new Edge2D(getLocation().subtract(slideVector.scaled(0.5)), slideVector);
        setValue(oldValue);
    }

    @Override
    public void setLocation(int x, int y){
        double oldValue = getValue();
        super.setLocation(x, y);
        setValue(oldValue);
    }

    public void setValue(double value) {
        double percentage = (value - min) / (max - min);
        handle.value = value;
        //setLastValue(value);
        try{
            handle.setLocation(
                edge.get(percentage)
            );
        }catch(Exception e){}
    }

    @Override
    protected void assignDefaultColorMappings() {
        mapColor("slider", 0, false);
        mapColor("handle", 1, false);
    }

    @Override
    protected void regenerateImages() {
        GreenfootImage image = new GreenfootImage(length, WIDTH);
        image.setColor(elementColor("slider"));
        image.fillRect(WIDTH / 2, 0, length - WIDTH, WIDTH);
        image.fillOval(0, 0, WIDTH - 1, WIDTH - 1);
        image.fillOval(length - WIDTH, 0, WIDTH - 1, WIDTH - 1);
        setImage(image);
    }

    public boolean remove() {
        if(getWorld() == null) return false;
        handle.remove();
        return super.remove();
    }
    




    // --------------------------------------------
    // Handle class
    // --------------------------------------------

    public class Handle extends IconButton {

        public static final int SIZE = 14;

        Vector2D offset;
        public double value;

        public Handle() {
            super(generateHandleImage());
            addPressAction(() -> updateOffset());
            addReleaseAction(() -> offset = null);
            addAddedAction(world -> value = getValue());
        }

        @Override
        protected void createHoverImage(GreenfootImage base) {
            base.setColor(Interactable.HOVER_OUTLINE_COLOR);
            base.drawOval(0, 0, base.getWidth() - 1, base.getHeight() - 1);
            base.drawOval(1, 1, base.getWidth() - 3, base.getHeight() - 3);
        }

        public void run() {
            if(offset != null){
                try {
                    setLocation(edge.get(getPercentage(Input.mouseState().getLocation())));
                } catch(Exception e) {
                    offset = null;
                }
            }
            value = getValue();
            setValue(value);
        }

        public void updateOffset(){
            try{
                MouseInfo mouse = Input.mouseInfo();
                offset = new Vector2D(getX() - mouse.getX(), getY() - mouse.getY());
            }catch(Exception e){
                offset = null;
            }
        }
    }

    private GreenfootImage generateHandleImage(){
        GreenfootImage image = new GreenfootImage(Handle.SIZE, Handle.SIZE);
        image.setColor(elementColor("handle"));
        image.fillOval(0, 0, Handle.SIZE - 1, Handle.SIZE - 1);
        return image;
    }
}
