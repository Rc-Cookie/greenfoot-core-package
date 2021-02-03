package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

public class Button extends Container {

    /**
     * The color filter if the object is clicked on.
     */
    protected static final Color CLICK_COLOR_CORRECTION = new Color(0, 0, 0, 60);

    /**
     * The factor to scale the image by if the object is clicked on.
     */
    protected static final double CLICK_SCALE = 0.98;

    /**
     * The color of the outline if the object is hovered on.
     */
    protected static final Color HOVER_OUTLINE_COLOR = new Color(128, 128, 128);

    /**
     * The color filter when the mouse touches the button if the hover outline is disabled.
     */
    protected static final Color ALT_HOVER_COLOR_CORRECTION = new Color(255, 255, 255, 30);

    /**
     * The color filter when the button is disabled.
     */
    protected static final Color DISABLED_COLOR_CORRECTION = new Color(255, 255, 255, 50);



    private static final int TOUCHED = 0, PRESSED = 1, DISABLED = -1;

    private boolean touched = false, pressed = false;
    private boolean enabled = true;
    private boolean animated = true;
    private final List<Consumer<Button>> onClick = new ArrayList<>();
    private final List<Consumer<Button>> onPress = new ArrayList<>();
    private final List<Consumer<Button>> onRelease = new ArrayList<>();

    private boolean useHoverOutline = true;
    private boolean useAltHoverColorIfNeeded = true;



    public Button(Runnable onClick, Widget... children) {
        this(onClick != null ? b -> onClick.run() : null, children);
    }

    public Button(Consumer<Button> onClick, Widget... children) {
        super(children);
        if(onClick != null) addOnClick(onClick);
        else setEnabled(false);
        setDynamic(false);
    }

    @Override
    protected GreenfootImage createImage(Size maxSize) {
        return null;
    }

    @Override
    GreenfootImage getCachedRender(GreenfootImage lastRender) {
        if(!isAnimated()) return lastRender;
        if(!isEnabled()) return getStateCache(DISABLED, () -> renderDisabled(lastRender));
        if(!touched) return lastRender;
        if(!pressed) return getStateCache(TOUCHED, () -> renderTouched(lastRender));
        return getStateCache(PRESSED, () -> renderPressed(lastRender));
    }

    protected GreenfootImage renderDisabled(GreenfootImage base) {
        if(base == null) return null;
        GreenfootImage image = new GreenfootImage(base);
        image.setColor(DISABLED_COLOR_CORRECTION);
        image.fill();
        return image;
    }

    protected GreenfootImage renderTouched(GreenfootImage base) {
        if(base == null) return null;
        GreenfootImage image = new GreenfootImage(base);
        if(usesHoverOutline()) {
            image.setColor(HOVER_OUTLINE_COLOR);
            image.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
            image.drawRect(1, 1, image.getWidth() - 3, image.getHeight() - 3);
        }
        else if(usesAltHoverColorIfNeeded()) {
            image.setColor(ALT_HOVER_COLOR_CORRECTION);
            image.fill();
        }
        return image;
    }

    protected GreenfootImage renderPressed(GreenfootImage base) {
        if(base == null) return null;
        GreenfootImage image = new GreenfootImage(base);
        image.setColor(CLICK_COLOR_CORRECTION);
        image.scale((int)(image.getWidth() * CLICK_SCALE), (int)(image.getHeight() * CLICK_SCALE));
        image.fill();
        GreenfootImage withBorder = new GreenfootImage(base.getWidth(), base.getHeight());
        withBorder.drawImage(image, base.getWidth() / 2 - image.getWidth() / 2, base.getHeight() / 2 - image.getHeight() / 2);
        return withBorder;
    }

    @Override
    protected void update(Size maxSize) {
        if(enabled) {
            WidgetHolder holder = getHolder();

            boolean newTouched = mouseTouching();
            if(newTouched != touched) modifyState(() -> touched = newTouched);
    
            if(touched && Greenfoot.mousePressed(holder)) onPress(true);
            else if(pressed && Greenfoot.mouseClicked(null)) onRelease(true);
        }
    }

    private void onPress(boolean real) {
        pressed = true;
        if(real) modifyState();
        for(Consumer<Button> method : onPress) method.accept(this);
    }

    private void onRelease(boolean real) { // not real means emulated, no need to rerender then
        pressed = false; // Not neccecarily a graphical difference if not touched
        for(Consumer<Button> method : onRelease) method.accept(this);
        if(touched) {
            if(real) modifyState(); // If touched then the release made a difference
            onClick();
        }
    }

    private void onClick() {
        for(Consumer<Button> method : onClick) method.accept(this);
    }



    /**
     * Emulates a click on this button. Internally this will be thread like
     * a 'real' click on the button, including internal touching states, with
     * the only exception that the click will not be rendered.
     */
    public void click() {
        if(enabled) {
            boolean realTouched = touched;
            touched = true;
            onPress(false);
            onRelease(false);
            touched = realTouched;
        }
    }



    public Button addOnClick(Runnable method) {
        if(method == null) return this;
        return addOnClick(b -> method.run());
    }

    public Button addOnClick(Consumer<Button> method) {
        if(method == null) return this;
        onClick.add(method);
        return this;
    }

    public boolean removeOnClick(Consumer<Button> method) {
        return onClick.remove(method);
    }

    public Button addOnPress(Runnable method) {
        if(method == null) return this;
        return addOnPress(b -> method.run());
    }

    public Button addOnPress(Consumer<Button> method) {
        if(method == null) return this;
        onPress.add(method);
        return this;
    }

    public boolean removeOnPress(Consumer<Button> method) {
        return onPress.remove(method);
    }

    public Button addOnRelease(Runnable method) {
        if(method == null) return this;
        return addOnRelease(b -> method.run());
    }

    public Button addOnRelease(Consumer<Button> method) {
        if(method == null) return this;
        onRelease.add(method);
        return this;
    }

    public boolean removeOnRelease(Consumer<Button> method) {
        return onRelease.remove(method);
    }



    public boolean usesHoverOutline() {
        return useHoverOutline;
    }

    public Button useHoverOutline(boolean flag) {
        if(usesHoverOutline() == flag) return this;
        modify(() -> useHoverOutline = flag);
        return this;
    }

    public boolean usesAltHoverColorIfNeeded() {
        return useAltHoverColorIfNeeded;
    }

    public Button useAltHoverColorIfNeeded(boolean flag) {
        if(useAltHoverColorIfNeeded == flag) return this;
        useAltHoverColorIfNeeded = flag;
        if(!usesHoverOutline()) modify();
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Button setEnabled(boolean flag) {
        if(enabled == flag) return this;
        modifyState(() -> enabled = flag);
        return this;
    }

    public boolean isAnimated() {
        return animated;
    }

    public Button setAnimated(boolean flag) {
        if(animated == flag) return this;
        modifyState(() -> animated = flag);
        return this;
    }
}
