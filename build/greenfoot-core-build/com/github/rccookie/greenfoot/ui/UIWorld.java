package com.github.rccookie.greenfoot.ui;

import com.github.rccookie.greenfoot.ui.util.ColorMapping;
import com.github.rccookie.greenfoot.ui.util.Design;
import com.github.rccookie.greenfoot.ui.util.Theme;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

import com.github.rccookie.greenfoot.core.CoreWorld;

public abstract class UIWorld extends CoreWorld {

    private static Class<?>[] UI_CLASS_PAINT_ORDER = {
        FpsDisplay.class,
        Fade.class,
        UINavigator.class,
        DropDownMenu.MenuButton.class,
        DropDownMenu.Backpanel.class,
        TextButton.class,
        Slider.Handle.class,
        Slider.class,
        Text.class,
        UIPanel.class
    };



    private UINavigator buttonNavi;
    private Design design = Design.DEFAULT;

    private boolean colorBackground = false;
    private boolean backgroundChanged = true;

    /**
     * Stores the indices in the theme of the colored elements.
     */
    private HashMap<String, ColorMapping> colorIndices = new HashMap<String, ColorMapping>();




    private Class<?>[] customPaintOrder;

    public UIWorld(int width, int height) {
        this(width, height, 1);
        bluej.Boot
    }

    public UIWorld(int x, int y, int cellSize){
        this(x, y, cellSize, false);
    }

    public UIWorld(int x, int y, int cellSize, boolean bounded){
        super(x, y, cellSize, bounded);
        assignDefaultColorMappings();

        colorBackground(Color.WHITE);

        setPaintOrder();
    }



    @Override
    public void addObject(Actor object, int x, int y) {
        setPaintOrder(customPaintOrder);
        super.addObject(object, x, y);

        // Create button navi if there is no and a button but not a fps display is added
        if(buttonNavi == null && object instanceof TextButton && !(object instanceof FpsDisplay)) buttonNavi = new UINavigator(this);
    }


    /**
     * Returns one object of the specified class, or {@code null} if there are none.
     * 
     * @param <A> The type of actor
     * @param clazz The class of that tyoe
     * @return One object of the specified class, or {@code null}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <A extends Actor> A getObject(Class<? extends A> clazz) {
        List<A> objects = getObjects((Class)clazz);
        if(objects.size() == 0) return null;
        return objects.get(0);
    }

    /**
     * Defines the order of painting. UI elements are automatically at the top. To override their position,
     * specificly set them to the position in the ordering you want them to be in.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void setPaintOrder(Class... classes) {
        customPaintOrder = classes;
        super.setPaintOrder(combineClasses(UI_CLASS_PAINT_ORDER, classes != null ? classes : new Class[0]));
    }

    private Class<?>[] combineClasses(Class<?>[] a, Class<?>[] b) {
        int twice = 0;
        aLoop: for(Class<?> clsA : a) {
            for(Class<?> clsB : b) {
                if(clsA == clsB) {
                    twice++;
                    continue aLoop;
                }
            }
        }
        Class<?>[] c = new Class[a.length + b.length - twice];
        twice = 0;
        aLoop: for(int i=0; i<a.length; i++) {
            for(Class<?> clsB : b) {
                if(a[i] == clsB) {
                    twice++;
                    continue aLoop;
                }
            }
            c[i - twice] = a[i];
        }
        for(int i=0; i<b.length; i++) {
            c[a.length - twice + i] = b[i];
        }
        return c;
    }

    public void addFps(){
        if(getObjects(FpsDisplay.class).size() == 0) addObject(new FpsDisplay(), 42, 13);
    }

    public void colorBackground(Color color) {
        getBackground().setColor(color);
        getBackground().fill();
    }

    public UINavigator uiNavi() {
        return buttonNavi == null ? buttonNavi = new UINavigator(this) : buttonNavi;
    }




    @Override
    public GreenfootImage getBackground() {
        if(backgroundChanged) {
            backgroundChanged = false;
            regenerateBackground();
        }
        return super.getBackground();
    }


    protected void regenerateBackground() {
        GreenfootImage background = new GreenfootImage(getWidth(), getHeight());
        if(colorBackground) {
            background.setColor(elementColor("plain-world-background"));
            background.fill();
        }
        setBackground(background);
    }


    public void backgroundChanged() {
        //backgroundChanged = true;
        regenerateBackground();
    }


    public void colorBackground(boolean flag) {
        if(flag == colorBackground) return;
        colorBackground = flag;
        backgroundChanged();
    }

    protected void mapBackgroundColor(int index, boolean isText) {
        mapColor("plain-world-background", index, isText);
    }

    /**
     * Called once on initialization of the ui element. Has to assign all the color mappings
     * using {@link #mapColor(String, int, boolean)} so that the default color mapping
     * is achieved.
     */
    protected void assignDefaultColorMappings() {
        mapBackgroundColor(1, false);
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
    public UIWorld setDesign(Design design) {
        Objects.requireNonNull(design, "The design must not be null");
        if(this.design == design) return this;
        this.design = design;
        backgroundChanged();
        return this;
    }

    /**
     * Sets the main color theme of this element.
     * 
     * @param theme The new theme
     * @return This element
     */
    public UIWorld setTheme(Theme theme) {
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
    public UIWorld setTextTheme(Theme theme) {
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
    public UIWorld setBackgroundColor(Color color) {
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
    protected UIWorld mapColor(String elementName, int index, boolean isText) {
        Objects.requireNonNull(elementName, "The element must not be null");
        if(index < 0) throw new IllegalArgumentException("Index must be positive");
        ColorMapping newMapping = new ColorMapping(index, isText);
        if(!Objects.equals(colorIndices.put(elementName, newMapping), newMapping)) backgroundChanged();
        return this;
    }
}
