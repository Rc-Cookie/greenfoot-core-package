package com.github.rccookie.greenfoot.ui.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.rccookie.greenfoot.core.Color;
import com.github.rccookie.greenfoot.core.Image;
import com.github.rccookie.greenfoot.core.CoreActor;

public abstract class UIElement extends CoreActor {

    /**
     * The design used by this ui element.
     */
    private Design design = Design.DEFAULT;

    /**
     * Stores the indices in the theme of the colored elements.
     */
    private final HashMap<String, ColorMapping> colorIndices = new HashMap<String, ColorMapping>();

    /**
     * Stores subelements of this element. This could for example be a text object.
     */
    final HashSet<UIElement> subElements = new HashSet<>();

    /**
     * Flag indicating that since the last frame the image has been changed and
     * should be repainted.
     */
    private boolean imageChanged = true;




    {
        assignDefaultColorMappings();
    }




    /**
     * Returns the current design of the ui element.
     * 
     * @return The current design of the ui element
     */
    public Design getDesign() {
        return design;
    }

    /**
     * Returns the theme of the current design of the ui element.
     * 
     * @return The current theme
     */
    public Theme getTheme() {
        return getDesign().theme();
    }

    /**
     * Returns the text theme of the current design of the ui element.
     * 
     * @return The current text theme
     */
    public Theme getTextTheme() {
        return design.textTheme();
    }

    /**
     * Returns the currently mapped index in the theme/ text theme for the specified element.
     * 
     * @param elementName The name of the element
     * @return The currently mapped index in the theme/ text theme, or {@code null} if there is no index
     *         mapped for the specified element
     */
    protected Integer getColorIndex(String elementName) {
        return colorIndices.get(elementName).index;
    }

    /**
     * Returns the color currently mapped to the specified element name by the
     * index mapped and the current designs theme. Throws a NullPointerException if the
     * index for that element is not defined.
     * 
     * @param elementName The name of the element to get the color for
     * @return The color currently mapped
     */
    public Color elementColor(String elementName) {
        ColorMapping mapping = colorIndices.get(elementName);
        Objects.requireNonNull(mapping, "The element color " + elementName + " is not defined");
        return (mapping.isText ? getTextTheme() : getTheme()).get(mapping.index);
    }


    /**
     * Sets the design of the ui element to the specified one.
     * 
     * @param design The new design
     * @return This element
     */
    public UIElement setDesign(Design design) {
        Objects.requireNonNull(design, "The design must not be null");
        if(this.design == design) return this;
        this.design = design;
        imageChanged();
        return this;
    }

    /**
     * Sets the main color theme of this element.
     * 
     * @param theme The new theme
     * @return This element
     */
    public UIElement setTheme(Theme theme) {
        Objects.requireNonNull(theme, "The theme must not be null");
        if(getDesign().theme() == theme) return this;
        return setDesign(new Design(theme, getDesign().textTheme()));
    }

    /**
     * Sets the text theme to the specified one.
     * 
     * @param theme The new text theme
     * @return This element
     */
    public UIElement setTextTheme(Theme theme) {
        Objects.requireNonNull(theme, "The theme must not be null");
        if(getDesign().textTheme() == theme) return this;
        return setDesign(new Design(getDesign().theme(), theme));
    }

    /**
     * Sets the background color of this element to the given one.
     * 
     * @param color The new background color
     * @return This element
     * @throws NullPointerException If the background color was never assigned
     */
    public UIElement setBackgroundColor(Color color) {
        Objects.requireNonNull(color, "The color must not be null");
        if(elementColor("background").equals(color)) return this;
        return setTheme(getDesign().theme().modified(getColorIndex("background"), color));
    }


    /**
     * Sets the index in the theme/ text theme of the specified element
     * to the given one.
     * 
     * @param elementName The name of the element
     * @param index The new index in the theme/ text theme
     * @param isText {@code true} if the color corresponds to the index in the text theme,
     *               {@code false} otherwise
     * @return This element
     */
    protected UIElement mapColor(String elementName, int index, boolean isText) {
        Objects.requireNonNull(elementName, "The element must not be null");
        if(index < 0) throw new IllegalArgumentException("Index must be positive");
        ColorMapping newMapping = new ColorMapping(index, isText);
        if(!Objects.equals(colorIndices.put(elementName, newMapping), newMapping)) imageChanged();
        return this;
    }


    /**
     * Called once on initialization of the ui element. Has to assign all the color mappings
     * using {@link #mapColor(String, int, boolean)} so that the default color mapping
     * is achieved.
     */
    protected abstract void assignDefaultColorMappings();









    /**
     * Adds a subelement to this element. Implementations of this class should add any subelements
     * using this to allow maximum funcionality, especially if they aren't also in the world.
     * 
     * @param <E> The type of ui element to add
     * @param subElement The ui element to add
     * @return The inserted element
     */
    protected <E extends UIElement> E addSubElement(E subElement) {
        if(subElement == null) return subElement;
        if(subElement.subElements.contains(this)) throw new IllegalStateException("Trying to make two ui elements subelements of each other");
        subElements.add(subElement);
        return subElement;
    }

    /**
     * Removed the specified subelement from this element.
     * 
     * @param subElement The ui element to remove
     * @return Weather the given element was a subelement of this element
     */
    protected boolean removeSubElement(UIElement subElement) {
        if(subElement == null) return false;
        return subElements.remove(subElement);
    }

    /**
     * Returns a set containing all current subelements of this ui element.
     * 
     * @return A set containing all subelements
     */
    protected Set<UIElement> getSubElements() {
        return new HashSet<>(subElements);
    }









    /**
     * Called whenever a regeneration of the ui elements image is neccecary. This
     * method should then recreate the image(s) for the element from scratch.
     * <p>Subelements do <b>not</b> have to be updated using this method, if they have
     * been added using {@link #addSubElement(UIElement)}, and will automatically be
     * updated.
     */
    protected abstract void regenerateImages();

    /**
     * Informs this ui element that some component relevalnt for its image has changed
     * and that should regenerate its image. This could for example be a change in
     * the design, a different text title or a resizing.
     */
    protected void imageChanged() {
        imageChanged = true;
        for(UIElement element : subElements) element.imageChanged();
    }

    @Override
    public Image getImage() {
        if(imageChanged) {
            imageChanged = false;
            regenerateImages();
            imageChanged = false; // Some regeneration algorithm may reactivate this
        }
        return super.getImage();
    }
}
