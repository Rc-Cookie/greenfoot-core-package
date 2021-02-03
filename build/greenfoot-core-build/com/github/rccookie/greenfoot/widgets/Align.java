package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.common.geometry.Vector2D;

import greenfoot.GreenfootImage;

public class Align extends Structure {

    private int xalign, yalign;

    private Align(int xalign, int yalign, Widget... children) {
        super(children);
        this.xalign = xalign;
        this.yalign = yalign;
    }

    public int getXalign() {
        return xalign;
    }

    public int getYalign() {
        return yalign;
    }

    public void setXalign(int xalign) {
        modify(() -> this.xalign = xalign);
    }

    public void setYalign(int yalign) {
        modify(() -> this.yalign = yalign);
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        final List<RenderTask> tasks = new ArrayList<>();
        for(Widget child : children) {
            GreenfootImage childRender = child.getRenderedImage(maxSize);
            if(childRender == null) continue;
            Vector2D offset = new Vector2D(
                xalign == 0 ? 0 : (
                    (xalign < 0 ? 1 : -1) * (childRender.getWidth() / 2 - maxSize.width / 2)
                ),
                yalign == 0 ? 0 : (
                    (yalign < 0 ? 1 : -1) * (childRender.getHeight() / 2 - maxSize.height / 2)
                )
            );
            final int x, y;
            if(xalign < 0) x = 0;
            else if(xalign > 0) x = maxSize.width - childRender.getWidth();
            else x = maxSize.width / 2 - childRender.getWidth() / 2;
            if(yalign < 0) y = 0;
            else if(yalign > 0) y = maxSize.height - childRender.getHeight();
            else y = maxSize.height / 2 - childRender.getHeight() / 2;
            tasks.add(new RenderTask(
                childRender,
                x,
                y
            ));
            child.setOffset(offset);
        }
        return tasks;
    }

    @Override
    public String toString() {
        return super.toString() + " (x: " + xalign + ", y: " + yalign + ")";
    }

    /**
     * Returns an Align that alignes its child according to the given values.
     * 
     * @param xalign The x axis alignment. 0 means centered, less that 0 means
     *               left aligned, greater that 0 means right aligned
     * @param yalign The y axis alignment. 0 means centered, less that 0 means
     *               top aligned, greater that 0 means bottom aligned
     * @param child The child of the Align
     * @return The according Align
     */
    public static Align custom(int xalign, int yalign, Widget... children) {
        return new Align(xalign, yalign, children);
    }

    public static Align center(Widget... children) {
        return new Align(0, 0, children);
    }

    public static Align top(Widget... children) {
        return new Align(0, -1, children);
    }

    public static Align bottom(Widget... children) {
        return new Align(0, 1, children);
    }

    public static Align left(Widget... children) {
        return new Align(-1, 0, children);
    }

    public static Align right(Widget... children) {
        return new Align(1, 0, children);
    }

    public static Align topleft(Widget... children) {
        return new Align(-1, -1, children);
    }

    public static Align bottomleft(Widget... children) {
        return new Align(-1, 1, children);
    }

    public static Align topright(Widget... children) {
        return new Align(1, -1, children);
    }

    public static Align bottomright(Widget... children) {
        return new Align(1, 1, children);
    }
}
