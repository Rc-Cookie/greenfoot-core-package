package com.github.rccookie.greenfoot.core;

import com.github.rccookie.data.json.JsonSerializable;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Vector2D;
import com.github.rccookie.greenfoot.java.util.Optional;
import com.github.rccookie.util.Arguments;
import greenfoot.Actor;
import greenfoot.GreenfootImage;
import greenfoot.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A GameObject is an object that can be on a {@link Map}. It is based on {@link Actor} sharing all its common
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
public class GameObject {

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
     * The location of this object.
     */
    private final Vector location = new Vector2D() {
        @Override
        public Vector2D set(int dimension, double coordinate) throws UnsupportedOperationException, DimensionOutOfBoundsException {
            if(get(dimension) == coordinate) return this;
            super.set(dimension, coordinate);
            transformModified();
            return this;
        }
    };

    /**
     * The rotation of this object.
     */
    private double rotation = 0;

    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private boolean transformModified = true;

    /**
     * The map the object is currently on.
     */
    Map map = null; // TODO: Move map handling into transform ?



    // Rendering



    /**
     * The image of this object.
     */
    private Image image;

    /**
     * The underlying support actor that will actually be displayed.
     */
    final SupportActor actor = createActor();

    SupportActor createActor() {
        return new SupportActor();
    }


    // Listeners



    final Set<Component> components = new HashSet<>();

    /**
     * Actions to perform when the object is added to a map.
     */
    private final Set<Consumer<Map>> onAdd = new HashSet<>();

    private final Set<Consumer<Map>> onRemove = new HashSet<>();



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
        setImage(DEFAULT_IMAGE.clone());
        new DefaultCollider(this);
    }






    // -----------------------------------------------------------------------------
    // Update methods
    // -----------------------------------------------------------------------------






    public void earlyUpdate() { }

    public void update() { }

    public void lateUpdate() { }

    protected void earlyInternalUpdate() { }

    void internalUpdate() {
        earlyUpdateComponents();
    }

    void lateInternalUpdate() {
        updateComponents();
    }

    void veryLateInternalUpdate() {
        lateUpdateComponents();
    }

    private void earlyUpdateComponents() {
        for (Iterator<Component> iterator = components.iterator(); iterator.hasNext(); ) {
            Component c = iterator.next();
            if(c.isEnabled()) c.runEarlyUpdate();
        }
    }

    private void updateComponents() {
        for(Iterator<Component> iterator = components.iterator(); iterator.hasNext();) {
            Component c = iterator.next();
            if(c.isEnabled()) c.update();
        }
    }

    private void lateUpdateComponents() {
        for(Iterator<Component> iterator = components.iterator(); iterator.hasNext();) {
            Component c = iterator.next();
            if(c.isEnabled()) c.lateUpdate();
        }
    }






    // -----------------------------------------------------------------------------
    // Basics
    // -----------------------------------------------------------------------------






    /**
     * Returns the {@link Map} this game object is currently on, if any.
     *
     * @return The map this object is currently on
     */
    public Map getMap() {
        return map;
    }

    /**
     * Returns the {@link Map} this game object is currently on, if any.
     *
     * @return The map this object is currently on
     */
    public Optional<Map> tryGetMap() {
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
    public <M> Optional<M> tryGetMap(Class<M> mapType) {
        Arguments.checkNull(mapType);
        return tryGetMap().filter(mapType::isInstance).map(m -> (M)m);
    }

    /**
     * Returns the map of the given type this object is currently on. If the object
     * is not on a map of the given type or not on a map at all this will return {@code null}.
     *
     * @param <M> The type of map
     * @param mapType The class of the map that should be returned
     * @return The map the object is on
     */
    public <M> M getMap(Class<M> mapType) {
        return tryGetMap(mapType).orNull();
    }

    /**
     * Removes this object and its children from the map, if it is on one.
     *
     * @return Weather the object was on a map before
     */
    public boolean remove() {
        return tryGetMap().ifPresent(m -> {
            m.world.removeObject(actor);
            m.objects.remove(this);
            map = null;
            for(Iterator<Consumer<Map>> i = onRemove.iterator(); i.hasNext();)
                i.next().accept(m);
        });
    }

    /**
     * Returns the id of this object. You can set its id using
     * {@link #setId(String)}.
     *
     * @return The id of this object, defaulted to {@code null}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this object. By default, the id is {@code null}.
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
     * Returns a string representation of this object. By default, this will return the name of the
     * class of the object and its location.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + (tryGetMap().isEmpty() ? "" : " at " + location() + "@" + rotation() + "°");
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }






    // -----------------------------------------------------------------------------
    // Transform
    // -----------------------------------------------------------------------------






    /**
     * Returns the location of this gameobject. Modify this vector to move the object.
     *
     * @return The location of this object
     */
    public Vector location() {
        return location;
    }

    /**
     * Returns the rotation of this gameobject.
     *
     * @return The rotation of this object
     */
    public double rotation() {
        return rotation;
    }

    /**
     * Sets the rotation of this object.
     *
     * @param rotation The rotation to set
     */
    public void setRotation(double rotation) {
        if(this.rotation == rotation) return;
        this.rotation = rotation;
        transformModified();
    }

    /**
     * Moves the object to the location of the other object.
     *
     * @param toObjectsLocation The object to move to
     */
    public void moveTo(GameObject toObjectsLocation) {
        location().set(toObjectsLocation.location());
    }



    // Moving



    /**
     * Moves the object the specified distance in the direction it is currently facing.
     *
     * @param distance The distance in cells to move
     */
    public void move(double distance) {
        location().add(Vector2D.angled(rotation(), distance));
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
        location().add(movement.scaled(Time.deltaTime()));
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
        move(distance * com.github.rccookie.greenfoot.core.Time.deltaTime());
    }



    // Rotation




    /**
     * Turn this object towards the given target object.
     *
     * @param target The object to look at
     */
    public void turnTowards(GameObject target) {
        Arguments.checkNull(target);
        turnTowards(Vector.between(location(), target.location()));
    }

    /**
     * Turn this object towards the given target coordinates.
     *
     * @param target The location to look at
     */
    public void turnTowards(Vector target) {
        setRotation(Vector.between(location(), target).angle());
    }

    /**
     * Turns the object the specified amount of degrees. Positive values will result
     * in clockwise rotation, negative in counterclockwise.
     *
     * @param rotation The angle to turn the object in degrees
     */
    public void turn(double rotation) {
        setRotation(rotation() + rotation);
    }

    /**
     * Turns the object the specified amount of degrees multiplied by the current time delta.
     * This means that if this method is called once per frame with the parameter {@code x}
     * the object will in sum turn {@code x} degrees per second, independent of the framerate.
     *
     * @param rotation The turning speed in degrees/second
     */
    public void fixedTurn(double rotation) {
        turn(rotation * Time.deltaTime());
    }



    // Internals



    /**
     * Inform the transform that it has been modified.
     */
    protected void transformModified() {
        /*if(Core.isRunning())
            transformModified = true;
        else*/ updateActor(); // TODO: Check performance penalty
    }

    void ensureTransformUpToDate() {
        if(transformModified) updateActor();
    }

    void updateActor() {
        actor.superSetLocation((int) (location().x() + 0.5), (int) (location().y() + 0.5));
        actor.superSetRotation((int) (rotation() + 0.5));
    }






    // -----------------------------------------------------------------------------
    // Image
    // -----------------------------------------------------------------------------






    /**
     * Sets the image of this object.
     */
    public void setImage(Image image) {
        this.image = image;
        actor.superSetImage(Image.asGImage(image));
    }

    /**
     * Returns the current image of this object. The returned instance is
     * not a copy, so modifications will affect the look of this object.
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

    /**
     * Returns the current size of this object, that is defined by the
     * size of its image. If this object currently does not have an image,
     * a zero vector will be returned.
     *
     * @return The size of this object
     */
    public Vector getSize() {
        Image image = getImage();
        return image != null ? image.getSize() : Vector.of();
    }





    // -----------------------------------------------------------------------------
    // Components
    // -----------------------------------------------------------------------------






    public <C extends Component> C addComponent(Class<C> type, Object... arguments) {
        return Component.instantiatePrefab(type, this, arguments);
    }

    /**
     * Returns a component of the given type, if this object has such a component.
     *
     * @param type The type of component
     * @param <C> The type of component
     * @return A component of the given type, or {@code null}
     */
    @SuppressWarnings("unchecked")
    public <C extends Component> C getComponent(Class<C> type) {
        for (Component c : components) {
            if(type == null || type.isInstance(c)) return (C) c;
        }
        return null;
    }

    /**
     * Returns an optional with a component of the given type, if this object
     * has such a component.
     *
     * @param type The type of component
     * @param <C> The type of component
     * @return An optional with a component of the given type, or an empty optional
     */
    @SuppressWarnings("unchecked")
    public <C extends Component> Optional<C> tryGetComponent(Class<C> type) {
        for (Component c : components) {
            if(type == null || type.isInstance(c)) return Optional.of((C) c);
        }
        return Optional.empty();
    }

    /**
     * Returns all components of the given type from this object.
     *
     * @param type The type of components
     * @param <C> The type of components
     * @return All components of the given type
     */
    @SuppressWarnings("unchecked")
    public <C extends Component> Set<C> getComponents(Class<C> type) {
        Set<C> cs = new HashSet<>();
        for (Component c : components) {
            if(type == null || type.isInstance(c)) cs.add((C)c);
        }
        return cs;
    }

    /**
     * Returns whether this object has a component of the given type.
     *
     * @param type The type of component to check for
     * @return {@code true} if this object has a component of the
     *         specified type
     */
    public boolean hasComponent(Class<? extends Component> type) {
        return tryGetComponent(type).isPresent();
    }

    /**
     * Ensures that this GameObject has at least one component of the given
     * type. If not the specified generator will be used to create such a
     * component.
     *
     * @param type The type of component required
     * @param generator A function to generate a new component of the given
     *                  type if none is present
     * @param <C> The type of component required
     * @return A component of the given type. Either one that was already
     *         present, or the newly generated one
     */
    public <C extends Component> C requireComponent(Class<C> type, Supplier<C> generator) {
        Arguments.checkNull(type, "type");
        Arguments.checkNull(generator, "generator");
        return tryGetComponent(type).orElseGet(generator);
    }

    /**
     * Ensures that this GameObject has at least one component of the given
     * type. If not the specified generator will be used to create such a
     * component.
     *
     * @param type The type of component required
     * @param generator A function to generate a new component of the given
     *                  type if none is present. Takes this GameObject as
     *                  parameter
     * @param <C> The type of component required
     * @return A component of the given type. Either one that was already
     *         present, or the newly generated one
     */
    public <C extends Component> C requireComponent(Class<C> type, Function<GameObject, C> generator) {
        Arguments.checkNull(type, "type");
        Arguments.checkNull(generator, "generator");
        return tryGetComponent(type).orElseGet(() -> generator.apply(this));
    }

    /**
     * Ensures that this gameobject has at least one component of the given type.
     * If not, a new instance will be generated. Therefore, {@code type} must point
     * at a class that has a prefab generator registered in {@link Component}.
     *
     * @param type The type of component required
     * @param arguments Arguments used to generate a new component if none is present
     * @param <C> The type of component required
     * @return A component of the given type
     */
    public <C extends Component> C requireComponent(Class<C> type, Object... arguments) {
        return requireComponent(type, () -> Component.instantiatePrefab(type, this, arguments));
    }



    /**
     * Removes the given component from this gameobject. Removed components
     * cannot be added again, so also consider disabling the component using
     * {@link Component#setEnabled(boolean)} instead.
     *
     * @param component The component to remove
     * @return Whether the component was present before
     */
    public boolean removeComponent(Component component) {
        Arguments.checkNull(component);
        component.onRemove();
        return components.remove(component);
    }

    /**
     * Removes a component of the given type from this gameobject, if there
     * is any. Removed components cannot be added again, so also consider
     * disabling the component using {@link Component#setEnabled(boolean)}
     * instead.
     *
     * @param type The type of component to remove
     * @return Whether a component was removed
     */
    public boolean removeComponent(Class<? extends Component> type) {
        return tryGetComponent(Arguments.checkNull(type)).ifPresent(this::removeComponent);
    }

    /**
     * Removes the given components from this gameobject. Removed components
     * cannot be added again, so also consider disabling the component using
     * {@link Component#setEnabled(boolean)}.
     *
     * @param components The components to remove
     * @return Whether at least one of the components was present before
     */
    public boolean removeComponents(Collection<? extends Component> components) {
        boolean modified = false;
        for(Component component : Arguments.checkNull(components))
            modified |= removeComponent(component);
        return modified;
    }

    /**
     * Removes any components of the given type from this gameobject, if there
     * are any. Removed components cannot be added again, so also consider
     * disabling the component using {@link Component#setEnabled(boolean)}
     * instead.
     *
     * @param type The type of components to remove
     * @return Whether any component was removed
     */
    public boolean removeComponents(Class<? extends Component> type) {
        return removeComponents(getComponents(type));
    }






    // -----------------------------------------------------------------------------
    // Find methods
    // -----------------------------------------------------------------------------







    /**
     * Returns all game objects of the specified class that are at the specified
     * location relative to this object's location.
     *
     * @param <A> The type of object
     * @param cls The class of the objects to return
     * @param offset The offset to this objects location
     * @return All game objects at the specified offset of the given class
     */
    @SuppressWarnings("unchecked")
    protected <A> Optional<A> findAtOffset(Class<A> cls, Vector offset) {
        Arguments.checkNull(cls);
        Vector target = offset.added(location());
        return Optional.ofNullable(map.streamObjects(cls)
                .filter(o -> o.location().equals(target))
                .map(o -> (A)o)
                .findAny().orElse(null));
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
    protected <A> Optional<A> findInRange(Class<A> cls, double radius) {
        Arguments.checkNull(cls);
        final double sqrDist = radius * radius;
        return Optional.ofNullable(actor.getObjectsInRange((int)(radius + 1), SupportActor.class)
                .stream()
                .map(SupportActor::gameObject)
                .filter(cls::isInstance)
                .filter(o -> o != this && Vector.between(location(), o.location()).sqrAbs() <= sqrDist)
                .map(o -> (A)o)
                .findAny().orElse(null));
    }

    /**
     * Returns all game objects of the specified class that are at the specified
     * location relative to this object's location.
     *
     * @param <A> The type of object
     * @param cls The class of the objects to return
     * @param offset The offset to this objects location
     * @return All game objects at the specified offset of the given class
     */
    @SuppressWarnings("unchecked")
    protected <A> Set<A> findAllAtOffset(Class<A> cls, Vector offset) {
        Arguments.checkNull(cls);
        Vector target = offset.added(location());
        return map.streamObjects(cls)
                    .filter(o -> o.location().equals(target))
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
    protected <A> Set<A> findAllInRange(Class<A> cls, double radius) {
        Arguments.checkNull(cls);
        final double sqrDist = radius * radius;
        return actor.getObjectsInRange((int)(radius + 1), SupportActor.class)
                .stream()
                .map(SupportActor::gameObject)
                .filter(cls::isInstance)
                .filter(o -> o != this && Vector.between(location(), o.location()).sqrAbs() <= sqrDist)
                .map(o -> (A)o)
                .collect(Collectors.toSet());
    }






    // -----------------------------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------------------------






    /**
     * Adds the given action to be executes whenever the object gets added to a
     * different map.
     *
     * @param action The action to add
     * @return This object
     */
    public GameObject addOnAdd(Consumer<Map> action) {
        onAdd.add(Arguments.checkNull(action));
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
        Arguments.checkNull(action);
        return addOnAdd(m -> action.run());
    }

    /**
     * Removes the given action from those that will be executed whenever the object
     * gets added to a different map.
     * 
     * @param action The action to add
     * @return This object
     */
    public GameObject removeOnAdd(Consumer<Map> action) {
        onAdd.remove(action);
        return this;
    }

    public GameObject addOnRemove(Consumer<Map> action) {
        onRemove.add(Arguments.checkNull(action));
        return this;
    }

    public GameObject addOnRemove(Runnable action) {
        Arguments.checkNull(action);
        return addOnRemove(m -> action.run());
    }

    public GameObject removeOnRemove(Consumer<Map> action) {
        onRemove.remove(action);
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
    class SupportActor extends Actor {

        // false by default, gets assigned to true after super ctor call. MUST BE FINAL AND
        // ASSIGNED INSTANTLY to work, otherwise it has the value true right away.
        private final boolean initialized;
        {
            initialized = true;
        }

        boolean updateLocation = true;

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
            Image image = GameObject.this.getImage();
            return image != null ? Image.asGImage(image) : null;
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

            return (int)(GameObject.this.rotation() + 0.5);
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
            return (int)(location().x() + 0.5);
        }

        @Override
        public int getY() {
            return (int)(location().y() + 0.5);
        }

        @Override
        public int hashCode() {
            return GameObject.this.hashCode();
        }

        @Override
        protected boolean intersects(Actor other) {
            if(other == null) return false;
            ensureTransformUpToDate();
            if(other instanceof SupportActor)
                ((SupportActor) other).gameObject().ensureTransformUpToDate();
            return super.intersects(other);
        }

        @Override
        public boolean isAtEdge() {
            Vector location = location();
            return location.x() <= 0
                    || location.x() <= 0
                    || location.y() >= GameObject.this.getMap().getWidth() - 1
                    || location.y() >= GameObject.this.getMap().getHeight() - 1;
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
            // Later we assign our own default image.
            if(initialized) GameObject.this.setImage(Image.of(image));
        }

        void superSetImage(GreenfootImage image) {
            super.setImage(image);
        }

        @Override
        public void setImage(String fileName) throws IllegalArgumentException {
            if(initialized) GameObject.this.setImage(Image.load(fileName));
            super.setImage(getImage());
        }

        @Override
        public void setLocation(int x, int y) {
            if(updateLocation)
                GameObject.this.location().setX(x).setY(y);
        }

        void superSetLocation(int x, int y) {
            super.setLocation(x, y);
        }

        @Override
        public void setRotation(int rotation) {
            GameObject.this.setRotation(rotation);
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
            GameObject.this.turnTowards(Vector.of(x, y));
            setRotation(getRotation());
        }

        GameObject gameObject() {
            return GameObject.this;
        }
    }
}
