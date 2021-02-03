package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.common.geometry.Vectors;

import greenfoot.GreenfootImage;

public class Offset extends Structure {

    private Vector2D off, dif;

    public Offset(double x, double y, Widget... children) {
        this(x, y, 0, 0, children);
    }

    public Offset(double x, double y, double dx, double dy, Widget... children) {
        this(new Vector2D(x, y), new Vector2D(dx, dy), children);
    }

    public Offset(Vector2D off, Vector2D dif, Widget...children) {
        super(children);
        setOff(off);
        setDif(dif);
    }

    public void setOff(Vector2D off) {
        modify(() -> this.off = Vectors.immutableVector(off != null ? off.clone() : new Vector2D(0.5, 0.5)));
    }

    public void setDif(Vector2D dif) {
        modify(() -> this.dif = Vectors.immutableVector(dif != null ? dif.clone() : new Vector2D(0, 0)));
    }

    public Vector2D getOff() {
        return off;
    }

    public Vector2D getDif() {
        return dif;
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        final List<RenderTask> tasks = new ArrayList<>();
        for(Widget child : children) {
            GreenfootImage childRender = child.getRenderedImage(maxSize);
            if(childRender == null) continue;
            tasks.add(new RenderTask(
                childRender,
                (int)(getWidth(maxSize) * off.x() + dif.x()) - childRender.getWidth() / 2,
                (int)(getHeight(maxSize) * off.y() + dif.y()) - childRender.getHeight() / 2
            ));
            Vector2D offset = new Vector2D(-0.5, -0.5).add(off);
            offset.setX(offset.x() * maxSize.width);
            offset.setY(offset.y() * maxSize.height);
            offset.add(dif);
            child.setOffset(offset);
        }
        return tasks;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + off + (dif.isZero() ? "" : " + " + dif) + ")";
    }
}
