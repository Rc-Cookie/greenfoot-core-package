package com.github.rccookie.greenfoot.widgets.prefabs;

import com.github.rccookie.greenfoot.widgets.Button;
import com.github.rccookie.greenfoot.widgets.CheckboxIcon;

public class CheckboxButton extends Button {

    protected static final String BOX_ICON_ID = "checkboxicon";
    
    public CheckboxButton() {
        this(false);
    }

    public CheckboxButton(boolean ticked) {
        super(
            self -> self.modify(() -> self.find(BOX_ICON_ID).as(CheckboxIcon.class).swap()),
            new CheckboxIcon(ticked).setId(BOX_ICON_ID)
        );
        useHoverOutline(false);
    }

    public void setTicked(boolean flag) {
        find(BOX_ICON_ID).as(CheckboxIcon.class).setTicked(flag);
    }
}
