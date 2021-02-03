package com.github.rccookie.greenfoot.widgets.prefabs;

import com.github.rccookie.greenfoot.widgets.Align;
import com.github.rccookie.greenfoot.widgets.Dimension;
import com.github.rccookie.greenfoot.widgets.FpsDisplay;

public class FPS extends Dimension {

    public static final int WIDTH = 60, HEIGHT = 18;

    public FPS() {
        super(
            WIDTH,
            HEIGHT,
            Align.left(
                new FpsDisplay()
            )
        );
    }
    
    public FPS(greenfoot.Color color) {
        super(
            WIDTH,
            HEIGHT,
            Align.left(
                new FpsDisplay()
            )
        );
    }
}
