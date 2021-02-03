package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.common.geometry.Vector2D;

import greenfoot.GreenfootImage;

public class Row extends Structure {

    public Row(Widget... children) {
        super(children);
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        final List<RenderTask> tasks = new ArrayList<>();
        int childWidth = maxSize.width / children.size();
        int dif = maxSize.width - childWidth * children.size();
        int off = 0;

        for(int i=0; i<children.size(); i++) {
            GreenfootImage childRender = children.get(i).getRenderedImage(
                getChildSize(children.get(i), children, maxSize)
            );
            if(childRender != null) {
                tasks.add(new RenderTask(
                    childRender,
                    i * childWidth + off,
                    maxSize.height / 2 - childRender.getHeight() / 2
                ));
            }
            if(i < dif) off++;

            children.get(i).setOffset(new Vector2D((int)(childWidth * (i + 0.5)) - maxSize.height / 2, 0));
        }
        return tasks;
    }

    @Override
    protected Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        int childWidth = maxSize.width / children.size();
        int dif = maxSize.width - childWidth * children.size();
        return maxSize.modify().setWidth(childWidth + (children.indexOf(child) < dif ? 1 : 0)).build();
    }
}
