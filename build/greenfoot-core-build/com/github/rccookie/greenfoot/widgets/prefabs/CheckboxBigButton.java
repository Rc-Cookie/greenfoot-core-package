package com.github.rccookie.greenfoot.widgets.prefabs;

import com.github.rccookie.greenfoot.widgets.Button;
import com.github.rccookie.greenfoot.widgets.CheckboxIcon;
import com.github.rccookie.greenfoot.widgets.Offset;
import com.github.rccookie.greenfoot.widgets.Scale;
import com.github.rccookie.greenfoot.widgets.Text;

public class CheckboxBigButton extends Button {

    protected static final String BOX_ICON_ID = "checkboxicon";

    public CheckboxBigButton(String title) {
        this(title, false);
    }
    
    public CheckboxBigButton(String title, boolean ticked) {
        super(
            self -> self.modify(() -> self.find(BOX_ICON_ID).as(CheckboxIcon.class).swap()),
            new Offset(
                0.1,
                0.5,
                new Scale(
                    0.5,
                    new CheckboxIcon(ticked).setId(BOX_ICON_ID)
                )
            ),
            new Offset(
                0.6,
                0.5,
                new Text(title)
            )
        );
        setDynamic(false);
    }

    public void setTicked(boolean flag) {
        find(BOX_ICON_ID).as(CheckboxIcon.class).setTicked(flag);
    }
}
