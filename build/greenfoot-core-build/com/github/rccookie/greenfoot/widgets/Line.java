package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.common.geometry.Vector2D;

import greenfoot.GreenfootImage;

public class Line extends Structure {

    public Line(Widget... children) {
        super(children);
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        final List<RenderTask> tasks = new ArrayList<>();
        int usedSpace = 0;
        for(int i=0; i<children.size(); i++) {
            GreenfootImage childRender = children.get(i).getRenderedImage(
                new Size(maxSize.width - usedSpace, maxSize.height)
            );
            if(childRender != null) {
                tasks.add(new RenderTask(
                    childRender,
                    usedSpace,
                    maxSize.height / 2 - childRender.getHeight() / 2
                ));
            }
            usedSpace += childRender.getWidth();
        }
        int actualWidth = usedSpace;
        int dif = (maxSize.width - usedSpace) / 2;
        if(maxSize.width - usedSpace > 0) {
            for(int i=0; i<tasks.size(); i++) {
                tasks.set(i, new RenderTask(tasks.get(i).image, tasks.get(i).x + dif, tasks.get(i).y));
                children.get(i).setOffset(new Vector2D(-dif).add(getOffset()));
            }
        }
        usedSpace = 0;
        for(int i=0; i<tasks.size(); i++) {
            int imageWidth = tasks.get(i).image != null ? tasks.get(i).image.getWidth() : 0;
            children.get(i).setOffset(new Vector2D(usedSpace + -actualWidth / 2 + imageWidth / 2));
            usedSpace += imageWidth;
        }
        return tasks;
    }
}
