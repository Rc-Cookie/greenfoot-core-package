package com.github.rccookie.greenfoot.core;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.rccookie.common.event.Time;
import com.github.rccookie.common.util.Console;

import greenfoot.Actor;
import greenfoot.World;

/**
 * An improced version of {@link World} which offers some
 * comveniance methods especially wo work with {@link CoreActor}.
 * It should be used as base class for custom world
 * implementations instead of greenfoot.World.
 * <p>Like World, CoreWorld is abstract without actually containing
 * any astract methods. This is because the purpose of CoreWorld is
 * to be extended from with a custom implementation while being
 * able to use its functionallity.
 */
public abstract class CoreWorld extends World {

    private static boolean initialized = false;
    static {
        initializeConsole();
    }

    /**
     * An instance of {@link Time} that is automatically being
     * updated.
     */
    protected final Time time = new Time();



    /**
     * Constructs a new CoreWorld with the specified dimensions,
     * a cell size of {@code 1} without bounds.
     * 
     * @param width The width of the world, in pixels
     * @param height The height of the world, in pixels
     */
    public CoreWorld(int width, int height) {
        this(width, height, false);
    }

    /**
     * Constructs a new CoreWorld with the specified dimensions,
     * a cell size of {@code 1}.
     * 
     * @param width The width of the world, in pixels
     * @param height The height of the world, in pixels
     * @param bounded Weather this world should be bounded
     */
    public CoreWorld(int width, int height, boolean bounded) {
        this(width, height, 1, bounded);
    }

    /**
     * Constructs a new CoreWorld with the specified dimensions
     * which is bounded and has the given cell size.
     * 
     * @param width The width of the world, in cells
     * @param height The height of the world, in cells
     * @param cellSize The size of a cell, in pixels
     */
    public CoreWorld(int width, int height, int cellSize) {
        this(width, height, cellSize, false);
    }

    /**
     * Constructs a new CoreWorld with the specified dimensions.
     * 
     * @param width The width of the world, in cells
     * @param height The height of the world, in cells
     * @param cellSize The size of a cell, in pixels
     * @param bounded Weather this world should be bounded
     */
    public CoreWorld(int width, int height, int cellSize, boolean bounded) {
        super(width, height, cellSize, bounded);
    }



    /**
     * Adds the given CoreActor into this world at the exact specified
     * coordinates.
     * 
     * @param object The CoreActor to add
     * @param x The x coordinate to add the object
     * @param y The y coordinate to add the object
     */
    public void addObject(Actor object, double x, double y) {
        if(object.getWorld() == this) return;
        super.addObject(object, (int)x, (int)y);
        if(object instanceof CoreActor) ((CoreActor)object).setLocation(x, y);
    }

    /**
     * Adds the given actor at the specified relative coordinates.
     * {@code x = 0} means left, {@code x = 1} means right.
     * 
     * @param object The object to add
     * @param relativeX The relative x coordinate
     * @param relativeY The relative y coordinate
     */
    public void addRelative(Actor object, double relativeX, double relativeY) {
        addRelative(object, relativeX, relativeY, 0, 0);
    }

    /**
     * Adds the given actor at the specified relative coordinates with
     * the given offset in cells.
     * {@code x = 0} means left, {@code x = 1} means right.
     * 
     * @param object The object to add
     * @param relativeX The relative x coordinate
     * @param relativeY The relative y coordinate
     * @param offX The x offset from the relative location
     * @param offY The y offset from the relative location
     */
    public void addRelative(Actor object, double relativeX, double relativeY, double offX, double offY) {
        double x = getWidth() * relativeX + offX, y = getHeight() * relativeY + offY;
        if(object instanceof CoreActor) addObject((CoreActor)object, x, y);
        else addObject(object, (int)x, (int)y);
    }



    /**
     * Returns an optional actor of the specified class from this world.
     * 
     * @param <A> The type of actor
     * @param cls The class of actor
     * @return An optional containing an actor of the specified class, if
     *         there is any in the world
     */
    public <A> Optional<A> find(Class<A> cls) {
        return getObjects(cls).stream().findAny();
    }

    /**
     * Returns an optional CoreActor of the specified class with the given
     * id from this world.
     * 
     * @param <A> The type of actor
     * @param cls The class of actor
     * @param id The id of the CoreActor, as specified using
     *           {@link CoreActor#setId(String)}
     * @return An optional containing an CoreActor of the specified class and
     *         with the specified id, if there is any in the world
     */
    public <A> Optional<A> find(Class<A> cls, String id) {
        return find(cls, a -> a instanceof CoreActor && Objects.equals(id, ((CoreActor)a).getId()));
    }

    /**
     * Returns an optional Object of the given class that meets the specified
     * requirement and is in this world.
     * 
     * @param <A> The type of actor
     * @param cls The class of actor
     * @param requirement The requirement that the object returned must meet
     * @return An optional containing an actor that meets the requirements if
     *         there is any in the world
     */
    public <A> Optional<A> find(Class<A> cls, Predicate<A> requirement) {
        return getObjects(cls).stream().filter(requirement).findAny();
    }

    /**
     * Returns all actors from this world that meet the given requirement.
     * 
     * @param requirement The requirement an actor must meet to be contained
     *                    in the returned list
     * @return A list of all actors in this world that meet the requirement
     */
    public List<Actor> findAll(Predicate<Actor> requirement) {
        return findAll(Actor.class, requirement);
    }

    /**
     * Returns all CoreActors of this world with the given id.
     * 
     * @param <A> The type of object to find
     * @param cls The class of object to find
     * @param id The id of the CoreActors to find
     * @return A list of CoreActors of the specified class with the given id
     */
    public <A> List<A> findAll(Class<A> cls, String id) {
        return findAll(cls, a -> a instanceof CoreActor && Objects.equals(id, ((CoreActor)a).getId()));
    }

    /**
     * Returns all objects of the specified class from this world that meet
     * the given requirement.
     * 
     * @param <A> The type of object to find
     * @param requirement The requirement an object must meet to be contained
     *                    in the returned list
     * @return A list of all objects in this world that meet the requirement
     */
    public <A> List<A> findAll(Class<A> cls, Predicate<A> requirement) {
        return getObjects(cls).stream().filter(requirement).collect(Collectors.toList());
    }



    /**
     * Sets the time scale for this world and all its CoreActors. You can always
     * modify the time scale of the world exclusively by modifying the
     * {@link Time#timeScale} field of {@link #time}.
     * 
     * @param scale The new time scale
     */
    public void setTimeScale(double scale) {
        time.timeScale = scale;
        for(CoreActor a : getObjects(CoreActor.class)) a.time.timeScale = scale;
    }



    /**
     * Initializes console settings.
     */
    static final void initializeConsole() {
        if(initialized) return;
        initialized = true;
        Console.Config.coloredOutput = false;
        Console.Config.manualConsoleWidth = 60;
        System.setErr(Console.CONSOLE_ERROR_STREAM);
    }



    /**
     * This method is used internally by CoreWorld and can therefore not be used.
     * Override {@link #update()} instead.
     */
    @Override
    public final void act() {
        internalAct();
        update();
    }

    private void internalAct() {
        time.update();
    }

    /**
     * Called once per frame like the {@link #act()} method.
     */
    public void update() {

    }
}
