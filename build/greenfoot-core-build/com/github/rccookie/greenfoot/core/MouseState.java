package com.github.rccookie.greenfoot.core;

import com.github.rccookie.common.geometry.Vector;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.common.geometry.Vectors;
import com.github.rccookie.greenfoot.util.util.Optional;

import greenfoot.Actor;
import greenfoot.Greenfoot;
import greenfoot.MouseInfo;

/**
 * Represents the state of mouse at a certain point in time.
 * 
 * @author RcCookie
 * @version 1.0
 */
public class MouseState {

    static {
        Core.initialize();
    }

    /**
     * The last not-null mouse state updated whenever {@link #now()} or {@link #get()}
     * is called. By default the mouse points at the top left corner, until at was measured
     * for once.
     */
    private static MouseState last = emulate(null, 0, 0, new Vector2D());

    /**
     * The actor (if any) that the current mouse behaviour is related to. If the mouse was
     * clicked or pressed this will be the actor it was clicked on. If the mouse was
     * dragged or a drag ended, this will be the actor where the drag started. If the mouse
     * was moved, this will be the actor that the mouse is currently over.
     */
    public final Actor actor;

    /**
     * The number of the pressed or clicked button (if any).
     * <p>Usually {@code 1} is the left, {@code 2} the middle and {@code 3} the right mouse
     * button. {@code 0} indicates that no button is pressed.
     */
    public final int button;

    /**
     * The number of times a mouse button has been clicked.
     */
    public final int clickCount;

    /**
     * Indicates weather any button of the mouse is pressed down.
     */
    public final boolean down;

    /**
     * The mouse location in world coordinates.
     */
    public final Vector location;

    /**
     * Weather this mouse state is emulated and not neccecary the case in 'reality'.
     */
    public final boolean emulated;

    /**
     * The return of {@link #toString()}.
     */
    private final String string;

    /**
     * Creates a new MouseState from the given MouseInfo.
     * 
     * @param mouseInfo The MouseInfo to base this mouse state on.
     * @param real Weather this mouse state should be marked as real
     */
    private MouseState(MouseInfo mouseInfo, boolean emulated) {
        this(mouseInfo.getActor(), mouseInfo.getButton(), mouseInfo.getClickCount(), new Vector2D(mouseInfo.getX(), mouseInfo.getY()), emulated);
    }

    /**
     * Creates a new MouseState.
     * 
     * @param actor The actor related to this mouse state
     * @param button The button related to this mouse state
     * @param clickCount The click count related to this mouse state
     * @param location The mouse location
     * @param real Weather this mouse state should be marked as real
     */
    private MouseState(Actor actor, int button, int clickCount, Vector location, boolean emulated) {
        this.actor = actor;
        this.button = button;
        this.clickCount = clickCount;
        this.location = Vectors.immutableVector(location);
        this.emulated = emulated;
        down = button != 0;
        string = "Mouse at " + location;
    }

    /**
     * Returns a representive string of this mouse state in the format
     * {@code "Mouse at " + location.toString()} which will usually result in
     * {@code "Mouse at [x|y]"}.
     */
    @Override
    public String toString() {
        return string;
    }



    /**
     * Emulates the given MouseInfo event. The returned mouse state will be merked as
     * emulated.
     * 
     * @param mouseInfo The MouseInfo to base this mouse state on
     * @return The emulated mouse state
     */
    public static final MouseState emulate(MouseInfo mouseInfo) {
        return new MouseState(mouseInfo, false);
    }

    /**
     * Emulates a mouse state. The returned mouse state will be marked as emulated.
     * 
     * @param actor The actor related to this mouse state
     * @param button The button related to this mouse state
     * @param clickCount The click count related to this mouse state
     * @param location The mouse location
     * @return The emulated mouse state
     */
    public static final MouseState emulate(Actor actor, int button, int clickCount, Vector location) {
        return new MouseState(actor, button, clickCount, location, false);
    }

    /**
     * Returns an optional containing the current mouse state, if available.
     * 
     * @return The current mouse state, or an empty optional
     */
    public static final Optional<MouseState> now() {
        MouseInfo mouseInfo = Greenfoot.getMouseInfo();
        if(mouseInfo == null) return Optional.empty();
        return Optional.of(last = new MouseState(mouseInfo, true));
    }

    /**
     * Returns the most up-to-date mouse state currently available. If it never was,
     * the mouse will be at the coordinates {@code [0|0]} and be marked as emulated.
     * 
     * @return The latest non-null mouse state
     */
    public static final MouseState get() {
        return now().orElse(last);
    }



    /**
     * Returns {@code true} in the frame in which the mouse has been pressed down
     * onto the specified object. The object may be an Actor, a World or {@code null}
     * which will trigger on any press.
     */
    public static final boolean pressed(Object onto) {
        return Greenfoot.mousePressed(onto);
    }

    /**
     * Returns {@code true} in the frame in which the mouse has been released on top
     * of the specified object. The object may be an Actor, a World or {@code null}
     * which will trigger on any release.
     */
    public static final boolean released(Object onTopOf) {
        return Greenfoot.mouseClicked(onTopOf);
    }

    /**
     * Returns {@code true} in any frame in which the mouse has been moved on top
     * of the specified object. The object may be an Actor, a World or {@code null}
     * which will trigger on any movement.
     */
    public static final boolean moved(Object onTopOf) {
        return Greenfoot.mouseMoved(onTopOf);
    }

    /**
     * Returns {@code true} in any frame in which the mouse has been dragged (moved
     * while being pressed) and the drag started on top of the specified object.
     * The object may be an Actor, a World or {@code null} which will trigger on
     * any mouse drag.
     */
    public static final boolean dragged(Object startedOnTopOf) {
        return Greenfoot.mouseDragged(startedOnTopOf);
    }

    /**
     * Returns {@code true} in the frame in which the mouse has stopped dragging
     * (the mouse has been released after dragging) and the drag had initially
     * started on top of the specified object. The object may be an Actor, a World
     * or {@code null} which will trigger on any mouse drag.
     */
    public static final boolean dragEnded(Object startedOnTopOf) {
        return Greenfoot.mouseDragEnded(startedOnTopOf);
    }
}
