package com.github.rccookie.greenfoot.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import greenfoot.Color;
import greenfoot.GreenfootImage;
import com.github.rccookie.common.util.ClassTag;
import com.github.rccookie.greenfoot.event.KeyListener;
import com.github.rccookie.greenfoot.ui.util.Interactable;

/**
 * The drop.down menu opens a list of options when clicked and saves the selected one.
 * 
 * @author RcCookie
 * @version 1.0
 */
public class DropDownMenu extends TextButton {

    static {
        ClassTag.tag(Backpanel.class, "ui");
    }

    private static final int TOP_BOTTOM_BORDER = 6;
    private static final int SIDE_BORDER = 1;
    


    private final List<Consumer<String>> listeners = new ArrayList<>();

    /**
     * The different buttons of the options shown when opened.
     */
    MenuButton[] menuButtons;

    /**
     * The background that pops up behind the opened menu.
     */
    Backpanel backpanel;

    /**
     * The name of the menu, shown as default selection.
     */
    Text title;


    /**
     * Constructs a new drop-down menu in a default design.
     * 
     * @param name The name of the menu and the default selection
     * @param options More options to choose from
     */
    public DropDownMenu(String name, String... options) {
        this(new Text(name), options);
    }

    /**
     * Constructs a new drop-down menu with the design based on the
     * design of the name text.
     * 
     * @param name The name of the menu and the default selection
     * @param options More options to choose from
     */
    public DropDownMenu(Text title, String... options) {
        super(title);
        this.title = title;

        setDrawOutline(true);
        mapColor("background", 1, false);

        menuButtons = new MenuButton[options.length + 1];
        menuButtons[0] = new MenuButton(title.getTitle());
        for(int i=0; i<options.length; i++) 
            menuButtons[i+1] = addSubElement(new MenuButton(options[i]));

        backpanel = addSubElement(new Backpanel());

        addClickAction(() -> openMenu());
    }





    private void openMenu() {
        setEnabled(false);

        int offset = getImage().getHeight();
        getWorld().addObject(backpanel, getX(), getY() + (int)(0.5 * TOP_BOTTOM_BORDER) + (int)((menuButtons.length - 1) * offset / 2d));

        for(int i=0; i<menuButtons.length; i++)
            backpanel.add(menuButtons[i], 0.5, 0, 0, 1.5 * TOP_BOTTOM_BORDER + getImage().getHeight() / 2 + offset * i);
    }


    private void closeMenu(String selection) {
        setEnabled(true);

        backpanel.remove();

        setTitle(selection);

        for(Consumer<String> listener : listeners) listener.accept(selection);
    }

    @Override
    protected void imageChanged() {
        super.imageChanged();
        if(menuButtons != null)
            for(MenuButton button : menuButtons) button.imageChanged();
    }




    public boolean isDefault() {
        return title.getTitle().equals(getSelection());
    }

    public String getSelection() {
        return getTitle();
    }






    public DropDownMenu addSelectAction(Consumer<String> action) {
        listeners.add(action);
        return this;
    }

    public void removeSelectAction(Consumer<String> action) {
        listeners.remove(action);
    }




    public class Backpanel extends UIPanel {

        private Backpanel() {
            super(
                menuButtons[0].getImage().getWidth() + 2 * SIDE_BORDER,
                menuButtons.length * DropDownMenu.this.getImage().getHeight() + 2 * TOP_BOTTOM_BORDER
            );

            addClickAction(() -> closeMenu(title.getTitle()));
        }

        @Override
        protected void regenerateImages() {
            super.regenerateImages();
            getImage().setColor(darker(title.getDesign().theme().main(), 0.05));
            getImage().fill();
            getImage().setColor(darker(title.getDesign().theme().main(), 0.15));
            getImage().drawRect(0, 0, getImage().getWidth() - 1, getImage().getHeight() - 1);
        }
    }

    private static final Color darker(Color color, double amount) {
        return new Color(
            (int) (color.getRed() * (1 - amount)),
            (int) (color.getGreen() * (1 - amount)),
            (int) (color.getBlue() * (1 - amount)),
            color.getAlpha()
        );
    }


    public class MenuButton extends TextButton {

        private KeyListener escListener = new KeyListener("escape");

        private MenuButton(String name) {
            super(title.clone().setTitle(name));
            addClickAction(() -> closeMenu(getTitle()));
            escListener.addListener(() -> closeMenu(title.getTitle()));
            addAddedAction(world -> {
                if(getTitle().equals(getSelection())) mapColor("background", 2, false);
                else mapColor("background", 0, false);
            });
        }

        @Override
        protected void imageChanged() {
            super.imageChanged();
        }

        @Override
        protected void regenerateImages() {
            if(DropDownMenu.this.getImage() != null) {
                setMinWidth(DropDownMenu.this.getImage().getWidth());
                setMaxWidth(DropDownMenu.this.getImage().getWidth());
                setMinHeight(DropDownMenu.this.getImage().getHeight());
                setMaxHeight(DropDownMenu.this.getImage().getHeight());
            }
            setUseBigBorder(DropDownMenu.this.usesBigBorder());
            //setDesign(DropDownMenu.this.getDesign());
            super.regenerateImages();
        }

        @Override
        protected void createHoverImage(GreenfootImage base) {
            base.setColor(new Color(0, 0, 0, 30));
            base.fill();
        }


        @Override
        public List<Interactable> getFocusable() {
            return new ArrayList<>(Arrays.asList(menuButtons));
        }

        @Override
        public void update() {
            escListener.update();
        }
    }
}
