package com.github.rccookie.greenfoot.ui.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import greenfoot.Color;
import greenfoot.GreenfootImage;
import com.github.rccookie.common.util.ClassTag;
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
    protected static final String DEFAULT_CONTENT = "";
    protected static final Design DEFAULT_DESIGN = Design.getDefaultDesign();

    static {
        ClassTag.tag(Text.class, "ui");
    }


    /**
     * The text of the text.
     */
    private String content = DEFAULT_CONTENT;

    /**
     * The fontSize of the dext drawn.
     */
    private int fontSize = DEFAULT_FONT_SIZE;


    private final List<Runnable> updateActions = new ArrayList<>();


    /**
     * Constructs an empty text with default settings.
     */
    public Text() {
        regenerateImages();
    }

    /**
     * Constructs a new text with the given content.
     * 
     * @param content The text of the text
     */
    public Text(String content) {
        setContent(content);
    }

    /**
     * Constructs a new text with the given content written in the given
     * font size.
     * 
     * @param content The text of the text
     * @param fontSize The font size of the text
     */
    public Text(String content, int fontSize) {
        this.content = content;
        setFontSize(fontSize);
    }

    /**
     * Constructs a new text with the specified content and text color.
     * 
     * @param content The text of the text
     * @param textColor The text color
     */
    public Text(String content, Color textColor) {
        this.content = content;
        setTextColor(textColor);
    }

    /**
     * Constructs a new Text with the specified content and text theme.
     * 
     * @param content The text of the text
     * @param theme The theme of the text
     */
    public Text(String content, Theme theme) {
        this.content = content;
        setTheme(theme);
    }

    /**
     * Constructs a new text with the given content and design-
     * 
     * @param content The text of the text
     * @param design The design of the text
     */
    public Text(String content, Design design) {
        this.content = content;
        setDesign(design);
    }

    /**
     * Constructs a new text with the given content written in the given
     * color and font size. The background will be filled with the given
     * backgound color.
     * 
     * @param content The text of the text
     * @param fontSize The font size of the text
     * @param design The design of the text
     */
    public Text(String content, int fontSize, Design design) {
        this.content = content;
        this.fontSize = fontSize;
        setDesign(design);
    }

    /**
     * Creates a new Text of the given one. The design instance will not be
     * cloned.
     * 
     * @param copy The text to create a copy of
     */
    public Text(Text copy) {
        this(copy.getContent(), copy.getFontSize(), copy.getDesign());
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
        if(content == null || content.equals("")) {
            setImage(new GreenfootImage(1, 1));
        }
        else setImage(new GreenfootImage(
            content,
            fontSize,
            elementColor("text"),
            Theme.C_TRANSPARENT));

        for (Runnable action : updateActions) action.run();
    }


    /**
     * Sets the text of the text to the given string.
     * 
     * @param content The new text
     */
    public Text setContent(String content) {
        this.content = content;
        regenerateImages();
        return this;
    }

    /**
     * Sets the font size of the text (also of the already written stuff) to
     * the given one.
     * 
     * @param fontSize The new font size
     */
    public Text setFontSize(int fontSize) {
        this.fontSize = fontSize;
        regenerateImages();
        return this;
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
    public Text setTextColor(Color color) {
        return setTheme(getTheme().modified(0, color));
    }




    /**
     * Returns the text of the text.
     * 
     * @return The text of the text
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the font size of the text.
     * 
     * @return The font size of the text
     */
    public int getFontSize() {
        return fontSize;
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



    /**
     * Returns the design used for any text by default.
     * 
     * @return The default design
     */
    public static final Design defaultDesign() {
        return DEFAULT_DESIGN;
    }

    /**
     * Returns the text theme used for any text by default.
     * 
     * @return The default text theme
     */
    public static final Theme defaultTheme() {
        return DEFAULT_DESIGN.textTheme();
    }

    @Override
    protected void assignDefaultColorMappings() {
        mapColor("text", 0, true);
    }
}
