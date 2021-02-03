package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import greenfoot.GreenfootImage;

public class Dimension extends Structure {

    private int maxWidth, maxHeight;

    public Dimension(int width, int height, Widget... children) {
        super(children);
        this.maxWidth = width;
        this.maxHeight = height;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public int getWidth(Size maxSize) {
        return maxWidth > 0 ? Math.min(maxSize.width, maxWidth) : Math.max(maxSize.width + maxWidth, 1);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public int getHeight(Size maxSize) {
        return maxHeight > 0 ? Math.min(maxSize.height, maxHeight) : Math.max(maxSize.height + maxHeight, 1);
    }

    public Dimension setMaxWidth(int maxWidth) {
        modify(() -> this.maxWidth = maxWidth);
        return this;
    }

    public Dimension setMaxHeight(int maxHeight) {
        modify(() -> this.maxHeight = maxHeight);
        return this;
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        return new GreenfootImage(
            getWidth(maxSize),
            getHeight(maxSize)
        );
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        final List<RenderTask> tasks = new ArrayList<>();
        for(int i=0; i<children.size(); i++) {
            if(children.get(i) == null) continue;
            Size childSize = getChildSize(children.get(i), children, maxSize);
            GreenfootImage childRender = children.get(i).getRenderedImage(childSize);
            if(childRender != null) {
                tasks.add(new RenderTask(
                    childRender,
                    childSize.width / 2 - childRender.getWidth() / 2,
                    childSize.height / 2 - childRender.getHeight() / 2
                ));
            }
            children.get(i).setOffset(null);
        }
        return tasks;
    }

    @Override
    protected Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        return new Size(getWidth(maxSize), getHeight(maxSize));
    }

    @Override
    public String toString() {
        return super.toString() + " (" + 
            (maxWidth == 0 ? '\u221E' : ((maxWidth < 0 ? "max" : "") + maxWidth)) + " x " +
            (maxHeight == 0 ? '\u221E' : ((maxHeight < 0 ? "max" : "") + maxHeight)) + ")";
    }
}
