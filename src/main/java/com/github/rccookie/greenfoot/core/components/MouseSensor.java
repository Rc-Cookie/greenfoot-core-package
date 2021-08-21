package com.github.rccookie.greenfoot.core.components;

import com.github.rccookie.greenfoot.core.Component;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.MouseState;
import com.github.rccookie.util.Arguments;
import greenfoot.Actor;
import greenfoot.ActorVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows easy access to mouse interaction information for the
 * game object, and to fire events based on mouse interaction.
 */
public class MouseSensor extends Component {

    static {
        registerPrefab(MouseSensor.class, MouseSensor::new);
    }



    /**
     * Weather the mouse is currently hovering above the object.
     */
    private boolean hovered = false;

    /**
     * Weather the mouse is currently pressing onto the object.
     */
    private boolean pressed = false;

    /**
     * Actions to perform when the mouse clicks onto this object.
     */
    private final List<Runnable> onClick = new ArrayList<>();

    /**
     * Actions to perform when the mouse presses onto this object.
     */
    private final List<Runnable> onPress = new ArrayList<>();

    /**
     * Actions to perform when the mouse is released after being pressed
     * onto this object.
     */
    private final List<Runnable> onRelease = new ArrayList<>();






    /**
     * Creates a new mouse sensor on the given gameobject.
     *
     * @param gameObject The gameobject to sense mouse interaction with
     */
    public MouseSensor(GameObject gameObject) {
        super(gameObject);
    }



    // Internals

    @Override
    public void update() {
        if(gameObject.tryGetMap().isEmpty()) return; // Not sure why this can happen...+

        Actor actor = GameObject.asActor(gameObject);
        MouseState mouse = MouseState.get();

        hovered = mouse != null && ActorVisitor.containsPoint(
                actor,
                (int) mouse.location.x() * gameObject.getMap().getCellSize(),
                (int) mouse.location.y() * gameObject.getMap().getCellSize()
        );

        if(hovered && MouseState.pressed(actor)) onPress();
        else if(pressed && MouseState.released(null)) onRelease();
    }

    /**
     * Called whenever the mouse clicked onto this object (released the mouse on the object after it had been pressed down on it).
     */
    protected void onClick() {
        for(Runnable action : onClick) action.run();
    }

    /**
     * Called whenever the mouse pressed down(only in the frame that the press started in)on the object.
     */
    protected void onPress(){
        pressed = true;
        for(Runnable action : onPress) action.run();
    }

    /**
     * Called whenever the mouse is released(only in the frame that the press ended) on the object.
     */
    protected void onRelease(){
        pressed = false;
        if(hovered) {
            onClick();
        }
        for(Runnable action : onRelease) action.run();
    }

    // Externals

    /**
     * Virtually presses and releases the mouse from this object within one frame, having the same result as if the mouse actually
     * clicked on this object.
     */
    public void click() {
        if(!isEnabled()) return;

        boolean realHovered = hovered;
        hovered = true;
        onPress();
        onRelease();
        hovered = realHovered;
    }

    /**
     * Returns weather the mouse presses this object right now. This is considered to be the case if the mouse was pressed down on the object
     * or its collider and since then not released.
     *
     * @return Weather the mouse is pressed onto the object
     */
    public boolean pressed() {
        return pressed;
    }

    /**
     * Returns weather the mouse currently hovers above this object.
     *
     * @return Weather the mouse is hovering above this object
     */
    public boolean hovered() {
        return hovered;
    }






    // -----------------------------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------------------------






    /**
     * Adds the given action to those that will be executed whenever the object
     * was clicked on.
     *
     * @param action The action to add
     */
    public void addOnClick(Runnable action) {
        onClick.add(Arguments.checkNull(action));
    }

    /**
     * Adds the given action to those that will be executed whenever the object
     * was pressed on.
     *
     * @param action The action to add
     */
    public void addOnPress(Runnable action) {
        onPress.add(Arguments.checkNull(action));
    }

    /**
     * Adds the given action to those that will be executed whenever the mouse
     * was released after having pressed onto this object.
     *
     * @param action The action to add
     */
    public void addOnRelease(Runnable action) {
        onRelease.add(Arguments.checkNull(action));
    }

    /**
     * Removes the given action from those that will be executed whenever the object
     * was clicked on.
     *
     * @param action The action to remove
     */
    public void removeOnClick(Runnable action) {
        onClick.remove(action);
    }

    /**
     * Removes the given action from those that will be executed whenever the object
     * was pressed on.
     *
     * @param action The action to remove
     */
    public void removeOnPress(Runnable action) {
        onPress.remove(action);
    }

    /**
     * Removes the given action from those that will be executed whenever the was
     * released after having pressed onto this object.
     *
     * @param action The action to remove
     */
    public void removeOnRelease(Runnable action) {
        onRelease.remove(action);
    }
}
