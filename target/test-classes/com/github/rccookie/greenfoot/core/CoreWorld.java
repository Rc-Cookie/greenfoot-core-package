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
 * An improved version of {@link World} which offers some
 * comveniance methods especially wo work with {@link CoreActor}.
 * It should be used as base class for custom world
 * implementations instead of greenfoot.World.
 * <p>Like World, CoreWorld is abstract without actually containing
 * any astract methods. This is because the purpose of CoreWorld is
 * to be extended from with a custom implementation while being
 * able to use its functionallity.
 */
public abstract class CoreWorld extends World {

    /**
     * Indicates weather the current session is online or on the Greenfoot application.
     * Offline the code runs plain java ansuring that any java functionallity will work.
     * Online however the code gets converted to javascript which is not very reliable
     * and does not have all classes that java has. Therefore special handling when
     * operating online max be helpful or neccecary.
     */
    public static final boolean IS_ONLINE;
    static {
        boolean isOnline = false;
        // Simple test that will throw an exception when online due to missing class
        // If offline this will do nothing else than some console settings
        try {
            System.setErr(Console.CONSOLE_ERROR_STREAM);
        } catch(Exception e) {
            isOnline = true;
            System.out.println("Online session (Exception)");
            e.printStackTrace();
        } catch(Error e) {
            isOnline = true;
            System.out.println("Online session (Error)");
            e.printStackTrace();
        }
        IS_ONLINE = isOnline;
    }

    private static boolean initialized = false;
    static {
        initializeConsole();
    }

    /**
     * An instance of {@link Time} that is automatically being
     * updated.
     */
    protected final Time time = new NoExternalUpdateTime();



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
     * Finds an actor that meets the given requirement.
     * 
     * @param requirement The requirement the actor has to fulfill to be
     *                    returned
     * @return An optional containing an actor that fulfilles the requirement,
     *         or an empty optional
     */
    public Optional<Actor> find(Predicate<Actor> requirement) {
        return find(Actor.class, requirement);
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
     * Returns weather this world contains an actor that meets the given requirement.
     * 
     * @param requirement The requirement the actor has to meet
     * @return {@code true} if there is at least one actor in the world that meets
     *         the requirement
     */
    public boolean contains(Predicate<Actor> requirement) {
        return find(requirement).isPresent();
    }

    /**
     * Returns weather this world contains an actor of the given class.
     * 
     * @param <A> The type of actor to check for
     * @param cls The class of actor to check for
     * @return {@code true} if there is at least one actor in the world of that class
     */
    public <A> boolean contains(Class<A> cls) {
        return find(cls).isPresent();
    }

    /**
     * Returns weather this world contains a CoreActor of the given class that has
     * the given id.
     * 
     * @param <A> The type of CoreActor to check for
     * @param cls The classs of CoreActor to check for
     * @param id The id to check for
     * @return {@code true} if this world contains at least one CoreActor of the given
     *         class with the specified id
     */
    public <A> boolean contains(Class<A> cls, String id) {
        return find(cls, id).isPresent();
    }

    /**
     * Returns weather this world contains an actor of the given class that meets the
     * specified requirement.
     * 
     * @param <A> The type of actor to check for
     * @param cls The class of actor to check for
     * @param requirement The requirement that the actor must meet
     * @return {@code true} of this world contains at least one actor of the given
     *         class that meets the requirement
     */
    public <A> boolean contains(Class<A> cls, Predicate<A> requirement) {
        return find(cls, requirement).isPresent();
    }

    /**
     * Returns weather this world contains a CoreActor with the given id.
     * 
     * @param id The id to check for
     * @return {@code true} if this world contains at least one CoreActor with the
     * specified id
     */
    public boolean contains(String id) {
        return contains(CoreActor.class, id);
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
     * This method is used internally by CoreWorld and can therefore not be used.
     * Override {@link #update()} instead.
     */
    @Override
    public final void act() {
        internalAct();
        update();
    }

    private void internalAct() {
        ((NoExternalUpdateTime)time).actualUpdate();
    }

    /**
     * Called once per frame like the {@link #act()} method.
     */
    public void update() {

    }



    /**
     * Initializes console settings.
     */
    static final void initializeConsole() {
        if(initialized) return;
        initialized = true;
        Console.Config.coloredOutput = false;
        Console.Config.manualConsoleWidth = 60;
        try {
            System.setErr(Console.CONSOLE_ERROR_STREAM);
        } catch(Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        } catch(Error e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}
