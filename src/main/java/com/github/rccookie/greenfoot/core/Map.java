package com.github.rccookie.greenfoot.core;

import com.github.rccookie.geometry.Vector;
import com.github.rccookie.greenfoot.java.util.Optional;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import greenfoot.Actor;
import greenfoot.GreenfootImage;
import greenfoot.World;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An improved version of {@link World} which offers some
 * convenience methods especially wo work with {@link GameObject}.
 * It should be used as base class for custom world
 * implementations instead of greenfoot.World.
 * <p>Like World, CoreWorld is abstract without actually containing
 * any abstract methods. This is because the purpose of CoreWorld is
 * to be extended from with a custom implementation while being
 * able to use its functionality.
 *
 * @author RcCookie
 * @version 1.0
 */
public abstract class Map extends GameObject {

    static {
        Core.initialize();
    }

    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT = 400;



    /**
     * Weather this world is bounded. This will not change and will be the
     * same as the bounded field in the underlying world, but since that is
     * not accessible it is saved here.
     */
    private final boolean bounded;

    /**
     * The dimensions of this world.
     */
    private final int width, height, cellSize;

    /**
     * The background image of this world.
     */
    private Image image;

    /**
     * The underlying {@link World}.
     */
    final SupportWorld world;

    /**
     * The current paint order.
     */
    private PaintOrder paintOrder = null;

    /**
     * All objects on this map.
     */
    final Set<GameObject> objects = new HashSet<>();



    /**
     * Constructs a new map with a default size of {@code 600x400} pixels
     * and a cell size of {@code 1}.
     */
    public Map() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Constructs a new map with the specified dimensions,
     * a cell size of {@code 1} without bounds.
     *
     * @param width The width of the map, in pixels
     * @param height The height of the map, in pixels
     */
    public Map(int width, int height) {
        this(width, height, false);
    }

    /**
     * Constructs a new map with the specified dimensions,
     * a cell size of 1 and no bounds.
     *
     * @param size The size of the map in pixels
     */
    public Map(Vector size) {
        this((int)size.x(), (int) size.y());
    }

    /**
     * Constructs a new map with the specified dimensions,
     * a cell size of {@code 1}.
     *
     * @param width The width of the map, in pixels
     * @param height The height of the map, in pixels
     * @param bounded Weather this map should be bounded
     */
    public Map(int width, int height, boolean bounded) {
        this(width, height, 1, bounded);
    }

    /**
     * Constructs a new map with the specified dimensions
     * which is bounded and has the given cell size.
     *
     * @param width The width of the map, in cells
     * @param height The height of the map, in cells
     * @param cellSize The size of a cell, in pixels
     */
    public Map(int width, int height, int cellSize) {
        this(width, height, cellSize, false);
    }

    /**
     * Constructs a new map with the specified dimensions.
     *
     * @param width The width of the map, in cells
     * @param height The height of the map, in cells
     * @param cellSize The size of a cell, in pixels
     * @param bounded Weather this map should be bounded
     */
    public Map(int width, int height, int cellSize, boolean bounded) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.bounded = bounded;
        // Order matters!
        world = new SupportWorld();
        setImage(Image.block(width * cellSize, height * cellSize, Color.WHITE));
        removeComponent(DefaultCollider.class);
        addComponent(MapCollider.class);
    }



    /**
     * Adds the given object into this map at the specified location.
     *
     * @param object The object to add
     * @param location The location to set the object to
     */
    public void add(GameObject object, Vector location) {
        if(object.tryGetMap().filter(m -> m == this).isPresent()) return;
        world.addObject(object.actor, (int)(location.x() + 0.5), (int)(location.y() + 0.5));
        object.map = this;
        objects.add(object);
        Console.mapDebug("Added object", object);
        Console.printStackTrace("debug");
        object.location().set(location);
        object.updateActor(); // TODO: Necessary?
        updatePaintOrder();
        object.addedToMap(this);
    }

    /**
     * Adds the given object into this map at the specified coordinates.
     *
     * @param object The object to add
     * @param x The x coordinate to add the object
     * @param y The y coordinate to add the object
     */
    public void add(GameObject object, double x, double y) {
        add(object, Vector.of(x, y));
    }

    /**
     * Adds the given object at the specified relative coordinates.
     * {@code x = 0} means left, {@code x = 1} means right.
     *
     * @param object The object to add
     * @param relativeX The relative x coordinate
     * @param relativeY The relative y coordinate
     */
    public void addRelative(GameObject object, double relativeX, double relativeY) {
        addRelative(object, relativeX, relativeY, 0, 0);
    }

    /**
     * Adds the given object at the specified relative coordinates with
     * the given offset in cells.
     * {@code x = 0} means left, {@code x = 1} means right.
     *
     * @param object The object to add
     * @param relativeX The relative x coordinate
     * @param relativeY The relative y coordinate
     * @param offX The x offset from the relative location
     * @param offY The y offset from the relative location
     */
    public void addRelative(GameObject object, double relativeX, double relativeY, double offX, double offY) {
        double x = getWidth() * relativeX + offX, y = getHeight() * relativeY + offY;
        add(object, Vector.of(x, y));
    }



    /**
     * Removes the given object from this map.
     *
     * @param object The object to remove
     */
    public void remove(GameObject object) {
        Arguments.checkNull(object).remove();
    }

    public void removeAll(Collection<GameObject> objects) {
        for(GameObject object : objects) object.remove();
    }



    public void setPaintOrder(Class<?>... order) {
        if(order == null || order.length == 0) {
            if(paintOrder == null) return;
            paintOrder = null;
        }
        else {
            if(paintOrder instanceof ClassSortedPaintOrder && Arrays.equals(((ClassSortedPaintOrder)paintOrder).order, order)) return;
            Class<?>[] orderCopy = new Class<?>[order.length];
            System.arraycopy(order, 0, orderCopy, 0, order.length);
            paintOrder = new ClassSortedPaintOrder(orderCopy);
        }
        updatePaintOrder();
    }

    public void setPaintOrder(GameObject... order) {
        if(order == null || order.length == 0) {
            if(paintOrder == null) return;
            paintOrder = null;
        }
        else {
            if(paintOrder instanceof InstanceSortedPaintOrder && Arrays.equals(((InstanceSortedPaintOrder)paintOrder).order, order)) return;
            GameObject[] orderCopy = new GameObject[order.length];
            System.arraycopy(order, 0, orderCopy, 0, order.length);
            paintOrder = new InstanceSortedPaintOrder(orderCopy);
        }
        updatePaintOrder();
    }

    public void setPaintOrder(List<GameObject> order) {
        setPaintOrder(order != null ? order.toArray(new GameObject[0]) : null);
    }



    private void updatePaintOrder() {
        if(paintOrder == null) return; // Objects are not sorted
        List<GameObject> oldOrder = stream().collect(Collectors.toList());
        List<GameObject> newOrderReversed = paintOrder.getInReverseOrder(oldOrder);
        for(GameObject o : newOrderReversed) {
            Vector location = o.location();
            world.removeObject(o.actor);
            world.addObject(o.actor, (int)(location.x() + 0.5), (int)(location.y() + 0.5));
        }
    }



    /**
     * Finds an object that meets the given requirement.
     *
     * @param requirement The requirement the object has to fulfill to be
     *                    returned
     * @return An optional containing an object that fulfills the requirement,
     *         or an empty optional
     */
    public Optional<GameObject> tryFind(Predicate<GameObject> requirement) {
        return tryFind(GameObject.class, requirement);
    }

    /**
     * Finds an object that meets the given requirement.
     *
     * @param requirement The requirement the object has to fulfill to be
     *                    returned
     * @return An object that fulfills the requirement, or {@code null}
     */
    public GameObject find(Predicate<GameObject> requirement) {
        return find(GameObject.class, requirement);
    }

    /**
     * Returns an object of the specified class from this map.
     *
     * @param <A> The type of object
     * @param cls The class of object
     * @return An object of the specified class, or {@code null}
     */
    public <A> Optional<A> tryFind(Class<A> cls) {
        return Optional.ofNullable(findAll(cls).stream().findAny().orElse(null));
    }

    public <A> A find(Class<A> cls) {
        return stream(cls).findAny().orElse(null);
    }

    /**
     * Returns an optional object of the specified class with the given
     * id from this map.
     *
     * @param <A> The type of object
     * @param cls The class of object
     * @param id The id of the object, as specified using
     *           {@link GameObject#setId(String)}
     * @return An optional containing an object of the specified class and
     *         with the specified id, if there are any on the map
     */
    public <A> Optional<A> tryFind(Class<A> cls, String id) {
        return Optional.ofNullable(find(cls, id));
    }

    /**
     * Returns an object of the specified class with the given id from this map.
     *
     * @param <A> The type of object
     * @param cls The class of object
     * @param id The id of the object, as specified using
     *           {@link GameObject#setId(String)}
     * @return An object of the specified class and with the specified id, or {@code null}
     */
    public <A> A find(Class<A> cls, String id) {
        return find(cls, a -> a instanceof GameObject && Objects.equals(id, ((GameObject)a).getId()));
    }

    /**
     * Returns an optional object of the given class that meets the specified
     * requirement and is on this map.
     *
     * @param <A> The type of object
     * @param cls The class of object
     * @param requirement The requirement that the object returned must meet
     * @return An optional containing an object that meets the requirements if
     *         there is any on the map
     */
    public <A> Optional<A> tryFind(Class<A> cls, Predicate<A> requirement) {
        return Optional.ofNullable(find(cls, requirement));
    }

    /**
     * Returns an object of the given class that meets the specified
     * requirement and is on this map.
     *
     * @param <A> The type of object
     * @param cls The class of object
     * @param requirement The requirement that the object returned must meet
     * @return An object that meets the requirements, or {@code null}
     */
    public <A> A find(Class<A> cls, Predicate<A> requirement) {
        return stream(cls).filter(requirement).findAny().orElse(null);
    }

    /**
     * Returns an object from this map with the specified id.
     *
     * @param id The id of the object to find
     * @return An object with the id, or an empty optional
     */
    public Optional<GameObject> tryFind(String id) {
        return Optional.ofNullable(find(id));
    }

    /**
     * Returns an object from this map with the specified id.
     *
     * @param id The id of the object to find
     * @return An object with the id, or {@code null}
     */
    public GameObject find(String id) {
        return find(o -> Objects.equals(id, o.getId()));
    }

    /**
     * Returns all objects from this map that meet the given requirement.
     *
     * @param requirement The requirement an object must meet to be contained
     *                    in the returned list
     * @return A list of all objects on this map that meet the requirement
     */
    public Set<GameObject> findAll(Predicate<GameObject> requirement) {
        return findAll(GameObject.class, requirement);
    }

    /**
     * Returns all object of this map with the given id.
     *
     * @param <A> The type of object to find
     * @param cls The class of object to find
     * @param id The id of the object to find
     * @return A list of object of the specified class with the given id
     */
    public <A> Set<A> findAll(Class<A> cls, String id) {
        return findAll(cls, a -> Objects.equals(id, ((GameObject)a).getId()));
    }

    /**
     * Returns all objects of the specified class from this map that meet
     * the given requirement.
     *
     * @param <A> The type of object to find
     * @param requirement The requirement an object must meet to be contained
     *                    in the returned list
     * @return A list of all objects on this map that meet the requirement
     */
    public <A> Set<A> findAll(Class<A> cls, Predicate<A> requirement) {
        return stream(cls).filter(requirement).collect(Collectors.toSet());
    }



    /**
     * Returns weather this map contains an object that meets the given requirement.
     *
     * @param requirement The requirement the object has to meet
     * @return {@code true} if there is at least one object on the map that meets
     *         the requirement
     */
    public boolean contains(Predicate<GameObject> requirement) {
        return tryFind(requirement).isPresent();
    }

    /**
     * Returns weather this map contains an object of the given class.
     *
     * @param <A> The type of object to check for
     * @param cls The class of object to check for
     * @return {@code true} if there is at least one object on the map of that class
     */
    public <A> boolean contains(Class<A> cls) {
        return tryFind(cls).isPresent();
    }

    /**
     * Returns weather this map contains an object of the given class that has
     * the given id.
     *
     * @param <A> The type of object to check for
     * @param cls The class of object to check for
     * @param id The id to check for
     * @return {@code true} if this map contains at least one object of the given
     *         class with the specified id
     */
    public <A> boolean contains(Class<A> cls, String id) {
        return tryFind(cls, id).isPresent();
    }

    /**
     * Returns weather this map contains an object of the given class that meets the
     * specified requirement.
     *
     * @param <A> The type of object to check for
     * @param cls The class of object to check for
     * @param requirement The requirement that the object must meet
     * @return {@code true} of this map contains at least one object of the given
     *         class that meets the requirement
     */
    public <A> boolean contains(Class<A> cls, Predicate<A> requirement) {
        return tryFind(cls, requirement).isPresent();
    }

    /**
     * Returns weather this map contains an object with the given id.
     *
     * @param id The id to check for
     * @return {@code true} if this map contains at least one object with the
     * specified id
     */
    public boolean contains(String id) {
        return contains(GameObject.class, id);
    }



    /**
     * Returns weather this world is bounded.
     *
     * @return {@code true} if this world is bounded
     */
    public boolean isBounded() {
        return bounded;
    }



    /**
     * Returns all objects from this map and itself in the order they should be updated in.
     *
     * @return All objects that need to be updated in proper update order
     */
    private List<? extends GameObject> getInUpdateOrder() {
        return Stream.concat(Stream.of(this), stream()).collect(Collectors.toList());
    }

    /**
     * Called whenever {@link World#act()} is called on the underlying map.
     */
    private void onAct() {
        RuntimeException e = null;

        if(isActiveMap()) e = Core.earlyGlobalUpdate();

        List<? extends GameObject> updateTargets = getInUpdateOrder();
        for(GameObject updateTarget : updateTargets) e = runEarlyInternalUpdate(updateTarget, e);
        for(GameObject updateTarget : updateTargets) e = runEarlyUpdate(updateTarget, e);
        for(GameObject updateTarget : updateTargets) e = runInternalUpdate(updateTarget, e);
        for(GameObject updateTarget : updateTargets) e = runUpdate(updateTarget, e);
        for(GameObject updateTarget : updateTargets) e = runLateInternalUpdate(updateTarget, e);
        for(GameObject updateTarget : updateTargets) e = runLateUpdate(updateTarget, e);
        for(GameObject updateTarget : updateTargets) e = runVeryLateInternalUpdate(updateTarget, e);
        for(GameObject updateTarget : updateTargets) updateTarget.ensureTransformUpToDate();

        if(isActiveMap()) e = Core.lateGlobalUpdate(e);

        if(e != null) throw e;
    }

    private RuntimeException runEarlyInternalUpdate(GameObject object, RuntimeException exception) {
        try {
            object.earlyInternalUpdate();
        } catch(RuntimeException e) {
            if(exception == null) return e;
            else exception.addSuppressed(e);
        }
        return exception;
    }

    private RuntimeException runEarlyUpdate(GameObject object, RuntimeException exception) {
        if(object.getMap() != this) return exception;
        try {
            object.earlyUpdate();
        } catch(RuntimeException e) {
            if(exception == null) return e;
            else exception.addSuppressed(e);
        }
        return exception;
    }

    private RuntimeException runInternalUpdate(GameObject object, RuntimeException exception) {
        if(object.getMap() != this) return exception;
        try {
            object.internalUpdate();
        } catch(RuntimeException e) {
            if(exception == null) return e;
            else exception.addSuppressed(e);
        }
        return exception;
    }

    private RuntimeException runUpdate(GameObject object, RuntimeException exception) {
        if(object.getMap() != this) return exception;
        try {
            object.update();
        } catch(RuntimeException e) {
            if(exception == null) return e;
            else exception.addSuppressed(e);
        }
        return exception;
    }

    private RuntimeException runLateInternalUpdate(GameObject object, RuntimeException exception) {
        if(object.getMap() != this) return exception;
        try {
            object.lateInternalUpdate();
        } catch(RuntimeException e) {
            if(exception == null) return e;
            else exception.addSuppressed(e);
        }
        return exception;
    }

    private RuntimeException runLateUpdate(GameObject object, RuntimeException exception) {
        if(object.getMap() != this) return exception;
        try {
            object.lateUpdate();
        } catch(RuntimeException e) {
            if(exception == null) return e;
            else exception.addSuppressed(e);
        }
        return exception;
    }

    private RuntimeException runVeryLateInternalUpdate(GameObject object, RuntimeException exception) {
        if(object.getMap() != this) return exception;
        try {
            object.veryLateInternalUpdate();
        } catch(RuntimeException e) {
            if(exception == null) return e;
            else exception.addSuppressed(e);
        }
        return exception;
    }


    /**
     * Runs a full update loop for this map and all its gameobjects.
     */
    public void runFrame() {
        onAct();
    }



    protected void onSet() { }

    protected void onClose() { }



    /**
     * Returns the background image of this map.
     *
     * @return The map's background
     */
    @Override
    public Image getImage() {
        return image != null ? image : (image = Image.block(getWidth(), getHeight(), Color.WHITE));
    }

    /**
     * Returns the cell size of map.
     *
     * @return The map's cell size
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Returns the color of the background at the specified location.
     *
     * @param x The x coordinate of the pixel to get the color of
     * @param y The y coordinate of the pixel to get the color of
     * @return The color at that pixel
     */
    public Color getColorAt(int x, int y) {
        return getImage().getColorAt(x, y);
    }

    /**
     * Returns the height of the map, in cells.
     *
     * @return The map's height
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Returns all objects on this map.
     *
     * @return A set of all objects currently on this map
     */
    public Set<GameObject> findAll() {
        return Collections.unmodifiableSet(objects);
    }

    /**
     * Returns a stream of all objects of this map.
     *
     * @return A stream of all objects in this map
     */
    public Stream<GameObject> stream() {
        return objects.stream();
    }

    /**
     * Returns a stream of all objects of the given class of this map
     *
     * @param <T> The type of objects
     * @param cls The class of the objects in the stream
     * @return A stream with only the objects from this world that are from the
     *         given class
     */
    @SuppressWarnings("unchecked")
    public <T> Stream<T> stream(Class<T> cls) {
        Arguments.checkNull(cls);
        return streamObjects(cls).map(o -> (T)o);
    }

    /**
     * Returns a stream of all objects of the given class of this map, as gameobjects.
     *
     * @param cls The class of the objects in the stream
     * @return A stream with only the objects from this world that are from the
     *         given class
     */
    protected Stream<GameObject> streamObjects(Class<?> cls) {
        Arguments.checkNull(cls);
        return stream().filter(cls::isInstance);
    }

    /**
     * Returns all objects of this map that match the given class and filter.
     *
     * @param <T> The type of object
     * @param cls The class of the objects returned
     * @param filter A filter indicating weather an object should be included,
     *               with the object as {@link GameObject} as parameter
     * @return All objects from this map that are from the given class and
     *         match the specified filter
     */
    @SuppressWarnings("unchecked")
    protected <T> Set<T> findAllFiltered(Class<T> cls, Predicate<GameObject> filter) {
        Arguments.checkNull(cls);
        return stream().filter(cls::isInstance).filter(filter).map(o -> (T)o).collect(Collectors.toSet());
    }

    /**
     * Finds all objects from the given class from this map.
     *
     * @param <A> The type of object
     * @param cls The class of the objects to find
     * @return All objects of the given class from this map
     */
    public <A> Set<A> findAll(Class<A> cls) {
        return stream(cls).collect(Collectors.toSet());
    }

    /**
     * Finds all objects from the given class at the specified location on
     * this map.
     *
     * @param <A> The type of object
     * @param location The location that the objects must be at
     * @param cls The class of the objects to find
     * @return All objects from the given class at the specified location on
     *         this map
     */
    public <A> Set<A> findAllAt(Vector location, Class<A> cls) {
        return findAllFiltered(cls, o -> o.location().equals(location));
    }

    /**
     * Returns the width of this map, in cells.
     *
     * @return The map's width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Returns the size of this map, in cells.
     *
     * @return The map's size
     */
    @Override
    public Vector getSize() {
        return Vector.of(getWidth(), getHeight());
    }

    /**
     * Renders the map. This may be helpful to ensure that the displayed
     * map is up-to-date.
     */
    public void render() {
        world.superRepaint();
    }

    /**
     * Shows the given text on the background of the map. If there already is
     * text shown on that exact location it will be replaced.
     *
     * @param text The string to display
     * @param x The x coordinate of the center of the text
     * @param y The y coordinate of the center of the text
     */
    public void showText(String text, int x, int y) {
        world.showText(text, x, y);
    }

    /**
     * Executed when the update-loop gets resumed. Intended to be overridden.
     */
    public void started() {

    }

    /**
     * Executed when the update-loop gets paused. Intended to be overridden.
     */
    public void paused() {

    }

    /**
     * Returns a string representation of this object. By default, this will be
     * its class name and its size.
     */
    @Override
    public String toString() {
        return (Core.getRealSession().isJava() && getClass().isAnonymousClass() ? getClass().getSuperclass().getSimpleName() : getClass().getSimpleName())
                + " (" + getWidth() + "x" + getHeight() + ")";
    }

    /**
     * Sets the background image of this map. If the image is too small, it will
     * be repeated, if it is too large, it will be cut.
     *
     * @param image The image to set as background image.
     */
    @Override
    public void setImage(Image image) {
        if(world == null) return; // During super ctor call
        world.setBackground(Image.asGImage(image));
        if(image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            // If the image does not fit it will be modified so that in any case
            // the initially passed instance IS the instance used by the map.
            image.scale(getWidth() * getCellSize(), getHeight() * getCellSize());
            image.clear();
            image.drawImage(Image.of(world.getBackground()), 0, 0);
        }
        this.image = image;
    }

    /**
     * Returns whether this map is currently being displayed.
     *
     * @return {@code true} if this map is the one currently displayed, {@code false}
     *         otherwise
     */
    public boolean isActiveMap() {
        return this == Core.getMap();
    }


    @Override
    SupportActor createActor() {
        return new MapSupportActor();
    }

    final class MapSupportActor extends SupportActor {

    }

    @Override
    public Map getMap() {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> M getMap(Class<M> mapType) {
        Arguments.checkNull(mapType);
        return mapType.isInstance(this) ? (M) this : null;
    }

    @Override
    public boolean remove() {
        return false;
    }

    @Override
    public Map setId(String id) {
        super.setId(id);
        return this;
    }

    @Override
    public Vector location() {
        // Must be mutable
        return Vector.of(getWidth() / 2d, getHeight() / 2d);
    }

    @Override
    public double rotation() {
        return 0;
    }

    @Override
    public void setRotation(double rotation) {
        // Do nothing; DON'T throw any exceptions
    }

    @Override
    protected void transformModified() {
        // Never relevant
    }

    @Override
    void ensureTransformUpToDate() {

    }

    @Override
    void updateActor() {

    }

    @Override
    protected <A> Set<A> findAllAtOffset(Class<A> cls, Vector offset) {
        return super.findAllAtOffset(cls, offset);
    }

    @Override
    protected <A> Set<A> findAllInRange(Class<A> cls, double radius) {
        return super.findAllInRange(cls, radius);
    }

    @Override
    public GameObject addOnAdd(Consumer<Map> action) {
        return super.addOnAdd(action);
    }

    @Override
    public GameObject addOnAdd(Runnable action) {
        return super.addOnAdd(action);
    }

    @Override
    public GameObject removeOnAdd(Consumer<Map> action) {
        return super.removeOnAdd(action);
    }

    @Override
    public GameObject addOnRemove(Consumer<Map> action) {
        return super.addOnRemove(action);
    }

    @Override
    public GameObject addOnRemove(Runnable action) {
        return super.addOnRemove(action);
    }

    @Override
    public GameObject removeOnRemove(Consumer<Map> action) {
        return super.removeOnRemove(action);
    }

    @Override
    void addedToMap(Map map) {
        super.addedToMap(map);
    }

    public static World asWorld(Map map) {
        return map.world;
    }

    /**
     * @deprecated Only meant for executing by the Greenfoot ui
     */
    @Deprecated
    public static void executeOnCurrent(Consumer<Map> command) {
        executeOnCurrent(m -> {
            command.accept(m);
            return null;
        });
    }

    /**
     * @deprecated Only meant for executing by the Greenfoot ui
     */
    @Deprecated
    public static <R> R executeOnCurrent(Function<Map, R> command) {
        return command.apply(Core.getMap());
    }



    /**
     * The class of the underlying world. Calls necessary methods on the map it belongs
     * to when they are called on it.
     */
    final class SupportWorld extends World {

        private SupportWorld() {
            super(Map.this.getWidth(), Map.this.getHeight(), Map.this.getCellSize(), Map.this.isBounded());
        }

        @Override
        public void act() {
            Map.this.onAct();
        }

        @Override
        public void addObject(Actor object, int x, int y) {
            super.addObject(object, x, y);
        }

        @Override
        public GreenfootImage getBackground() {
            GreenfootImage image = Image.asGImage(Map.this.getImage());
            if(image == null) {
                image = new GreenfootImage(getWidth(), getHeight());
                image.setColor(greenfoot.Color.WHITE);
                image.fill();
            }
            return image;
        }

        @Override
        public int getCellSize() {
            return Map.this.getCellSize();
        }

        @Override
        public greenfoot.Color getColorAt(int x, int y) {
            return Color.asGColor(Map.this.getColorAt(x, y));
        }

        @Override
        public int getHeight() {
            return Map.this.getHeight();
        }

        @Override
        public <A> List<A> getObjects(Class<A> cls) {
            return super.getObjects(cls);
        }

        @Override
        public <A> List<A> getObjectsAt(int x, int y, Class<A> cls) {
            return super.getObjectsAt(x, y, cls);
        }

        @Override
        public int numberOfObjects() {
            return super.numberOfObjects();
        }

        @Override
        public int getWidth() {
            return Map.this.getWidth();
        }

        @Override
        public void removeObject(Actor object) {
            if(object instanceof MapSupportActor) return;
            super.removeObject(object);
        }

        @Override
        public void removeObjects(Collection<? extends Actor> objects) {
            for(Actor o : objects) removeObject(o);
        }

        @Override
        public void repaint() {
            Map.this.render();
        }

        private void superRepaint() {
            super.repaint();
        }

        @Override
        public void setActOrder(Class... classes) {
            super.setActOrder(classes);
        }

        @Override
        public void setPaintOrder(Class... classes) {
            super.setPaintOrder(classes);
        }

        @Override
        public void showText(String text, int x, int y) {
            super.showText(text, x, y);
        }

        @Override
        public void started() {
            Map.this.started();
        }

        @Override
        public void stopped() {
            Map.this.paused();
        }

        @Override
        public String toString() {
            return Map.this.toString();
        }
    }



    public static class Loader extends World {

        static {
            Core.initialize();
        }

        private static boolean initialized = false;

        private Map map;

        private boolean startRunning;

        @Deprecated
        public Loader() {
            super(600, 400, 1);
            String name = getClass().getSimpleName();
            String className = name.substring(0, name.length() - 6);
            startRunning = false;
            load(() -> {
                try {
                    Constructor<?> ctor = Class.forName(className).getDeclaredConstructor();
                    return (Map)ctor.newInstance();
                } catch(Throwable t) {
                    throw new RuntimeException("Failed to generate map instance using reflection", t);
                }
            });
        }

        public Loader(String className) {
            this(() -> {
                try {
                    Constructor<?> ctor = Class.forName(className).getDeclaredConstructor();
                    return (Map)ctor.newInstance();
                } catch(Throwable t) {
                    throw new RuntimeException("Failed to generate map instance using reflection", t);
                }
            }, false);
        }

        public Loader(Supplier<Map> mapGenerator, boolean startRunning) {
            this(600, 400, mapGenerator, startRunning);
        }

        public Loader(int width, int height, Supplier<Map> mapGenerator, boolean startRunning) {
            super(width, height, 1);
            this.startRunning = startRunning;

            // Saved across multiple runs because static variables only get reset when
            // the vm restarts -> don't reload fully every time
            if(!initialized) {
                try {
                    initialize();
                } catch (Exception e) {
                    Console.error("Exception initializing map loader");
                    e.printStackTrace();
                }
                initialized = true;
            }
            load(mapGenerator);
        }

        private void load(Supplier<Map> mapGenerator) {
            addObject(GameObject.asActor(Core.getSession() == Session.ONLINE ?
                Image.text("Loading...", Color.DARK_GRAY, Font.modern(20)).asGameObject() :
                Image.text("Loading...\nIf you are offline and continue to see this image, simply\nhit reset. It should only occur whenever the start map's\nname was changed.", Color.DARK_GRAY, Font.modern(20)).asGameObject()
            ), getWidth() / 2, getHeight() / 2);
            try {
                map = mapGenerator.get();
            } catch(Exception e) {
                Console.error("Exception in map generator");
                e.printStackTrace();
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                Image errorMessageImage = Image.text("Failed to load map:\n\n" + writer, Color.RED, Font.monospace(20));
                Image errorMessageImageWithBackground = Image.block(errorMessageImage.getSize(), Color.DARK_GRAY);
                errorMessageImageWithBackground.drawImage(errorMessageImage, 0, 0);
                map = new Map(errorMessageImage.getWidth(), errorMessageImage.getHeight()) {{ setImage(errorMessageImageWithBackground); }};
                startRunning = false;
            }
            Core.setMap(map);
            map.render();
            if(startRunning || Core.getSession() == Session.ONLINE) Core.run();
        }

        @Override
        public final void act() {
            Core.setRun(startRunning);
            Core.setMap(map);
            map.render();
        }

        protected void initialize() { }
    }
}
