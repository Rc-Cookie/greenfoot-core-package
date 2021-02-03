package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import greenfoot.GreenfootImage;

public class Scale extends Structure {
    
    private double scale;

    public Scale(double scale, Widget... children) {
        super(children);
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        if(scale <= 0 || !Double.isFinite(scale)) throw new IllegalArgumentException("Cannot scale by " + scale);
        checkSafetyRange(scale);
        modify(() -> this.scale = scale);
    }

    protected void checkSafetyRange(double scale) {
        if(scale > 10) System.err.println("WARNING: Scaling by the factor " + scale + " is not reccomended and might cause lag");
    }

    @Override
    public int getWidth(Size maxSize) {
        double scale = getScale();
        if(scale >= 1) return maxSize.width;
        return (int)(maxSize.width * scale);
    }

    @Override
    public int getHeight(Size maxSize) {
        double scale = getScale();
        if(scale >= 1) return maxSize.height;
        return (int)(maxSize.height * scale);
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        final List<RenderTask> tasks = new ArrayList<>();
        for(int i=0; i<children.size(); i++) {
            if(children.get(i) == null) continue;
            GreenfootImage childRender = children.get(i).getRenderedImage(getChildSize(children.get(i), children, maxSize));
            if(childRender != null) {
                tasks.add(new RenderTask(
                    childRender,
                    maxSize.width / 2 - childRender.getWidth() / 2,
                    maxSize.height / 2 - childRender.getHeight() / 2
                ));
            }
            children.get(i).setOffset(null);
        }
        return tasks;
    }

    @Override
    protected Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        return maxSize.modify().setWidth(getWidth(maxSize)).setHeight(getHeight(maxSize)).build();
    }

    @Override
    public String toString() {
        return super.toString() + " (x" + scale + ")";
    }
}
