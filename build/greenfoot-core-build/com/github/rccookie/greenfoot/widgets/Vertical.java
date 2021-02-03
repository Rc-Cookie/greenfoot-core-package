package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.common.geometry.Vector2D;

import greenfoot.GreenfootImage;

public class Vertical extends Structure {

    public Vertical(Widget... children) {
        super(children);
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        int childHeight = maxSize.height / children.size();
        int dif = maxSize.height - childHeight * children.size();
        int off = 0;

        final List<RenderTask> tasks = new ArrayList<>();
        for(int i=0; i<children.size(); i++) {
            GreenfootImage childRender = children.get(i).getRenderedImage(
                maxSize.modify().setHeight(childHeight + (i < dif ? 1 : 0)).build()
            );
            if(childRender != null) {
                tasks.add(new RenderTask(
                    childRender,
                    maxSize.width / 2 - childRender.getWidth() / 2,
                    i * childHeight + off
                ));
            }
            if(i < dif) off++;

            children.get(i).setOffset(new Vector2D(0, (int)(childHeight * (i + 0.5)) - maxSize.height / 2));
        }
        return tasks;
    }

    @Override
    protected Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        int childHeight = maxSize.height / children.size();
        int dif = maxSize.height - childHeight * children.size();
        return maxSize.modify().setHeight(childHeight + (children.indexOf(child) < dif ? 1 : 0)).build();
    }
}
