package com.github.rccookie.greenfoot.ui;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.Comparator;
import java.util.List;

import greenfoot.Color;
import greenfoot.GreenfootImage;
import greenfoot.World;

import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.greenfoot.event.Input;
import com.github.rccookie.greenfoot.event.KeyListener;
import com.github.rccookie.greenfoot.event.MouseListener;
import com.github.rccookie.greenfoot.event.NavigatonListener;
import com.github.rccookie.greenfoot.core.CoreActor;
import com.github.rccookie.greenfoot.ui.util.Interactable;
import com.github.rccookie.greenfoot.ui.util.Theme;

public class UINavigator extends CoreActor {

    public static final int BORDER = 3;


    private Interactable focused = null;
    private final World world;

    private final Theme theme = new Theme(Color.BLACK, Color.WHITE);
    private final NavigatonListener navi;
    private final KeyListener spaceListener;
    private final KeyListener tabListener;
    private final KeyListener escListener;
    private final MouseListener mouseListener;


    public UINavigator(World world) {
        Objects.requireNonNull(world);
        this.world = world;

        world.addObject(this, 0, 0);
        
        navi = new NavigatonListener();
        navi.addNaviListener(k -> {
            switch (k) {
                case "up":
                    focus(getAbove());
                    break;
                case "down":
                    focus(getBelow());
                    break;
                case "left":
                    focus(getLeft());
                    break;
                case "right":
                    focus(getRight());
                    break;
            }
        });

        spaceListener = new KeyListener("space");
        spaceListener.addListener(() -> focused.click());

        tabListener = new KeyListener("tab");
        tabListener.addListener(() -> {
            focused = Input.keyState("shift") ? getLast() : getNext();
            updateImage();
        });

        escListener = new KeyListener("escape");
        escListener.addListener(() -> focus(null));

        mouseListener = new MouseListener();
        mouseListener.addListener(m -> focus(null));

        updateImage();
    }

    public UINavigator(Interactable focused) {
        this(focused.getWorld());
        this.focused = focused;
        setLocation(focused);
        updateImage();
    }



    @Override
    public void update() {
        tabListener.update();
        if(focused != null) escListener.update();
        if(focused != null) mouseListener.update();
        if(focused != null) {
            navi.update();
            spaceListener.update();
            setLocation(focused.getLocation());
            setRotation(focused.getAngle());
        }
    }



    private Interactable getAbove() {
        List<Interactable> buttons = focused != null ? focused.getFocusable() : world.getObjects(Interactable.class);
        if(buttons.size() == 0) return null;
        removeIf(buttons, b -> {
            if(b == focused) return true;
            Vector2D between = Vector2D.between(getLocation(), b.getLocation());
            double angle = between.angle();
            return angle < -135 || angle > -45; 
        });
        if(buttons.size() == 0) return focused;
        buttons.sort(getComparator());
        return buttons.get(0);
    }

    private Interactable getBelow() {
        List<Interactable> buttons = focused != null ? focused.getFocusable() : world.getObjects(Interactable.class);
        if(buttons.size() == 0) return null;
        removeIf(buttons, b -> {
            if(b == focused) return true;
            Vector2D between = Vector2D.between(getLocation(), b.getLocation());
            double angle = between.angle();
            return angle < 45 || angle > 135; 
        });
        if(buttons.size() == 0) return focused;
        buttons.sort(getComparator());
        return buttons.get(0);
    }

    private Interactable getLeft() {
        List<Interactable> buttons = focused != null ? focused.getFocusable() : world.getObjects(Interactable.class);
        if(buttons.size() == 0) return null;
        removeIf(buttons, b -> {
            if(b == focused) return true;
            Vector2D between = Vector2D.between(getLocation(), b.getLocation());
            double angle = between.angle();
            return !(angle <= -135 || angle > 135); 
        });
        if(buttons.size() == 0) return focused;
        buttons.sort(getComparator());
        return buttons.get(0);
    }

    private Interactable getRight() {
        List<Interactable> buttons = focused != null ? focused.getFocusable() : world.getObjects(Interactable.class);
        if(buttons.size() == 0) return null;
        removeIf(buttons, b -> {
            if(b == focused) return true;
            Vector2D between = Vector2D.between(getLocation(), b.getLocation());
            double angle = between.angle();
            return angle < -45 || angle > 45;
        });
        if(buttons.size() == 0) return focused;
        buttons.sort(getComparator());
        return buttons.get(0);
    }

    private Interactable getNext() {
        List<Interactable> buttons = focused != null ? focused.getFocusable() : world.getObjects(Interactable.class);
        if(buttons.size() == 0) return null;
        try{
            return buttons.get(buttons.indexOf(focused) + 1);
        } catch(Exception e) { return buttons.get(0); }
    }

    private Interactable getLast() {
        List<Interactable> buttons = focused != null ? focused.getFocusable() : world.getObjects(Interactable.class);
        if(buttons.size() == 0) return null;
        try{
            return buttons.get(buttons.indexOf(focused) - 1);
        } catch(Exception e) { return buttons.get(buttons.size() - 1); }
    }



    /**
     * Because Greenfoot does not support the method for list online.
     */
    private void removeIf(List<Interactable> buttons, Predicate<Interactable> filter) {
        for(int i=0; i<buttons.size();) {
            if(filter.test(buttons.get(i))) buttons.remove(buttons.get(i));
            else i++;
        }
    }



    private Comparator<Interactable> getComparator() {
        return (a, b) -> {
            double dA = Vector2D.between(getLocation(), a.getLocation()).sqrAbs();
            double dB = Vector2D.between(getLocation(), b.getLocation()).sqrAbs();
            if(dA < dB) return -1;
            if(dB < dA) return 1;
            return 0;
        };
    }


    private void updateImage() {
        if(focused == null) {
            setImage((GreenfootImage)null);
            return;
        }
        GreenfootImage image = new GreenfootImage(focused.getImage().getWidth() + 2 * BORDER, focused.getImage().getHeight() + 2 * BORDER);
        image.setColor(theme.main());
        for(int i=0; i<BORDER - 1; i++) image.drawRect(i, i, image.getWidth() - 2 * i - 1, image.getHeight() - 2 * i - 1);
        image.setColor(theme.second());
        image.drawRect(BORDER - 1, BORDER - 1, focused.getImage().getWidth() + 1, focused.getImage().getHeight() + 1);
        setImage(image);
    }


    public void focus(Interactable button) {
        focused = (button != null && button.getWorld() == world) ? button : null;
        updateImage();
    }



    public void allowNaviHolding(boolean flag) {
        navi.allowNaviHolding(flag);
    }

    public boolean getAllowNaviHolding() {
        return navi.getAllowHolding();
    }


    public void allowTabHolding(boolean flag) {
        tabListener.allowHolding = flag;
    }

    public boolean getAllowTabHolding() {
        return tabListener.allowHolding;
    }
}
