package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.common.geometry.Vector2D;

import greenfoot.GreenfootImage;

public class Column extends Structure {

    static final double WIDTH_HEIGHT_RATIO = 0.285; // From TextButton

    public Column(Widget... children) {
        super(children);
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        int childHeight = (int)(maxSize.width * WIDTH_HEIGHT_RATIO);

        final List<RenderTask> tasks = new ArrayList<>();
        for(int i=0; i<children.size(); i++) {
            GreenfootImage childRender = children.get(i).getRenderedImage(
                maxSize.modify().setHeight(childHeight).build()
            );
            if(childRender != null) {
                tasks.add(new RenderTask(
                    childRender,
                    maxSize.width / 2 - childRender.getWidth() / 2,
                    i * childHeight + childHeight / 2 - childRender.getHeight() / 2
                ));
            }

            children.get(i).setOffset(new Vector2D(0, (int)(childHeight * (i + 0.5)) - maxSize.height / 2));
        }
        return tasks;
    }

    @Override
    protected Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        return maxSize.modify().setHeight((int)(maxSize.width * WIDTH_HEIGHT_RATIO)).build();
    }
}
