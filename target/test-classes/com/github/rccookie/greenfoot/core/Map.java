package com.github.rccookie.greenfoot.core;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rccookie.common.event.Time;
import com.github.rccookie.common.geometry.Vector;
import com.github.rccookie.greenfoot.core.GameObject.SupportActor;
import com.github.rccookie.greenfoot.java.util.Optional;

import greenfoot.Actor;
import greenfoot.GreenfootImage;
import greenfoot.World;

/**
 * An improved version of {@link World} which offers some
 * comveniance methods especially wo work with {@link GameObject}.
 * It should be used as base class for custom world
 * implementations instead of greenfoot.World.
 * <p>Like World, CoreWorld is abstract without actually containing
 * any astract methods. This is because the purpose of CoreWorld is
 * to be extended from with a custom implementation while being
 * able to use its functionallity.
 * 
 * @author RcCookie
 * @version 1.0
 */
public abstract class Map extends ComplexUpdateable {

    static {
        Core.initialize();
    }

    /**
     * An instance of {@link Time} that is automatically being
     * updated.
     */
    protected final Time time = new NoExternalUpdateTime();



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
     * Constructs a new map with a default size of {@code 600}x{@400} pixels
     * and a cell size of {@code 1}.
     */
    public Map() {
        this(600, 400);
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
        world = new SupportWorld(this);
        setBackground(Image.block(width, height, Color.WHITE));
    }



    /**
     * Adds the given object into this map at the specified location.
     * 
     * @param object The object to add
     * @param location The location to set the object to
     */
    public void add(GameObject object, Vector location) {
        if(object.getMap().filter(m -> m == this).isPresent()) return;
        world.addObject(object.actor, (int)(location.x() + 0.5), (int)(location.y() + 0.5));
        object.map = this;
        object.setLocation(location);
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
        Objects.requireNonNull(object);
        world.removeObject(object.actor);
        object.map = null;
    }

    public void removeAll(Collection<GameObject> objects) {
        for(GameObject object : objects) remove(object);
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
        List<GameObject> oldOrder = allStream().collect(Collectors.toList());
        List<GameObject> newOrderReversed = paintOrder.getInReverseOrder(oldOrder);
        for(GameObject o : newOrderReversed) {
            Vector location = o.getLocation();
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
    public Optional<GameObject> find(Predicate<GameObject> requirement) {
        return find(GameObject.class, requirement);
    }

    /**
     * Returns an optional object of the specified class from this map.
     * 
     * @param <A> The type of object
     * @param cls The class of object
     * @return An optional containing an object of the specified class, if
     *         there is any in the world
     */
    public <A> Optional<A> find(Class<A> cls) {
        return Optional.ofNullable(findAll(cls).stream().findAny().orElse(null));
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
    public <A> Optional<A> find(Class<A> cls, String id) {
        return find(cls, a -> a instanceof GameObject && Objects.equals(id, ((GameObject)a).getId()));
    }

    /**
     * Returns an optional Object of the given class that meets the specified
     * requirement and is on this map.
     * 
     * @param <A> The type of object
     * @param cls The class of object
     * @param requirement The requirement that the object returned must meet
     * @return An optional containing an object that meets the requirements if
     *         there is any on the map
     */
    public <A> Optional<A> find(Class<A> cls, Predicate<A> requirement) {
        return Optional.ofNullable(allStream(cls).filter(o -> requirement.test(o)).findAny().orElse(null));
    }

    /**
     * Returns a object from this map with the specified id.
     * 
     * @param id The id of the object to find
     * @return An object with the id, or an empty optional
     */
    public Optional<GameObject> find(String id) {
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
        return allStream(cls).filter(o -> requirement.test(o)).collect(Collectors.toSet());
    }



    /**
     * Returns weather this map contains an object that meets the given requirement.
     * 
     * @param requirement The requirement the object has to meet
     * @return {@code true} if there is at least one object on the map that meets
     *         the requirement
     */
    public boolean contains(Predicate<GameObject> requirement) {
        return find(requirement).isPresent();
    }

    /**
     * Returns weather this map contains an object of the given class.
     * 
     * @param <A> The type of object to check for
     * @param cls The class of object to check for
     * @return {@code true} if there is at least one object on the map of that class
     */
    public <A> boolean contains(Class<A> cls) {
        return find(cls).isPresent();
    }

    /**
     * Returns weather this map contains a object of the given class that has
     * the given id.
     * 
     * @param <A> The type of object to check for
     * @param cls The classs of object to check for
     * @param id The id to check for
     * @return {@code true} if this map contains at least one object of the given
     *         class with the specified id
     */
    public <A> boolean contains(Class<A> cls, String id) {
        return find(cls, id).isPresent();
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
        return find(cls, requirement).isPresent();
    }

    /**
     * Returns weather this map contains a object with the given id.
     * 
     * @param id The id to check for
     * @return {@code true} if this map contains at least one object with the
     * specified id
     */
    public boolean contains(String id) {
        return contains(GameObject.class, id);
    }



    /**
     * Sets the time scale for this map and all its object. You can always
     * modify the time scale of the map exclusively by modifying the
     * {@link Time#timeScale} field of {@link #time}.
     * 
     * @param scale The new time scale
     */
    public void setTimeScale(double scale) {
        time.timeScale = scale;
        for(GameObject a : findAll(GameObject.class)) a.time.timeScale = scale;
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
    private List<? extends ComplexUpdateable> getInUpdateOrder() {
        return Stream.concat(Stream.of(this), allStream().sequential()).collect(Collectors.toList());
    }

    /**
     * Called whenever {@link World#act()} is called on the underlying map.
     */
    private void onAct() {
        List<? extends ComplexUpdateable> updateTargets = getInUpdateOrder();
        for(ComplexUpdateable updateTarget : updateTargets) updateTarget.earlyUpdate();
        for(ComplexUpdateable updateTarget : updateTargets) updateTarget.internalUpdate();
        for(ComplexUpdateable updateTarget : updateTargets) updateTarget.update();
        for(ComplexUpdateable updateTarget : updateTargets) updateTarget.lateInternalUpdate();
        for(ComplexUpdateable updateTarget : updateTargets) updateTarget.physicsUpdate();
        for(ComplexUpdateable updateTarget : updateTargets) updateTarget.lateUpdate();
    }

    @Override
    void internalUpdate() {
        ((NoExternalUpdateTime)time).actualUpdate();
    }

    @Override
    void lateInternalUpdate() {
        
    }

    @Override
    protected void earlyUpdate() { }

    @Override
    public void update() { }

    @Override
    protected void lateUpdate() { }

    @Override
    protected void physicsUpdate() { }

    /**
     * Returns the background image of this map.
     * 
     * @return The map's background
     */
    public Image getBackground() {
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
        return getBackground().getColorAt(x, y);
    }

    /**
     * Returns the height of the map, in cells.
     * 
     * @return The map's height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns all objects on this map.
     * 
     * @return A set of all objects currently on this map
     */
    public Set<GameObject> findAll() {
        return allStream().collect(Collectors.toSet());
    }

    /**
     * Returns a stream of all objects of this map.
     * 
     * @return A stream of all objects in this map
     */
    protected Stream<GameObject> allStream() {
        return world.getObjects(SupportActor.class).stream().map(a -> a.gameObject);
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
    protected <T> Stream<T> allStream(Class<T> cls) {
        Objects.requireNonNull(cls);
        return allStream().filter(o -> cls.isInstance(o)).map(o -> (T)o);
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
        Objects.requireNonNull(cls);
        return allStream().filter(o -> cls.isInstance(o)).filter(o -> filter.test(o)).map(o -> (T)o).collect(Collectors.toSet());
    }

    /**
     * Finds all objects from the given class from this map.
     * 
     * @param <A> The type of object
     * @param cls The class of the objects to find
     * @return All objects of the given class from this map
     */
    public <A> Set<A> findAll(Class<A> cls) {
        return allStream(cls).collect(Collectors.toSet());
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
        return findAllFiltered(cls, o -> o.getLocation().equals(location));
    }

    /**
     * Returns the width of this map, in cells.
     * 
     * @return The map's width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Rerenders the map. This may be helpful to ensure that the displayed
     * map is up-to-date.
     */
    public void render() {
        world.superRepaint();
    }

    /**
     * Shows the given text on the background of the map. If there already is
     * text shown on that exact location it woll be replaced.
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
     * Returns a string representation of this object. By default this will be
     * its class name and its size.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getWidth() + "x" + getHeight() + ")";
    }

    /**
     * Sets the background image of this map. If the image is to small, it will
     * be repeated, if it is too large, it will be cut.
     * 
     * @param image The image to set as background image.
     */
    public void setBackground(Image image) {
        world.setBackground(Image.asGImage(image));
        if(image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            // If the image does not fit it will be modified so that in any case
            // the initially passed instance IS the instance used by the map.
            image.scale(getWidth(), getHeight());
            image.clear();
            image.drawImage(Image.of(world.getBackground()), 0, 0);
        }
        this.image = image;
    }



    /**
     * The class of the underlying world. Calls neccecary methods on the map it belongs
     * to when they are called on it.
     */
    static final class SupportWorld extends World {

        final Map map;

        private SupportWorld(Map map) {
            super(map.getWidth(), map.getHeight(), map.getCellSize(), map.isBounded());
            this.map = map;
        }

        @Override
        public void act() {
            
            map.onAct();
        }

        @Override
        public void addObject(Actor object, int x, int y) {
            
            super.addObject(object, x, y);
        }

        @Override
        public GreenfootImage getBackground() {
            
            GreenfootImage image = Image.asGImage(map.getBackground());
            if(image == null) {
                image = new GreenfootImage(getWidth(), getHeight());
                image.setColor(greenfoot.Color.WHITE);
                image.fill();
            }
            return image;
        }

        @Override
        public int getCellSize() {
            
            return map.getCellSize();
        }

        @Override
        public greenfoot.Color getColorAt(int x, int y) {
            
            return Color.asGColor(map.getColorAt(x, y));
        }

        @Override
        public int getHeight() {
            
            return map.getHeight();
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
            
            return map.getWidth();
        }

        @Override
        public void removeObject(Actor object) {
            super.removeObject(object);
        }

        @Override
        public void removeObjects(Collection<? extends Actor> objects) {
            
            super.removeObjects(objects);
        }

        @Override
        public void repaint() {
            
            map.render();
        }

        private void superRepaint() {
            
            super.repaint();
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void setActOrder(Class... classes) {
            
            super.setActOrder(classes);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void setPaintOrder(Class... classes) {
            
            super.setPaintOrder(classes);
        }

        @Override
        public void showText(String text, int x, int y) {
            
            super.showText(text, x, y);
        }

        @Override
        public void started() {
            
            map.paused();
        }

        @Override
        public void stopped() {
            
            map.started();
        }

        @Override
        public String toString() {
            
            return map.toString();
        }
    }



    public static World asWorld(Map map) {
        return map.world;
    }



    public static abstract class MapLoader extends World {

        private Map map;

        private final boolean startRunning;

        public MapLoader() {
            super(600, 400, 1);
            String name = getClass().getSimpleName();
            String className = name.substring(0, name.length() - 6);
            startRunning = false;
            load(() -> {
                try {
                    Constructor<?> ctor = Class.forName(className).getDeclaredConstructor();
                    return (Map)ctor.newInstance();
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public MapLoader(String className) {
            this(() -> {
                try {
                    Constructor<?> ctor = Class.forName(className).getDeclaredConstructor();
                    return (Map)ctor.newInstance();
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }, false);
        }

        public MapLoader(Supplier<Map> mapGenerator, boolean startRunning) {
            this(600, 400, mapGenerator, startRunning);
        }

        public MapLoader(int width, int height, Supplier<Map> mapGenerator, boolean startRunning) {
            super(width, height, 1);
            this.startRunning = startRunning;
            load(mapGenerator);
        }

        private final void load(Supplier<Map> mapGenerator) {
            addObject(GameObject.asActor(Core.isOnline() ?
                Image.text("Loading...", Color.DARK_GRAY, FontStyle.modern(20)).asGameObject() :
                Image.text("Loading...\nIf you are offline and continue to see this image, simply\nhit reset. It should only occur whenever the start map's\nname was changed.", Color.DARK_GRAY, FontStyle.modern(20)).asGameObject()
            ), getWidth() / 2, getHeight() / 2);
            map = mapGenerator.get();
            Core.setMap(map);
            map.render();
            if(startRunning || Core.isOnline()) Core.run();
        }

        @Override
        public void act() {
            Core.setRun(startRunning);
            Core.setMap(map);
            map.render();
        }
    }
}
