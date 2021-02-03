package com.github.rccookie.greenfoot.widgets.prefabs;

import com.github.rccookie.greenfoot.widgets.Text;

import greenfoot.Greenfoot;

import java.util.Objects;

import com.github.rccookie.greenfoot.widgets.Align;
import com.github.rccookie.greenfoot.widgets.Button;
import com.github.rccookie.greenfoot.widgets.Color;
import com.github.rccookie.greenfoot.widgets.Dimension;

public class TextInputButton extends Button {

    protected static final String TEXT = "button_text";
    static final greenfoot.Color COLOR_SELECTED = Color.DARK_GRAY, COLOR_NOT_SELECTED = new greenfoot.Color(160, 160, 160);

    private String title;
    private boolean selected = false;

    public TextInputButton(String title) {
        this(title, "Enter the value for '" + title + "':");
    }

    public TextInputButton(String title, String prompt) {
        super(
            self -> self.as(TextInputButton.class).enterText(Greenfoot.ask(prompt)),
            new Color(new greenfoot.Color(242, 242, 242)),
            new Dimension(
                -4,
                0,
                Align.left(
                    new Text(
                        title != null ? title : "",
                        COLOR_NOT_SELECTED
                    ).setId(TEXT)
                )
            )
        );
        setTitle(title);
    }

    public TextInputButton clear() {
        return enterText(null);
    }

    public TextInputButton enterText(String text) {
        if(selected && Objects.equals(text, getEnteredText())) return this;
        if(text == null) text = "";
        selected = text.length() != 0;
        find(TEXT).as(Text.class).setTitle(selected ? text : title);
        find(TEXT).as(Text.class).setColor(selected ? COLOR_SELECTED : COLOR_NOT_SELECTED);
        return this;
    }

    public String getEnteredText() {
        return selected ? find(TEXT).as(Text.class).getTitle() : null;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getTitle() {
        return title;
    }

    public TextInputButton setTitle(String title) {
        this.title = title != null ? title : "";
        if(!selected) modify();
        return this;
    }
}
