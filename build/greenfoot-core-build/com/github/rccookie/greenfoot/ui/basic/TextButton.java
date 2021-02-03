package com.github.rccookie.greenfoot.ui.basic;

import java.util.Objects;
import java.util.function.Consumer;

import greenfoot.GreenfootImage;
import greenfoot.World;
import com.github.rccookie.common.util.ClassTag;
import com.github.rccookie.greenfoot.ui.util.Design;
import com.github.rccookie.greenfoot.ui.util.Interactable;

/**
 * A button that shows some text.
 * 
 * @author RcCookie
 * @version 3.0
 */
public class TextButton extends Interactable {

    protected static final int BORDER_X = 5, BORDER_Y = 1;
    protected static final float HEIGHT_WIDTH_FACTOR = 3.5f;

    protected static final int DEFAULT_MIN_X = 6;
    protected static final int DEFAULT_MIN_Y = 6;
    protected static final int DEFAULT_MAX_X = -1;
    protected static final int DEFAULT_MAX_Y = -1;
    protected static final boolean DEFAULT_DRAW_OUTLINE = false;
    protected static final int DEFAULT_OUTLINE_WIDTH = 2;
    protected static final boolean DEFAULT_USE_BIG_BORDER = true;
    protected static final Design DEFAULT_DESIGN = Design.getDefaultDesign();

    static {
        ClassTag.tag(TextButton.class, "ui");
    }

    // ------------------------------------------
    // Button properties
    // ------------------------------------------


    /**
     * The text object containing the title and style of any button text.
     */
    private Text text;

    /**
     * The minimum width of the button.
     */
    private int minWidth = DEFAULT_MIN_X;

    /**
     * The minimum height of the button.
     */
    private int minHeight = DEFAULT_MIN_Y;

    /**
     * The maximum width of the button.
     */
    private int maxWidth = DEFAULT_MAX_X;

    /**
     * The maximum height of the button.
     */
    private int maxHeight = DEFAULT_MAX_Y;

    /**
     * Flag indicating whether to draw an outline or not.
     */
    private boolean drawOutline = DEFAULT_DRAW_OUTLINE;

    /**
     * The width of the outline, if one is drawn.
     */
    private int outlineWidth = DEFAULT_OUTLINE_WIDTH;

    /**
     * Flag indicating whether to use a big (normal) border around the text or not.
     */
    private boolean useBigBorder = DEFAULT_USE_BIG_BORDER;







    /**
     * Used to automatically update the appearance of the button when
     * the text is changed.
     */
    private final Runnable textUpdateAction = () -> imageChanged();






    //--------------------------------------------------
    // Constructors
    //--------------------------------------------------



    public TextButton(String title) {
        this(new Text(title));
    }

    public TextButton(Text text) {
        setText(text != null ? text : new Text());
        setDesign(this.text.getDesign());
    }









    /**
     * Creates the image, hoveredImage and clickedImage for the button using
     * the latest settings and aplies the matching image to the button.
     */
    @Override
    protected GreenfootImage createMainImage() {

        if(text == null || getDesign() == null) return null;


        // 1.: Calculate planned size based on font size and bigBorder configuration

        int plannedHeight = text.getImage().getHeight();
        if(useBigBorder) plannedHeight += text.getFontSize() / 2;

        int plannedWidth = (int)(plannedHeight * HEIGHT_WIDTH_FACTOR) + 2 * BORDER_X;

        plannedHeight += 2 * BORDER_Y;


        // 2.: Compare planned dimensions with allowed ones and maybe with actual text size

        final int width, height;

        // Minimum requirements are always specified

        if(maxWidth > 0) { // Maximum width is specified, should be text width or bigger
            int textWidth =  text.getImage().getWidth() + 2 * BORDER_X;
            if(textWidth > maxWidth) width = maxWidth;
            else if(textWidth < minWidth) width = minWidth;
            else width = Math.min(textWidth, Math.max(plannedWidth, maxWidth));
        }
        else { // Maximum width is not specified, should be planned width
            if(plannedWidth < minWidth) width = minWidth;
            else width = plannedWidth;
        }

        if(maxHeight > 0) { // Maximum height is specified, should be in allowed range
            if(plannedHeight > maxHeight) height = maxHeight;
            else if(plannedHeight < minHeight) height = minHeight;
            else height = plannedHeight;
        }
        else { // Maximum height is not specified, should be planned height
            if(plannedHeight < minHeight) height = minHeight;
            else height = plannedHeight;
        }


        // 3.: Create actual image instance

        GreenfootImage image = new GreenfootImage(width, height);


        // 4.: Fill background with theme main color

        image.setColor(elementColor("background"));
        image.fill();


        // 5.: Draw text onto background (text has no background no more, no problem with transparency no more)

        image.drawImage(
            text.getImage(),
            (width - text.getImage().getWidth()) / 2,
            (height - text.getImage().getHeight()) / 2
        );


        // 6.: Draw outline theme second color if necessary

        if(drawOutline) {
            image.setColor(elementColor("outline"));
            for(int i=0; i<outlineWidth; i++)
                image.drawRect(i, i, width - 2 * i - 1, height - 2 * i - 1);
        }


        // 7.: Return image

        return image;
    }

    @Override
    protected void assignDefaultColorMappings() {
        mapColor("background", 0, false);
        mapColor("outline", 0, true);
    }






    /**
     * Returns the title written onto the button. This is identical to
     * the result of calling {@code getText().getContent()}.
     * 
     * @return The buttons title
     */
    public String getTitle(){
        return text.getContent();
    }

    /**
     * Returns the text object used in this button
     * 
     * @return The text object of this button
     */
    public Text getText(){
        return text;
    }

    /**
     * Returns the current minimum width of this button.
     * 
     * @return The minimum width of this button
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * Returns the current minimum height of this button.
     * 
     * @return The minimum height of this button
     */
    public int getMinHeight() {
        return minHeight;
    }

    /**
     * Returns the current maximum width of this button.
     * 
     * @return The maximum width of this button
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Returns the current maximum height of this button.
     * 
     * @return The maximum height of this button
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * Indicates weather there is currently being an outline drawn or not.
     * 
     * @return Whether there is an outline drawn or not
     */
    public boolean isOutlineDrawn() {
        return drawOutline;
    }

    /**
     * Returns the currently selected outline width.
     * 
     * @return The outline width
     */
    public int getOutlineWidth() {
        return outlineWidth;
    }

    /**
     * Inticates weather the button uses a big (normal) border around the text or not.
     * 
     * @return Weather the button uses a big (normal) border around the text or not
     */
    public boolean usesBigBorder() {
        return useBigBorder;
    }





    /**
     * Override the title of the button.
     * 
     * @param title The new title
     * @return This button
     */
    public TextButton setTitle(String title){
        text.setContent(title);
        imageChanged();
        return this;
    }

    /**
     * Sets the text object the button is based on to the given one.
     * 
     * @param text The text object to set to
     * @return This button
     */
    public TextButton setText(Text text) {
        Objects.requireNonNull(text, "The text must not be null");
        if(this.text == text) return this;
        removeSubElement(this.text);
        addSubElement(text);
        if(this.text != null) this.text.removeUpdateAction(textUpdateAction);
        text.addUpdateAction(textUpdateAction);
        this.text = text;
        imageChanged();
        return this;
    }

    /**
     * Sets the minimum width for the button. The minimum width must be greater than 0.
     * 
     * @param minWidth The new minimum width
     * @return This button
     */
    public TextButton setMinWidth(int minWidth) {
        if(minWidth <= 0) throw new IllegalArgumentException("The minimun width must be greater than 0");
        if(this.minWidth == minWidth) return this;
        this.minWidth = minWidth;
        imageChanged();
        return this;
    }

    /**
     * Sets the minimum height for this button. The minimum height must be greater than 0.
     * 
     * @param minHeight The new minimum height
     * @return This button
     */
    public TextButton setMinHeight(int minHeight) {
        if(minHeight <= 0) throw new IllegalArgumentException("The minimun height must be greater than 0");
        if(this.minHeight == minHeight) return this;
        this.minHeight = minHeight;
        imageChanged();
        return this;
    }

    /**
     * Sets the maximum width of this button. If you want an unlimited width, set this to {@link Integer#MAX_VALUE}.
     * Negative values will disable the maximum width but the button's width will be limited to a generated
     * good-looking width.
     * 
     * @param maxWidth The new maximum width
     * @return This button
     */
    public TextButton setMaxWidth(int maxWidth) {
        if(this.maxWidth == maxWidth) return this;
        this.maxWidth = maxWidth;
        imageChanged();
        return this;
    }

    /**
     * Sets the maximum width of this button. If you want an unlimited height, set this to {@link Integer#MAX_VALUE}
     * or a negative value.
     * 
     * @param maxHeight The new maximum height
     * @return This button
     */
    public TextButton setMaxHeight(int maxHeight) {
        if(this.maxHeight == maxHeight) return this;
        this.maxHeight = maxHeight;
        imageChanged();
        return this;
    }

    /**
     * Sets weather an outline should be drawn or not.
     * 
     * @param flag Weather an outline should be drawn or not
     * @return This button
     */
    public TextButton setDrawOutline(boolean flag) {
        if(drawOutline == flag) return this;
        drawOutline = flag;
        imageChanged();
        return this;
    }

    /**
     * Sets the width of the outline to the specified value. The width must be greater than 0. If the outline is
     * not enabled this will have no visible effect.
     * 
     * @param outlineWidth The new with of the outline
     * @return This button
     */
    public TextButton setOutlineWidth(int outlineWidth) {
        if(outlineWidth <= 0) throw new IllegalArgumentException("The outline width must be greater than 0. If you want to disable the outline use setDrawOutline");
        if(this.outlineWidth == outlineWidth) return this;
        this.outlineWidth = outlineWidth;
        imageChanged();
        return this;
    }

    /**
     * Sets weather a big (normal) border around the text should be used.
     * 
     * @param flag Weather a big (normal) border should be used
     * @return This button
     */
    public TextButton setUseBigBorder(boolean flag) {
        if(useBigBorder == flag) return this;
        useBigBorder = flag;
        imageChanged();
        return this;
    }










    /**
     * Adds an action that will be executed whenever the button was clicked.
     * <p><b>Example:<b>
     * <p>{@code Button b = new Button("Hello World!");}
     * <p>{@code b.addClickAction(() -> System.out.println("Hello!"));}
     * 
     * @param mouse The action to add
     */
    @Override
    public TextButton addClickAction(Runnable mouse) {
        return (TextButton)super.addClickAction(mouse);
    }


    /**
     * Removes the given action from those that are ran whenever the button is clicked.
     * 
     * @param action The action to remove
     */
    public TextButton removeClickAction(Runnable action) {
        return (TextButton)super.removeClickAction(action);
    }

    @Override
    public TextButton addPressAction(Runnable mouse) {
        return (TextButton)super.addPressAction(mouse);
    }

    @Override
    public TextButton addReleaseAction(Runnable mouse) {
        return (TextButton)super.addReleaseAction(mouse);
    }

    @Override
    public TextButton addAddedAction(Consumer<World> world) {
        return (TextButton)super.addAddedAction(world);
    }

    @Override
    public TextButton removePressAction(Runnable action) {
        return (TextButton)super.removePressAction(action);
    }

    @Override
    public TextButton removeReleaseAction(Runnable action) {
        return (TextButton)super.removeReleaseAction(action);
    }
    @Override
    public TextButton removeAddedAction(Consumer<World> action) {
        return (TextButton)super.removeAddedAction(action);
    }
}