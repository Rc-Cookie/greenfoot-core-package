package com.github.rccookie.greenfoot.widgets;

import java.util.List;

public class DebugDisplay extends Structure {

    public DebugDisplay() {
        this(Color.GREEN);
    }
    
    public DebugDisplay(greenfoot.Color color) {
        super(
            new Color(color),
            new Dimension(
                -4,
                -4,
                new Color(Color.DARK_GRAY)
            ),
            new Text("?", Color.LIGHT_GRAY)
        );
    }

    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        find(Text.class).setTitle(getParent() + "");
        return super.getChildrenRenderTasks(maxSize, children);
    }
}
