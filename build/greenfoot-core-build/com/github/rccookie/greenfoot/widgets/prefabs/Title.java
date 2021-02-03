package com.github.rccookie.greenfoot.widgets.prefabs;

import com.github.rccookie.greenfoot.widgets.BigText;
import com.github.rccookie.greenfoot.widgets.Color;
import com.github.rccookie.greenfoot.widgets.Dimension;
import com.github.rccookie.greenfoot.widgets.Offset;

public class Title extends Offset {

    public Title(String text) {
        this(text, Color.WHITE);
    }
    
    public Title(String text, greenfoot.Color color) {
        super(
            0.5,
            0.2,
            new Dimension(
                -10,
                32,
                new BigText(text, color)
            )
        );
    }
}
