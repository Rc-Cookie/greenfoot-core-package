package com.github.rccookie.greenfoot.widgets.prefabs;

import java.util.function.Consumer;

import com.github.rccookie.greenfoot.widgets.Button;
import com.github.rccookie.greenfoot.widgets.Dimension;
import com.github.rccookie.greenfoot.widgets.Widget;

public class SizedButton extends Dimension {

    private Button actualButton;



    public SizedButton(String title, greenfoot.Color textColor, greenfoot.Color background, Runnable onClick) {
        this(title, textColor, background, self -> onClick.run());
    }
    
    public SizedButton(String title, greenfoot.Color textColor, greenfoot.Color background, Consumer<Button> onClick) {
        this(new TextButton(title, textColor, background, onClick));
    }

    public SizedButton(Runnable onClick, Widget... buttonChildren) {
        this(self -> onClick.run(), buttonChildren);
    }

    public SizedButton(Consumer<Button> onClick, Widget... buttonChildren) {
        this(new Button(onClick, buttonChildren));
    }

    public SizedButton(Button actualButton) {
        this(actualButton, 112, 31);
    }

    public SizedButton(Button actualButton, int maxWidth, int maxHeight) {
        super(
            maxWidth,
            maxHeight,
            actualButton
        );
        this.actualButton = actualButton;
    }



    /**
     * Returns the actual button instance used in this sized button.
     * 
     * @return The button that backs this sized button.
     */
    public Button button() {
        return actualButton;
    }

    public void click() {
        button().click();
    }
}
