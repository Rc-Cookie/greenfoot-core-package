package com.github.rccookie.greenfoot.core.raycast;

import com.github.rccookie.geometry.Ray;
import com.github.rccookie.geometry.*;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Raycast.Raycast2D;
import com.github.rccookie.greenfoot.core.Color;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.Image;
import com.github.rccookie.greenfoot.core.Map;
import com.github.rccookie.greenfoot.core.components.Collider;
import com.github.rccookie.util.Arguments;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The Raycast class allows to calculate raycasts. Raycasts are essentially lines
 * that have a source and a direction and an either discrete or infinite length.
 * The calculation of a raycast will calculate whether and if so where that line
 * will hit an object from a collection of objects that are checked for collision.
 * <p>By constructing an instance of a raycast the raycast is calculated instantly.
 * The instance of Raycast that is created will contain the result of the raycast
 * calculation.
 *
 * @author RcCookie
 */
@SuppressWarnings("Java8CollectionRemoveIf")
@Deprecated(forRemoval = true)
public final class OldRaycast {



    /**
     * Casts the given ray and checks for collisions with the given objects.
     * 
     * @param ray The ray to cast
     * @param objects The objects to consider
     */
    private OldRaycast(GameObject source, Ray ray, Collection<GameObject> objects, double maxDistance) {

        this.source = source;

        // Check for null
        Arguments.checkNull(ray, "ray");
        if(objects == null) {
            raw = Raycast2D.emptyResult(ray, maxDistance);
            gameObject = null;
        }
        else {

            // Load all edges connected to the object they belong to into the 'edges' map
            final HashMap<Border, GameObject> borders = new HashMap<>(objects.size());
            for (GameObject object : objects)
                for(Border border : object.tryGetComponent(Collider.class).map(c -> c.getBorders(ray.root)).orElse(new Border[0]))
                    borders.put(border, object);
            if(source.getMap().isBounded()) addMapEdgesTo(ray.root, source.getMap(), borders);

            // Letting the result be calculated
            raw = com.github.rccookie.geometry.Raycast.raycast2D(ray, maxDistance, borders.keySet());
            // Return the object that is associated with the edge that was hit. If the edge hit is null, so will be the object hit
            gameObject = borders.get(raw.hitBorder);
        }

        // Apply convenience information
        root = raw.root;
        location = raw.hitLoc;
        line = raw.connection;
        border = raw.hitBorder;
        edge = border instanceof Edge ? (Edge)border : null;
        circle = border instanceof Circle ? (Circle)border : null;
        this.ray = raw.ray;
        collided = border != null;
    }

    public OldRaycast(com.github.rccookie.geometry.Ray ray, Collection<GameObject> objects, double maxDistance, GameObject... ignore) {
        this(null, ray, removeIf(objects, OldRaycast::shouldIgnore), maxDistance);
    }

    public OldRaycast(com.github.rccookie.geometry.Ray ray, Collection<GameObject> objects, GameObject... ignore) {
        this(ray, objects, Double.POSITIVE_INFINITY, ignore);
    }


    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    private <C> OldRaycast(GameObject source, com.github.rccookie.geometry.Ray ray, Map map, Class<C> type, double maxDistance, Predicate<C> filter, C[] ignore) {
        // Check for null
        Arguments.checkNull(ray, "ray");
        Arguments.checkNull(map, "map");

        // Get objects
        Set<GameObject> objects;
        if(type != null)
            objects = map.findAll(type).stream().map(c -> (GameObject)c).collect(Collectors.toCollection(HashSet::new));
        else objects = new HashSet<>(map.findAll(GameObject.class));
        if(map.isBounded() && !shouldIgnore(map))
            objects.add(map);


        // Remove source and ignored objects
        objects.remove(source);
        if(ignore != null && ignore.length > 0)
            Arrays.asList(ignore).forEach(objects::remove);
        removeIf(objects, a -> shouldIgnore(a) || (a != map && (a.getImage() == null || !filter.test((C)a))));

        // Remove objects that are too far away
        if(Double.isFinite(maxDistance) && maxDistance > 0) {
            double sqrMaxDist = maxDistance * maxDistance;
            Vector loc = ray.root;
            removeIf(objects, a -> {
                int sqrImgSize = (a.getImage().getWidth() * a.getImage().getWidth() + a.getImage().getHeight() * a.getImage().getHeight()) / 4;
                double sqrDist = Vector.sqrDistance(a.location(), loc);
                return sqrDist + sqrImgSize - 2 * Math.sqrt(sqrDist * sqrImgSize) > sqrMaxDist;
            });
        }

        this.source = source;

        // Load all borders connected to the object they belong to into the 'borders' map
        final java.util.Map<Border, GameObject> borders = new IdentityHashMap<>(objects.size());
        for (GameObject object : objects)
            for(Border border : object.tryGetComponent(Collider.class).map(c -> c.getBorders(ray.root)).orElse(new Border[0]))
                borders.put(border, object);


        // Letting the result be calculated
        raw = com.github.rccookie.geometry.Raycast.raycast2D(ray, maxDistance, borders.keySet());
        // Return the object that is associated with the edge that was hit. If the edge hit is null, so will be the object hit
        gameObject = borders.get(raw.hitBorder);

        // Apply convenience information
        root = raw.root;
        location = raw.hitLoc;
        line = raw.connection;
        border = raw.hitBorder;
        edge = border instanceof Edge ? (Edge)border : null;
        circle = border instanceof Circle ? (Circle)border : null;
        this.ray = raw.ray;
        collided = border != null;
    }

    /**
     * Utility intermediate constructor.
     */
    private <C> OldRaycast(GameObject source, com.github.rccookie.geometry.Ray ray, Map map, Class<C> type, double maxDistance, C[] ignore) {
        this(source, ray, map, type, maxDistance, c -> true, ignore);
    }

    /**
     * Equivalent to {@link Collection#removeIf(Predicate)} because that method does not exist
     * online.
     *
     * @param collection The collection to remove objects from
     * @param filter Returns {@code true} for all items that should be removed
     * @param <T> The type of collection (i.e. List, Set...)
     * @param <C> The content type of the collection
     * @return The collection itself
     */
    private static <T, C extends Collection<T>> C removeIf(C collection, Predicate<T> filter) {
        // Collection.removeIf does not exist online.
        final Iterator<T> each = collection.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next()))
                each.remove();
        }
        return collection;
    }

    /**
     * Calculates a raycast for the given ray with the length {@code maxDistance} that only
     * considers objects that are from the given type.
     *
     * @param ray The ray to calculate a raycast for
     * @param map The map to calculate the raycast for, i.e. which objects to consider for collision
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(com.github.rccookie.geometry.Ray ray, Map map, Class<C> type, double maxDistance, C... ignore) {
        this(null, ray, map, type, maxDistance, ignore);
    }

    /**
     * Calculates a raycast for the given ray with the length {@code maxDistance} that only
     * considers objects that are from the given type and match the specified filter.
     *
     * @param ray The ray to calculate a raycast for
     * @param map The map to calculate the raycast for, i.e. which objects to consider for collision
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(com.github.rccookie.geometry.Ray ray, Map map, Class<C> type, double maxDistance, Predicate<C> filter, C... ignore) {
        this(null, ray, map, type, maxDistance, filter, ignore);
    }



    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from} and
     * facing the specified direction that only considers objects that are from the given
     * type and match the specified filter.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param direction The direction to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Vector direction, Class<C> type, double maxDistance, Predicate<C> filter, C... ignore) {
        this(from, new com.github.rccookie.geometry.Ray(from.location(), direction), from.getMap(), type, maxDistance, filter, ignore);
    }

    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from} and
     * facing the specified direction that only considers objects that are from the given type.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param direction The direction to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Vector direction, Class<C> type, double maxDistance, C... ignore) {
        this(from, new com.github.rccookie.geometry.Ray(from.location(), direction), from.getMap(), type, maxDistance, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the specified direction that
     * only considers objects that are from the given type.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param direction The direction to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Vector direction, Class<C> type, C... ignore) {
        this(from, direction, type, Double.POSITIVE_INFINITY, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the specified direction that
     * only considers objects that are from the given type and match the specified filter.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param direction The direction to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Vector direction, Class<C> type, Predicate<C> filter, C... ignore) {
        this(from, direction, type, Double.POSITIVE_INFINITY, filter, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the specified direction.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param direction The direction to shoot the ray in
     * @param ignore Objects to ignore
     */
    public OldRaycast(GameObject from, Vector direction, GameObject... ignore) {
        this(from, direction, null, ignore);
    }


    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from} and
     * facing the specified direction that only considers objects that are from the given type.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param angle The world angle to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, double angle, Class<C> type, double maxDistance, C... ignore) {
        this(from, Vector2D.angled(angle, 1), type, maxDistance, ignore);
    }

    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from} and
     * facing the specified direction that only considers objects that are from the given
     * type and match the specified filter.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param angle The world angle to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, double angle, Class<C> type, double maxDistance, Predicate<C> filter, C... ignore) {
        this(from, Vector2D.angled(angle, 1), type, maxDistance, filter, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the specified direction that
     * only considers objects that are from the given type.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param angle The world angle to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, double angle, Class<C> type, C... ignore) {
        this(from, angle, type, Double.POSITIVE_INFINITY, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the specified direction that
     * only considers objects that are from the given type and match the specified filter.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param angle The world angle to shoot the ray in
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, double angle, Class<C> type, Predicate<C> filter, C... ignore) {
        this(from, angle, type, Double.POSITIVE_INFINITY, filter, ignore);
    }

    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from}
     * and facing the direction it is facing that only considers objects that are from the
     * given type.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Class<C> type, double maxDistance, C... ignore) {
        this(Arguments.checkNull(from, "from"), from.rotation(), type, maxDistance, ignore);
    }

    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from}
     * and facing the direction it is facing that only considers objects that are from the
     * given type and match the specified filter.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Class<C> type, double maxDistance, Predicate<C> filter, C... ignore) {
        this(Arguments.checkNull(from, "from"), from.rotation(), type, maxDistance, filter, ignore);
    }

    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from}
     * and facing the direction it is facing.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param ignore Objects to ignore
     */
    public OldRaycast(GameObject from, double maxDistance, GameObject... ignore) {
        this(from, null, maxDistance, ignore);
    }

    /**
     * Calculates a raycast with the length {@code maxDistance} starting at {@code from}
     * and facing the direction it is facing that only considers objects that match the
     * specified filter and are not included in {@code ignore}.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param maxDistance The maximum distance the ray can have. If no object is hit in this
     *                    distance, no collision will be returned
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    public OldRaycast(GameObject from, double maxDistance, Predicate<GameObject> filter, GameObject... ignore) {
        this(from, null, maxDistance, filter, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the direction it is facing.
     * Only objects of the specified type will be considered.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Class<C> type, Predicate<C> filter, C... ignore) {
        this(from, type, Double.POSITIVE_INFINITY, filter, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the direction it is facing.
     * Only objects of the specified type will be considered.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param type The type of objects to consider for collision, {@code null} will consider
     *             all objects
     * @param ignore Objects to ignore
     */
    @SafeVarargs
    public <C> OldRaycast(GameObject from, Class<C> type, C... ignore) {
        this(from, type, Double.POSITIVE_INFINITY, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the direction it is facing.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param ignore Objects to ignore
     */
    public OldRaycast(GameObject from, GameObject... ignore) {
        this(from, (Class<GameObject>)null, ignore);
    }

    /**
     * Calculates a raycast starting at {@code from} and facing the direction it is facing
     * that only considers objects that match the specified filter.
     *
     * @param from The source of the ray, will not be considered for collision
     * @param filter The filter objects must pass to be considered for collision
     * @param ignore Objects to ignore
     */
    public OldRaycast(GameObject from, Predicate<GameObject> filter, GameObject... ignore) {
        this(from, null, filter, ignore);
    }





    private static boolean shouldIgnore(final GameObject object) {
        if(object == null) return true;
        return object.getClass().isAnnotationPresent(IgnoreOnRaycasts.class);
    }

    private static void addMapEdgesTo(Vector root, Map map, java.util.Map<Border, GameObject> edgeMap) {
        // The world bounds should be visible from the inside, so the walls have to be facing counterclockwise
        Vector topLeft = Vector.of(-0.5, -0.5),
                topRight = Vector.of(map.getWidth() - 0.5, -0.5),
                bottomLeft = Vector.of(-0.5, map.getHeight() - 0.5),
                bottomRight = Vector.of(map.getWidth() - 0.5, map.getHeight() - 0.5);
        if(root.x() <= map.getWidth() - 0.5)
            edgeMap.put(new Edge(bottomRight, topRight), map);
        if(root.x() >= 0.5)
            edgeMap.put(new Edge(topLeft, bottomLeft), map);
        if(root.y() <= map.getHeight() - 0.5)
            edgeMap.put(new Edge(bottomLeft, bottomRight), map);
        if(root.y() >= 0.5)
            edgeMap.put(new Edge(topRight, topLeft), map);
    }














    /**
     * The object that was hit by the ray. May be {@code null} if no object was
     * hit by the ray.
     */
    public final GameObject gameObject;

    /**
     * A line representing the complete ray from its root until the location of the hit.
     * May be {@code null} if the ray did not hit anything.
     */
    public final Edge line;

    /**
     * The border that was hit. May be {@code null} if the ray did not
     * hit anything.
     */
    public final Border border;

    /**
     * The edge that was hit. May be {@code null} if the ray did not
     * hit anything or the hit border was not an edge.
     */
    public final Edge edge;

    /**
     * The edge that was hit. May be {@code null} if the ray did not
     * hit anything or the hit border was not a circle.
     */
    public final Circle circle;

    /**
     * The ray that this raycast was based on.
     */
    public final Ray ray;

    /**
     * The length of the ray from its root until the point of intersection. If there was
     * no hit this has the value of {@link Double#POSITIVE_INFINITY}.
     */
    public double length() {
        return raw.length();
    }

    /**
     * The location of the intersection. May by {@code null} if the ray did not hit anything.
     */
    public final Vector location;

    /**
     * The root of the ray. Should never be {@code null}.
     */
    public final Vector root;

    /**
     * The object that is the source of this ray. May be {@code null} if the ray was not
     * specified to start from a specific object but from a certain location.
     */
    public final GameObject source;

    /**
     * Indicates weather the ray has hit anything. This is the same as {@code hit != null}.
     */
    public final boolean collided;

    /**
     * A {@link Raycast2D} containing the raw result of the raycast.
     */
    public final Raycast2D raw;



    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof OldRaycast)) return false;
        OldRaycast o = (OldRaycast)obj;
        return Objects.equals(gameObject, o.gameObject)
            && Objects.equals(source, o.source)
            && Objects.equals(raw, o.raw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw, source, gameObject);
    }

    @Override
    public String toString() {
        return "Raycast{Hit object: " + gameObject + ", ray: " + line + ", length: " + length() + "}";
    }


    /**
     * Draws this raycast onto the map of {@code source}. If the ray has an
     * infinite length it will not be drawn.
     *
     * @param color The color to draw the ray in
     */
    public void draw(Color color) {
        Arguments.checkNull(color);
        Objects.requireNonNull(source);
        draw(source.getMap(), color);
    }

    /**
     * Draws this raycast onto the specified map. If the ray has an infinite
     * length it will not be drawn.
     *
     * @param map The map to draw the raycast onto
     * @param color The color to draw the ray in
     */
    public void draw(Map map, Color color) {
        if(map == null) return;
        draw(map.getImage(), color);
    }

    /**
     * Draws this raycast onto the specified image. If the ray has an infinite
     * length it will not be drawn.
     *
     * @param image The image to draw the raycast onto
     * @param color The color to draw the ray in
     */
    public void draw(Image image, Color color) {
        if(image == null || color == null) return;
        if(length() == Double.POSITIVE_INFINITY) return;
        image.drawLine((int)root.x(), (int)root.y(), (int) location.x(), (int) location.y(), color);
    }
}
