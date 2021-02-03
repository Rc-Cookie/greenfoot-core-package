package com.github.rccookie.greenfoot.widgets.prefabs;

import com.github.rccookie.greenfoot.widgets.Offset;
import com.github.rccookie.greenfoot.widgets.Scale;
import com.github.rccookie.greenfoot.widgets.Structure;
import com.github.rccookie.greenfoot.widgets.Text;
import com.github.rccookie.greenfoot.widgets.Widget;

public class Checkbox extends Structure {

    protected static final String BOX_BUTTON_ID = "checkboxbutton";

    public Checkbox(String title) {
        this(title, false);
    }

    public Checkbox(String title, boolean ticked) {
        this(new Text(title), ticked);
    }

    public Checkbox(Widget info, boolean ticked) {
        super(
            new Offset(
                0.15,
                0.5,
                new Scale(
                    0.5,
                    new CheckboxButton(ticked).setId(BOX_BUTTON_ID)
                )
            ),
            new Offset(
                0.6,
                0.5,
                info
            )
        );
    }

    public void click() {
        find(BOX_BUTTON_ID).as(CheckboxButton.class).click();
    }

    public void setTicked(boolean flag) {
        find(BOX_BUTTON_ID).as(CheckboxButton.class).setTicked(flag);
    }
}
