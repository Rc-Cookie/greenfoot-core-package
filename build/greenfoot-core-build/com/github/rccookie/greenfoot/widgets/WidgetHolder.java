package com.github.rccookie.greenfoot.widgets;

import java.util.Objects;

import com.github.rccookie.greenfoot.core.CoreActor;

import greenfoot.GreenfootImage;
import greenfoot.World;

public class WidgetHolder extends CoreActor {

    public static long MIN_REPAINT_DIF = 10;

    private Widget widget;
    private int width, height;
    private long lastUpdateTime;



    public WidgetHolder(World world, Widget widget) {
        this(world.getWidth(), world.getHeight(), widget);
        world.addObject(this, world.getWidth() / 2, world.getHeight() / 2);
    }

    public WidgetHolder(int width, int height, Widget widget) {
        this.width = width;
        this.height = height;
        setWidget(widget);
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        if(this.widget != null) this.widget.holder = null;
        this.widget = widget;
        widget.holder = this;
    }



    @SuppressWarnings("unchecked")
    public <W extends Widget> W find(Class<W> type) {
        if(widget == null || type == null) return null;
        if(type.isInstance(widget)) return (W)widget;
        return widget.find(type);
    }

    public Widget find(String id) {
        if(widget == null) return null;
        if(Objects.equals(widget.getId(), id)) return widget;
        return widget.find(id);
    }

    @Override
    public void update() {
        if(widget == null) return;
        widget.runUpdate(new Size(width, height));
    }

    public void print() {
        print(false);
    }

    public void print(boolean withState) {
        if(widget != null) widget.printStack(withState);
        else System.out.println("null");
    }

    @Override
    public GreenfootImage getImage() {
        if(System.currentTimeMillis() - lastUpdateTime >=MIN_REPAINT_DIF) repaint();
        return super.getImage();
    }

    protected void repaint() {
        lastUpdateTime = System.currentTimeMillis();
        setImage(widget != null ? widget.getRenderedImage(new Size(width, height)) : null);
    }
}
