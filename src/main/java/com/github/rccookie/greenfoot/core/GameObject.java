package com.github.rccookie.greenfoot.core;

import com.github.rccookie.data.json.JsonSerializable;
import com.github.rccookie.event.Time;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Vector2D;
import com.github.rccookie.geometry.Vectors;
import com.github.rccookie.greenfoot.java.util.Optional;
import com.github.rccookie.util.Updateable;
import greenfoot.Actor;
import greenfoot.ActorVisitor;
import greenfoot.GreenfootImage;
import greenfoot.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A GameObject is an an object that can be on a {@link Map}. It is based on {@link Actor} sharing all its common
 *  features and adding some more:
 * <ul>
 * <li>Double based localization with smooth movement and movement in steps over longer distances
 * <li>Vector and transform based movement methods
 * <li>Framerate dependent movement speed (see {@code fixedMove(int)}) to be independent of the framerate
 * <li>Included {@code Time} instance that is automatically being updated
 * <li>Information about the mouse state on this object and methods that may be executed when clicked
 * </ul>
 * Like {@link Actor}, GameObject is abstract, as its purpose is to be used for a specific implementation. It
 * does not contain any abstract methods though that may need to be implemented.
 * 
 * @author RcCookie
 * @version 4.0
 * @see Map
 * @see Actor
 * @see Time
 * @see Vector
 */
@JsonSerializable
public abstract class GameObject extends ComplexUpdateable {

    static {
        Core.initialize();
    }



    /**
     * The default image of an actor, the 'green foot'.
     */
    @SuppressWarnings("unused")
    private static final Image DEFAULT_GREENFOOT_IMAGE = Image.of(new Actor() { }.getImage());

    private static final Color DEFAULT_COLOR = Color.LIGHT_GRAY;

    private static final int DEFAULT_IMAGE_SIZE = 20;

    /**
     * The default image of a game object.
     */
    private static final Image DEFAULT_IMAGE = createDefaultImage();

    private static Image createDefaultImage() {
        Image image = Image.block(
            DEFAULT_IMAGE_SIZE,
            DEFAULT_IMAGE_SIZE,
            DEFAULT_COLOR
        );
        image.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1, DEFAULT_COLOR.darker());
        return image;
    }






    // -----------------------------------------------------------------------------
    // Instance fields
    // -----------------------------------------------------------------------------






    // Location



    /**
     * The location of the object.
     */
    private final Vector location = Vector.of(0, 0);

    /**
     * The rotation of the object.
     */
    private double angle = 0;

    /**
     * An immutable vector wrapping around the location vector of the transform. It can savely
     * be returned by any get method that returns the location of this object without the risk
     * of it being modified nor the unnecessary creation of a new vector every time a get method
     * is called.
     */
    private final Vector locationGetter = Vectors.immutableVector(location);

    /**
     * The map the object is currently on.
     */
    Map map = null;

    /**
     * The velocity of the object.
     */
    private final Vector velocity = new Vector2D();



    // Rendering



    /**
     * The image of this object.
     */
    private Image image;

    /**
     * The underlying support actor that will actually be displayed.
     */
    final SupportActor actor = new SupportActor();



    // Mouse interactions



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



    // Listeners



    /**
     * The time object of this game object. It is updated once per frame and can be accessed by extending classes.
     */
    protected final Time time = new NoExternalUpdateTime();

    /**
     * Actions to perform on every frame.
     */
    private final List<Runnable> onUpdate = new ArrayList<>();

    private final List<Runnable> addedOnUpdate = new ArrayList<>();

    private final List<Runnable> removedOnUpdate = new ArrayList<>();

    /**
     * Actions to perform later on every frame.
     */
    private final List<Runnable> onLateUpdate = new ArrayList<>();

    private final List<Runnable> addedOnLateUpdate = new ArrayList<>();

    private final List<Runnable> removedOnLateUpdate = new ArrayList<>();

    /**
     * Actions to perform when the object is added to a map.
     */
    private final List<Consumer<Map>> onAdd = new ArrayList<>();



    // misc



    /**
     * The id of this object;
     */
    private String id = null;






    // -----------------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------------






    /**
     * Constructs a new game object.
     */
    public GameObject() {
        addOnUpdate(((NoExternalUpdateTime)time)::actualUpdate);
        addOnLateUpdate(() -> fixedMove(velocity));
        setImage(DEFAULT_IMAGE.clone());
    }






    // -----------------------------------------------------------------------------
    // Update methods
    // -----------------------------------------------------------------------------






    @Override
    public void earlyUpdate() { }

    @Override
    public void update() { }

    /**
     * Called once per frame. Is intended to contain physical operations (but also things like button functionality)
     * and is therefore called after the {@code update()} method.
     * <p>Make sure to always call the super implementation of this method when overriding it!
     */
    @Override
    protected void physicsUpdate() { }

    @Override
    public void lateUpdate() { }

    @Override
    void internalUpdate() {
        handleMouseInteractions();
        handleUpdateListeners();
    }

    @Override
    void lateInternalUpdate() {
        handleLateUpdateListeners();
    }

    private void handleUpdateListeners() {
        onUpdate.addAll(addedOnUpdate);
        addedOnUpdate.clear();
        onUpdate.removeAll(removedOnUpdate);
        removedOnUpdate.clear();
        for(Runnable listener : onUpdate) listener.run();
    }

    private void handleLateUpdateListeners() {
        onLateUpdate.addAll(addedOnLateUpdate);
        addedOnLateUpdate.clear();
        onLateUpdate.removeAll(removedOnLateUpdate);
        removedOnLateUpdate.clear();
        for(Runnable listener : onLateUpdate) listener.run();
    }






    // -----------------------------------------------------------------------------
    // Basics
    // -----------------------------------------------------------------------------






    /**
     * Returns the {@link Map} this game object is currently on, if any.
     *
     * @return The map this object is currently on
     */
    public Optional<Map> getMap() {
        return Optional.ofNullable(map);
    }

    /**
     * Returns the map of the given type this object is currently on. If the object
     * is not on a map of the given type or not on a map at all this will return an
     * empty optional.
     *
     * @param <M> The type of map
     * @param mapType The class of the map that should be returned
     * @return The map the object is on
     */
    @SuppressWarnings("unchecked")
    public <M> Optional<M> getMap(Class<M> mapType) {
        Objects.requireNonNull(mapType);
        return getMap().filter(mapType::isInstance).map(m -> (M)m);
    }

    /**
     * Removes this object from the map it is on, if there is one.
     *
     * @return Weather the object was on a map before
     */
    public boolean remove() {
        return getMap().ifPresent(m -> m.remove(this));
    }

    /**
     * Returns the id of this object. You can set it's id using
     * {@link #setId(String)}.
     *
     * @return The id of this object, defaulted to {@code null}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this object. By default the id is {@code null}.
     * You can request an object's id using {@link #getId()}.
     *
     * @param id The new id for this object
     * @return This object
     */
    public GameObject setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns a string representation of this object. By default this will return the name of the
     * class of the object and its location.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + (getMap().isEmpty() ? "" : " at " + getLocation());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }






    // -----------------------------------------------------------------------------
    // Image
    // -----------------------------------------------------------------------------






    /**
     * Sets the image of this object.
     */
    public void setImage(Image image){
        this.image = image;
        actor.superSetImage(Image.asGImage(image));
    }

    /**
     * Returns the current image of this object. The returned instance is
     * not a copy, so modifications will effect the look of this object.
     *
     * @return The object's image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Returns the current width of this object, that is defined by the
     * width of its image. If this object currently does not have an image,
     * {@code 0} will be returned.
     *
     * @return The width of this object
     */
    public int getWidth() {
        Image image = getImage();
        return image != null ? image.getWidth() : 0;
    }

    /**
     * Returns the current height of this object, that is defined by the
     * height of its image. If this object currently does not have an image,
     * {@code 0} will be returned.
     *
     * @return The height of this object
     */
    public int getHeight() {
        Image image = getImage();
        return image != null ? image.getHeight() : 0;
    }






    // -----------------------------------------------------------------------------
    // Mouse interactions
    // -----------------------------------------------------------------------------






    private void handleMouseInteractions() {
        try {
            MouseState mouse = MouseState.get();
            hovered = mouse != null && ActorVisitor.containsPoint(actor, (int) mouse.location.x() * getMap().get().getCellSize(), (int) mouse.location.y() * getMap().get().getCellSize());
            if(hovered && MouseState.pressed(actor)) onPress();
            else if(pressed && MouseState.released(null)) onRelease();
        } catch(NoSuchElementException e) {
            // Not sure why this can happen...
        }
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



    /**
     * Virtually presses and releases the mouse from this object within one frame, having the same result as if the mouse actually
     * clicked on this object.
     */
    public void click() {
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
    // Movement
    // -----------------------------------------------------------------------------




    // Location




    /**
     * Moves the object to the location of the other object.
     *
     * @param toObjectsLocation The object to move to
     */
    public void setLocation(GameObject toObjectsLocation) {
        setLocation(toObjectsLocation.getLocation());
    }

    /**
     * Moves the object to the specified x coordinate.
     *
     * @param x The new x coordinate
     */
    public void setX(double x) {
        location.setX(x);
        updateLocation();
    }

    /**
     * Moves the object to the specified y coordinate.
     *
     * @param y The new y coordinate
     */
    public void setY(double y) {
        location.setY(y);
        updateLocation();
    }

    /**
     * Moves the object to the specified x and y coordinates. While floating point
     * values may not be visible, they will be saved.
     *
     * @param x The new x coordinate of the object
     * @param y The new y coordinate of the object
     */
    public void setLocation(double x, double y) {
        location.setX(x);
        location.setY(y);
        updateLocation();
    }

    /**
     * Moves the object to the specified location.
     *
     * @param location The new location of the object
     */
    public void setLocation(Vector location) {
        this.location.set(location);
        updateLocation();
    }

    /**
     * Updates the underlying actor's location.
     */
    private void updateLocation(){
        actor.superSetLocation((int)(location.x() + 0.5), (int)(location.y() + 0.5));
    }




    // Moving




    /**
     * Moves the object the specified distance.
     *
     * @param movement The vector that describes the movement of the object
     */
    public void move(Vector movement) {
        setLocation(location.add(movement));
    }

    /**
     * Moves the object the specified distance in the direction it is currently facing.
     *
     * @param distance The distance in cells to move
     */
    public void move(double distance) {
        move(Vector2D.angledVector(angle, distance));
    }

    /**
     * Moves the object the specified number of cells parallel to the x axis.
     *
     * @param distance The distance in cells for the object to move
     */
    public void moveX(double distance) {
        move(Vector2D.angledVector(0, distance));
    }

    /**
     * Moves the object the specified number of cells parallel to the y axis.
     *
     * @param distance The distance in cells for the object to move
     */
    public void moveY(double distance) {
        move(Vector2D.angledVector(90, distance));
    }




    // Fixed moving




    /**
     * Moves the object the specified distance multiplied by the current time delta.
     * This means that if this method is called once per frame with the parameter
     * {@code x} the object will in sum move the length of {@code x} per second,
     * independent of the framerate.
     *
     * @param movement The distance to move in cells/second
     */
    public void fixedMove(Vector movement) {
        move(movement.scaled(time.deltaTime()));
    }

    /**
     * Moves the object the specified distance multiplied by the current time delta.
     * This means that if this method is called once per frame with the parameter
     * {@code x} the object will in sum move the length of {@code x} per second,
     * independent of the framerate.
     *
     * @param distance The distance to move in cells/second
     */
    public void fixedMove(double distance){
        fixedMove(Vector2D.angledVector(angle, distance));
    }

    /**
     * Moves the object the specified distance parallel to the x axis multiplied by the
     * current time delta. This means that if this method is called once per frame with
     * the parameter {@code x} the object will in sum move the length of {@code x} per
     * second, independent of the framerate.
     *
     * @param distance The distance to move in cells/second
     */
    public void fixedMoveX(double distance){
        fixedMove(Vector2D.angledVector(0, distance));
    }

    /**
     * Moves the object the specified distance parallel to the y axis multiplied by the
     * current time delta. This means that if this method is called once per frame with
     * the parameter {@code x} the object will in sum move the length of {@code x} per
     * second, independent of the framerate.
     *
     * @param distance The distance to move in cells/second
     */
    public void fixedMoveY(double distance){
        fixedMove(Vector2D.angledVector(90, distance));
    }






    // -----------------------------------------------------------------------------
    // Rotation
    // -----------------------------------------------------------------------------






    /**
     * Sets the rotations of the object. While floating point values may not be
     * visible, they will be saved.
     *
     * @param angle The rotation in degrees
     */
    public void setAngle(double angle) {
        this.angle = angle;
        actor.superSetRotation((int)(angle + 0.5));
    }

    /**
     * Turn this object towards the given target.
     *
     * @param target The object to look at
     */
    public void turnTowards(GameObject target) {
        turnTowards(target.getLocation());
    }

    /**
     * Turn this object towards the given target.
     *
     * @param target The location to look at
     */
    public void turnTowards(Vector target) {
        setAngle(Vector.between(getLocation(), target.get2D()).angle());
    }

    /**
     * Turn this object facing towards the given target.
     *
     * @param x The x coordinate of the target to look at
     * @param y The y coordinate of the target to look at
     */
    public void turnTowards(double x, double y) {
        turnTowards(Vector.of(x, y));
    }

    /**
     * Turns the object the specified amount of degrees. Positive values will result
     * in clockwise rotation, negative in counterclockwise.
     *
     * @param angle The angle to turn the object in degrees
     */
    public void turn(double angle) {
        setAngle(this.angle + angle);
    }

    /**
     * Turns the object the specified amount of degrees multiplied by the current time delta.
     * This means that if this method is called once per frame with the parameter {@code x}
     * the object will in sum turn {@code x} degrees per second, independent of the framerate.
     *
     * @param angle The turning speed in degrees/second
     */
    public void fixedTurn(double angle) {
        turn(angle * time.deltaTime());
    }






    // -----------------------------------------------------------------------------
    // Position getters
    // -----------------------------------------------------------------------------






    /**
     * Returns the x coordinate of the object.
     *
     * @return The x coordinate of the object
     */
    public double getX() {
        return location.x();
    }

    /**
     * Returns the y coordinate of the object.
     *
     * @return The y coordinate of the object
     */
    public double getY() {
        return location.y();
    }

    /**
     * Returns the location of the object.
     *
     * @return The location of the object
     */
    public Vector getLocation() {
        return locationGetter;
    }

    /**
     * Returns the angle of rotation of this object.
     *
     * @return The angle of this object
     */
    public double getAngle(){
        return angle;
    }






    // -----------------------------------------------------------------------------
    // Animating / automation
    // -----------------------------------------------------------------------------






    /**
     * Sets the velocity of this object.
     *
     * @param velocity The new velocity for this object. Must not be
     *                 {@code null}
     */
    public void setVelocity(Vector velocity) {
        Objects.requireNonNull(velocity);
        this.velocity.set(velocity);
    }

    /**
     * Returns this object's velocity. Modifications to this vector <b>will</b>
     * effect it's velocity!
     *
     * @return This object's velocity
     */
    public Vector velocity() {
        return velocity;
    }

    /**
     * Runs the given animation on this object.
     *
     * @param animation The animation to run
     * @return The runnable representing the animation. May be used to cancel the animation early
     */
    public Runnable animate(Animation animation) {
        Runnable runnable = animation.build(this);
        addOnUpdate(runnable);
        return runnable;
    }

    public void link(GameObject object, Vector offset) {
        Vector offsetClone = offset.clone();
        addOnLateUpdate(() -> object.setLocation(getLocation().added(offsetClone.get2D().rotated(getAngle()))));
    }






    // -----------------------------------------------------------------------------
    // Find methods
    // -----------------------------------------------------------------------------






    /**
     * Returns all game objects of the specified class that intersect this
     * object.
     * 
     * @param <A> The type of object
     * @param cls The class of the objects to return
     * @return All intersecting game objects of the specified class
     */
    @SuppressWarnings("unchecked")
    protected <A> Set<A> findAllIntersecting(Class<A> cls) {
        Objects.requireNonNull(cls);
        return actor.getIntersectingObjects(SupportActor.class)
                    .stream()
                    .map(SupportActor::gameObject)
                    .filter(cls::isInstance)
                    .map(o -> (A)o)
                    .collect(Collectors.toSet());
    }

    /**
     * Returns all game objects of the specified class that are at the specified
     * location relative to this objects location.
     * 
     * @param <A> The type of object
     * @param dx The x offset to this objects location
     * @param dy The y offset to this objects location
     * @param cls The class of the objects to return
     * @return All game objects at the specified offset of the given class
     */
    @SuppressWarnings("unchecked")
    protected <A> Set<A> findAllAtOffset(double dx, double dy, Class<A> cls) {
        Objects.requireNonNull(cls);
        Vector target = Vector.of(dx, dy).add(getLocation());
        return map.findAll(SupportActor.class)
                    .stream()
                    .map(SupportActor::gameObject)
                    .filter(cls::isInstance)
                    .filter(o -> o.getLocation().equals(target))
                    .map(o -> (A)o)
                    .collect(Collectors.toSet());
    }

    /**
     * Returns all game objects of the specified class that are in the given range.
     * 
     * @param <A> The type of object
     * @param radius The radius to search in (the exact coordinates matter, not the image)
     * @param cls The class of the objects to return
     * @return All game objects at the specified offset of the given class
     */
    @SuppressWarnings("unchecked")
    protected <A> Set<A> findAllInRange(double radius, Class<A> cls) {
        Objects.requireNonNull(cls);
        return actor.getObjectsInRange((int)(radius + 1), null)
                    .stream()
                    .map(a -> ((SupportActor)a).gameObject())
                    .filter(cls::isInstance)
                    .filter(o -> Vector.between(getLocation(), o.getLocation()).abs() <= radius)
                    .map(o -> (A)o)
                    .collect(Collectors.toSet());
    }

    /**
     * Finds an object of the given class that graphically intersects this object.
     * 
     * @param <A> The type of object
     * @param cls The class of the object to return
     * @return An optional with an intersecting object, or an empty optional
     */
    @SuppressWarnings("unchecked")
    protected <A> Optional<A> findIntersecting(Class<A> cls) {
        Objects.requireNonNull(cls);
        return Optional.ofNullable(actor.getWorld().getObjects(SupportActor.class)
                            .stream()
                            .map(SupportActor::gameObject)
                            .filter(cls::isInstance)
                            .filter(this::intersects)
                            .map(o -> (A)o)
                            .findAny()
                            .orElse(null));
    }

    /**
     * Checks weather this game object graphically intersects the given object.
     * 
     * @param other The object to check collisions for
     * @return {@code true} if the two objects intersect, {@code false} otherwise
     */
    protected boolean intersects(GameObject other) {
        Objects.requireNonNull(other);
        return actor.intersects(other.actor);
    }

    /**
     * Returns weather this object graphically touches an object of the given class.
     * 
     * @param cls The class of the objects to check intersection for
     * @return Weather this object touches any objects of the given class
     */
    protected boolean isTouching(Class<?> cls) {
        return findIntersecting(cls).isPresent();
    }

    /**
     * Removes an intersecting object of the given class, if there is any.
     */
    protected void removeOneTouching(Class<?> cls) {
        findIntersecting(cls).ifPresent(o -> ((GameObject)o).remove());
    }






    // -----------------------------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------------------------




    // Mouse interaction




    /**
     * Adds the given action to those that will be executed whenever the object
     * was clicked on.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnClick(Runnable action) {
        onClick.add(Objects.requireNonNull(action));
        return this;
    }

    /**
     * Adds the given action to those that will be executed whenever the object
     * was pressed on.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnPress(Runnable action) {
        onPress.add(Objects.requireNonNull(action));
        return this;
    }

    /**
     * Adds the given action to those that will be executed whenever the mouse
     * was released after having pressed onto this object.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnRelease(Runnable action) {
        onRelease.add(Objects.requireNonNull(action));
        return this;
    }

    /**
     * Removes the given action from those that will be executed whenever the object
     * was clicked on.
     * 
     * @param action The action to remove
     * @return This object
     */
    public GameObject removeOnClick(Runnable action) {
        onClick.remove(action);
        return this;
    }

    /**
     * Removes the given action from those that will be executed whenever the object
     * was pressed on.
     * 
     * @param action The action to remove
     * @return This object
     */
    public GameObject removeOnPress(Runnable action) {
        onPress.remove(action);
        return this;
    }

    /**
     * Removes the given action from those that will be executed whenever the was
     * released after having pressed onto this object.
     * 
     * @param action The action to remove
     * @return This object
     */
    public GameObject removeOnRelease(Runnable action) {
        onRelease.remove(action);
        return this;
    }




    // Other listeners




    /**
     * Adds the given action to be executes whenever the object gets added to a
     * different map.
     *
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnAdd(Consumer<Map> action) {
        onAdd.add(Objects.requireNonNull(action));
        return this;
    }

    /**
     * Adds the given action to be executes whenever the object gets added to a
     * different map.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnAdd(Runnable action) {
        Objects.requireNonNull(action);
        return addOnAdd(m -> action.run());
    }

    /**
     * Removes the given action from those that will be executes whenever the object
     * gets added to a different map.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject removeOnAdd(Consumer<Map> action) {
        onAdd.remove(action);
        return this;
    }

    /**
     * Adds the given action that will be executed during every update sequence.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnUpdate(Runnable action) {
        if(action != null) addedOnUpdate.add(action);
        return this;
    }

    /**
     * Registers the given updatable to be updated by this game object.
     * 
     * @param updatable The updatable to add
     * @return This object
     */
    public GameObject register(Updateable updatable) {
        if(updatable == null) return this;
        return addOnUpdate(updatable::update);
    }

    /**
     * Removes the given action from those that will be executed during every update
     * sequence.
     * 
     * @param listener The listener to remove
     * @return This object
     */
    public GameObject removeOnUpdate(Runnable listener) {
        if(listener != null) removedOnUpdate.add(listener);
        return this;
    }

    /**
     * Adds the given action that will be executed later during every update sequence.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnLateUpdate(Runnable action) {
        if(action != null) addedOnLateUpdate.add(action);
        return this;
    }

    /**
     * Registers the given updatable to be updated later by this game object.
     * 
     * @param updatable The updatable to add
     * @return This object
     */
    public GameObject registerLate(Updateable updatable) {
        if(updatable == null) return this;
        return addOnLateUpdate(updatable::update);
    }

    /**
     * Removes the given action from those that will be executed later during every update
     * sequence.
     * 
     * @param listener The listener to remove
     * @return This object
     */
    public GameObject removeOnLateUpdate(Runnable listener) {
        if(listener != null) removedOnLateUpdate.add(listener);
        return this;
    }






    // -----------------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------------






    void addedToMap(Map map) {
        for(Consumer<Map> action : onAdd) action.accept(map);
    }



    /**
     * Returns the underlying actor that represents this object on a greenfoot world.
     * <p>Usually this method is not necessary to use as the whole core package works
     * as an enclosed system.
     *
     * @param gameObject The object to get the actor from
     * @return The object's actor representation
     */
    public static Actor asActor(GameObject gameObject) {
        return gameObject.actor;
    }



    /**
     * An implementation of actor that will on method calls first call the methods of
     * its game object. If is used to display a game object in a greenfoot world.
     */
    final class SupportActor extends Actor {

        // false by default, gets assigned to true after super ctor call. MUST BE FINAL AND
        // ASSIGNED INSTANTLY to work, otherwise it has the value true right away.
        private final boolean initialized;
        {
            initialized = true;
        }

        @Override
        public void act() {
            // Nothing - handled by the map to allow for more customization in update order
        }

        @Override
        protected void addedToWorld(World world) {
            // Nothing - handled by the map to first set the exact added position and then call this stuff
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return GameObject.this.clone();
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            return GameObject.this.equals(obj);
        }

        @Override
        public GreenfootImage getImage() {
            return Image.asGImage(GameObject.this.getImage());
        }

        @Override
        protected <A> List<A> getIntersectingObjects(Class<A> cls) {
            return super.getIntersectingObjects(cls);
        }

        @Override
        protected <A> List<A> getNeighbours(int distance, boolean diagonal, Class<A> cls) {
            return super.getNeighbours(distance, diagonal, cls);
        }

        @Override
        protected <A> List<A> getObjectsAtOffset(int dx, int dy, Class<A> cls) {
            return super.getObjectsAtOffset(dx, dy, cls);
        }

        @Override
        protected <A> List<A> getObjectsInRange(int radius, Class<A> cls) {
            return super.getObjectsInRange(radius, cls);
        }

        @Override
        protected Actor getOneIntersectingObject(Class<?> cls) {
            return super.getOneIntersectingObject(cls);
        }

        @Override
        protected Actor getOneObjectAtOffset(int dx, int dy, Class<?> cls) {
            return super.getOneObjectAtOffset(dx, dy, cls);
        }

        @Override
        public int getRotation() {
            return (int)(GameObject.this.getAngle() + 0.5);
        }

        @Override
        public World getWorld() {
            return super.getWorld();
        }

        @Override
        public <W> W getWorldOfType(Class<W> worldClass) {
            return super.getWorldOfType(worldClass);
        }

        @Override
        public int getX() {
            return (int)(GameObject.this.getX() + 0.5);
        }

        @Override
        public int getY() {
            return (int)(GameObject.this.getY() + 0.5);
        }

        @Override
        public int hashCode() {
            return GameObject.this.hashCode();
        }

        @Override
        protected boolean intersects(Actor other) {
            return super.intersects(other);
        }

        @Override
        public boolean isAtEdge() {
            return GameObject.this.getX() <= 0
                    || GameObject.this.getY() <= 0
                    || GameObject.this.getX() >= GameObject.this.getMap().get().getWidth() - 1
                    || GameObject.this.getY() >= GameObject.this.getMap().get().getHeight() - 1;
        }

        @Override
        protected boolean isTouching(Class<?> cls) {
            return super.isTouching(cls);
        }

        @Override
        public void move(int distance) {
            GameObject.this.move(distance);
            setLocation(getX(), getY());
        }

        @Override
        protected void removeTouching(Class<?> cls) {
            super.removeTouching(cls);
        }

        @Override
        public void setImage(GreenfootImage image) {
            // When creating a new instance setImage gets called from Actor ctor before
            // the appropriate field in GameObject is assigned. We skip that one:
            // initialized gets assigned after the super call, and is false by default.
            // Later we assign out own default image.
            if(initialized) GameObject.this.setImage(Image.of(image));
        }

        void superSetImage(GreenfootImage image) {
            super.setImage(image);
        }

        @Override
        public void setImage(String filename) throws IllegalArgumentException {
            GameObject.this.setImage(Image.load(filename));
            super.setImage(getImage());
        }

        @Override
        public void setLocation(int x, int y) {
            GameObject.this.setLocation(x, y);
        }

        void superSetLocation(int x, int y) {
            super.setLocation(x, y);
        }

        @Override
        public void setRotation(int rotation) {
            GameObject.this.setAngle(rotation);
        }

        void superSetRotation(int rotation) {
            super.setRotation(rotation);
        }

        @Override
        public String toString() {
            return GameObject.this.toString();
        }

        @Override
        public void turn(int amount) {
            GameObject.this.turn(amount);
            setRotation(getRotation());
        }

        @Override
        public void turnTowards(int x, int y) {
            GameObject.this.turnTowards(x, y);
            setRotation(getRotation());
        }

        GameObject gameObject() {
            return GameObject.this;
        }
    }
}
