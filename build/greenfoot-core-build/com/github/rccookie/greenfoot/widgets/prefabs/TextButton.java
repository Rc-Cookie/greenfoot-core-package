package com.github.rccookie.greenfoot.widgets.prefabs;

import java.util.function.Consumer;

import com.github.rccookie.greenfoot.widgets.Text;
import com.github.rccookie.greenfoot.widgets.Button;
import com.github.rccookie.greenfoot.widgets.Color;

public class TextButton extends Button {

    static final String TEXT = "button_text";

    public TextButton(String title, greenfoot.Color textColor, greenfoot.Color background, Runnable onClick) {
        this(title, textColor, background, self -> onClick.run());
    }
    
    public TextButton(String title, greenfoot.Color textColor, greenfoot.Color background, Consumer<Button> onClick) {
        super(
            onClick,
            new Color(background),
            new Text(
                title,
                textColor
            ).setId(TEXT)
        );
    }
}
