package com.github.rccookie.greenfoot.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.rccookie.greenfoot.core.FontStyle;
import com.github.rccookie.greenfoot.core.Image;
import com.github.rccookie.greenfoot.core.Color;
import com.github.rccookie.greenfoot.ui.util.Design;
import com.github.rccookie.greenfoot.ui.util.Theme;
import com.github.rccookie.greenfoot.ui.util.UIElement;

/**
 * The text class is used to store and display some text and to control
 * text elements in other ui elements.
 * 
 * @author RcCookie
 * @version 2.0
 */
public class Text extends UIElement implements Cloneable {

    protected static final int DEFAULT_FONT_SIZE = 20;
    protected static final FontStyle DEFAULT_FONT = FontStyle.modern(DEFAULT_FONT_SIZE);
    protected static final String DEFAULT_CONTENT = "";

    private static final String TEXT = "text";


    /**
     * The text of the text.
     */
    private String title = DEFAULT_CONTENT;

    /**
     * The font of the text drawn.
     */
    private FontStyle font = DEFAULT_FONT;



    private final List<Runnable> updateActions = new ArrayList<>();



    /**
     * Constructs an empty text.
     */
    public Text() { }

    /**
     * Constructs a new text with the given title.
     * 
     * @param title The text of the text
     */
    public Text(String title) {
        setTitle(title);
    }

    /**
     * Creates a new Text of the given one. The design instance will not be
     * cloned.
     * 
     * @param copy The text to create a copy of
     */
    public Text(Text copy) {
        this(copy.getTitle());
        setFont(copy.getFont());
        setDesign(copy.getDesign());
    }



    @Override
    public Text clone() {
        return new Text(this);
    }



    /**
     * Updates the image of the text according to the current settings.
     */
    @Override
    protected void regenerateImages() {
        if(title == null || title.equals("")) setImage(new Image(1, 1));
        else {
            //setImage(Image.text(title, elementColor("text"), font));
            Image image = new Image(Math.max(1, font.getWidth(title)), Math.max(1, font.getHeight(title)));
            //image.fill(Color.LIGHT_GRAY); // For debugging purposes
            image.drawString(title, 0, (int)(font.getSize() * 0.75), elementColor(TEXT), font);
            setImage(image);
        }

        for (Runnable action : updateActions) action.run();
    }



    /**
     * Sets the text of the text to the given string.
     * 
     * @param title The new text
     */
    public Text setTitle(String title) {
        this.title = title;
        imageChanged();
        return this;
    }

    /**
     * Sets the font of this text.
     * 
     * @param font The new font
     * @return This text
     */
    public Text setFont(FontStyle font) {
        this.font = font;
        imageChanged();
        return this;
    }

    /**
     * Sets the font size of the text (also of the already written stuff) to
     * the given one.
     * 
     * @param fontSize The new font size
     */
    public Text setSize(int fontSize) {
        return setFont(font.deriveFont(fontSize));
    }

    @Override
    public Text setDesign(Design design) {
        super.setDesign(design);
        return this;
    }

    /**
     * Sets the text theme to the given one.
     */
    public Text setTheme(Theme theme) {
        Objects.requireNonNull(theme, "The theme must not be null");
        return setDesign(new Design(getDesign().theme(), theme));
    }

    /**
     * Sets a new, modified version of the current design with the given
     * main text color.
     */
    public Text setColor(Color color) {
        return setTheme(getTheme().modified(0, color));
    }



    /**
     * Returns the title of the text.
     * 
     * @return The text of the text
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the font size of the text.
     * 
     * @return The font size of the text
     */
    public FontStyle getFont() {
        return font;
    }



    /**
     * Adds an action that will be executed whenever the image of the text
     * gets updated (usually meaning it changed).
     * 
     * @param nothing The action to add
     */
    public Text addUpdateAction(Runnable action) {
        if(action == null) return this;
        updateActions.add(action);
        return this;
    }

    /**
     * Removes the given action from being excuted whenever the image
     * of the text gets updated.
     * 
     * @param action The action to remove
     */
    public void removeUpdateAction(Runnable action) {
        updateActions.remove(action);
    }



    @Override
    protected void assignDefaultColorMappings() {
        mapColor(TEXT, 0, true);
    }
}
