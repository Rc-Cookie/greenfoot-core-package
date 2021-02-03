package com.github.rccookie.greenfoot.game.raycast;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;
import greenfoot.World;
import com.github.rccookie.greenfoot.game.raycast.Raycast;
import com.github.rccookie.common.geometry.Ray2D;
import com.github.rccookie.common.geometry.Transform2D;
import com.github.rccookie.common.geometry.Edge2D;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.common.geometry.Raycast.Raycast2D;
import com.github.rccookie.greenfoot.core.CoreActor;

/**
 * A class that contains some methods raycasting in Greenfoot.
 */
public final class Raycast {


    private static final HashMap<Actor, ImageCache> IMAGE_CACHE = new HashMap<>();



    /**
     * Casts the given ray and checks for collisions with the given actors.
     * 
     * @param ray The ray to cast
     * @param actors The actors to consider
     * @return A raycast result representing the calcutations of the raycast
     */
    private static final Raycast raycast(Actor source, Ray2D ray, Collection<Actor> actors, double maxDistance) {

        // Check for null
        Objects.requireNonNull(ray, "The ray to cast must not be null");
        if(actors == null) return new Raycast(Raycast2D.emptyResult(ray, maxDistance), source, null);

        // Load all edges connected to the actor they belong to into the 'edges' map
        final HashMap<Edge2D, Actor> edges = new HashMap<>(actors.size());
        for(Actor actor : actors) {
            addEdgesTo(ray.root(), actor, edges);
        }

        // Letting the result be calculated
        Raycast2D result = com.github.rccookie.common.geometry.Raycast.raycast2D(ray, maxDistance, edges.keySet());

        // Return the actor that is accisiated with the edge that was hit. If the edge hit is null, so will be the actor hit
        return new Raycast(result, source, edges.get(result.hitEdge));
    }

    public static final Raycast raycast(Ray2D ray, Collection<Actor> actors, double maxDistance, Actor... ignore) {
        actors.removeIf(a -> ignore(a));
        return raycast(null, ray, actors, maxDistance);
    }


    @SuppressWarnings("unchecked")
    private static final <C> Raycast raycast(Actor source, Ray2D ray, World world, Class<C> clazz, double maxDistance, Predicate<C> filter, C[] ignore) {
        // Check for null
        Objects.requireNonNull(ray, "The ray to cast must not be null");
        if(world == null) return null;

        // Get objects
        List<Actor> actors;
        if(clazz != null) actors = world.getObjects(clazz).stream().map(c -> (Actor)c).collect(Collectors.toList());
        else actors = world.getObjects(Actor.class);

        // Remove source and ignored objects
        actors.remove(source);
        if(ignore != null && ignore.length > 0) actors.removeAll(Arrays.asList(ignore));
        removeIf(actors, a -> ignore(a) || a.getImage() == null || !filter.test((C)a));

        // Remove objects that are too far away
        if(Double.isFinite(maxDistance) && maxDistance > 0) {
            double sqrMaxDist = maxDistance * maxDistance;
            Vector2D loc = ray.root();
            removeIf(actors, a -> {
                int sqrImgSize = (a.getImage().getWidth() * a.getImage().getWidth() + a.getImage().getHeight() * a.getImage().getHeight()) / 4;
                double dx = getX(a) - loc.x(), dy = getY(a) - loc.y();
                double sqrDist = dx * dx + dy * dy;
                return sqrDist + sqrImgSize - 2 * Math.sqrt(sqrDist * sqrImgSize) > sqrMaxDist;
            });
        }

        // Pass into next subfunction
        return raycast(source, ray, actors, maxDistance);
    }

    private static final <C> Raycast raycast(Actor source, Ray2D ray, World world, Class<C> clazz, double maxDistance, C[] ignore) {
        return raycast(source, ray, world, clazz, maxDistance, c -> true, ignore);
    }

    private static final <T> void removeIf(List<T> list, Predicate<T> filter) {
        final Iterator<T> each = list.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
            }
        }
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Ray2D ray, World world, Class<C> clazz, double maxDistance, C... ignore) {
        return raycast(null, ray, world, clazz, maxDistance, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Ray2D ray, World world, Class<C> clazz, double maxDistance, Predicate<C> filter, C... ignore) {
        return raycast(null, ray, world, clazz, maxDistance, filter, ignore);
    }



    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Vector2D direction, Class<C> clazz, double maxDistance, C... ignore) {
        return raycast(from, new Ray2D(new Vector2D(getX(from), getY(from)), direction), from.getWorld(), clazz, maxDistance, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Vector2D direction, Class<C> clazz, double maxDistance, Predicate<C> filter, C... ignore) {
        return raycast(from, new Ray2D(new Vector2D(getX(from), getY(from)), direction), from.getWorld(), clazz, maxDistance, filter, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Vector2D direction, Class<C> clazz, C... ignore) {
        return raycast(from, direction, clazz, Double.POSITIVE_INFINITY, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Vector2D direction, Class<C> clazz, Predicate<C> filter, C... ignore) {
        return raycast(from, direction, clazz, Double.POSITIVE_INFINITY, filter, ignore);
    }

    public static final Raycast raycast(Actor from, Vector2D direction, Actor... ignore) {
        return raycast(from, direction, null, ignore);
    }


    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, double angle, Class<C> clazz, double maxDistance, C... ignore) {
        return raycast(from, Vector2D.angledVector(angle, 1), clazz, maxDistance, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, double angle, Class<C> clazz, double maxDistance, Predicate<C> filter, C... ignore) {
        return raycast(from, Vector2D.angledVector(angle, 1), clazz, maxDistance, filter, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, double angle, Class<C> clazz, C... ignore) {
        return raycast(from, angle, clazz, Double.POSITIVE_INFINITY, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, double angle, Class<C> clazz, Predicate<C> filter, C... ignore) {
        return raycast(from, angle, clazz, Double.POSITIVE_INFINITY, filter, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Class<C> clazz, double maxDistance, C... ignore) {
        if(from == null) return null;
        double angle = from instanceof CoreActor ? ((CoreActor)from).getAngle() : from.getRotation();
        return raycast(from, angle, clazz, maxDistance, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Class<C> clazz, double maxDistance, Predicate<C> filter, C... ignore) {
        if(from == null) return null;
        double angle = from instanceof CoreActor ? ((CoreActor)from).getAngle() : from.getRotation();
        return raycast(from, angle, clazz, maxDistance, filter, ignore);
    }

    public static final Raycast raycast(Actor from, double maxDistance, Actor... ignore) {
        return raycast(from, null, maxDistance, ignore);
    }

    @SafeVarargs
    public static final Raycast raycast(Actor from, double maxDistance, Predicate<Actor> filter, Actor... ignore) {
        return raycast(from, null, maxDistance, filter, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Class<C> clazz, Predicate<C> filter, C... ignore) {
        return raycast(from, clazz, Double.POSITIVE_INFINITY, filter, ignore);
    }

    @SafeVarargs
    public static final <C> Raycast raycast(Actor from, Class<C> clazz, C... ignore) {
        return raycast(from, clazz, Double.POSITIVE_INFINITY, ignore);
    }

    public static final Raycast raycast(Actor from, Actor... ignore) {
        return raycast(from, (Class<Actor>)null, ignore);
    }

    public static final Raycast raycast(Actor from, Predicate<Actor> filter, Actor... ignore) {
        return raycast(from, (Class<Actor>)null, filter, ignore);
    }





    private static final boolean ignore(final Actor actor) {
        if(actor == null) return true;
        return actor.getClass().isAnnotationPresent(IgnoreOnRaycasts.class);
    }

    private static final double getX(Actor a) {
        if(a instanceof CoreActor) return ((CoreActor)a).x();
        return a.getX();
    }

    private static final double getY(Actor a) {
        if(a instanceof CoreActor) return ((CoreActor)a).y();
        return a.getY();
    }





    private static final void addEdgesTo(Vector2D root, Actor actor, Map<Edge2D, Actor> map) {
        GreenfootImage image = actor.getImage();
        if(image == null) return;
        ImageCache cache = IMAGE_CACHE.get(actor);

        if(cache == null || getX(actor) != cache.transform.location.x() || getY(actor) != cache.transform.location.y() || actor.getRotation() != cache.transform.rotation || image.getWidth() != cache.width || image.getHeight() != cache.height) {

            final Vector2D loc = new Vector2D(getX(actor), getY(actor));
            final int hw = image.getWidth() / 2, hh = image.getHeight() / 2;

            if(image.getWidth() == 1) {
                Edge2D edge = new Edge2D(
                    Vector2D.angledVector(actor.getRotation() + 90, -hh).add(loc),
                    Vector2D.angledVector(actor.getRotation() + 90, image.getHeight())
                );
                cache = new ImageCache(new EdgeCache[] {new EdgeCache(edge, null)}, actor);
            }
            else if(image.getHeight() == 1) {
                Edge2D edge = new Edge2D(
                    Vector2D.angledVector(actor.getRotation(), -hw).add(loc),
                    Vector2D.angledVector(actor.getRotation(), image.getWidth())
                );
                cache = new ImageCache(new EdgeCache[] {new EdgeCache(edge, null)}, actor);
            }
            else {
                EdgeCache[] edges = new EdgeCache[4];
                Edge2D currentEdge;

                currentEdge = new Edge2D(
                    new Vector2D(-hw, -hh).rotate(actor.getRotation()).add(loc),
                    new Vector2D(image.getWidth(), 0).rotate(actor.getRotation())
                );
                edges[0] = new EdgeCache(currentEdge, currentEdge.edge().rotated(-90));

                currentEdge = new Edge2D(
                    new Vector2D(hw, -hh).rotate(actor.getRotation()).add(loc),
                    new Vector2D(0, image.getHeight()).rotate(actor.getRotation())
                );
                edges[1] = new EdgeCache(currentEdge, currentEdge.edge().rotated(-90));

                currentEdge = new Edge2D(
                    new Vector2D(hw, hh).rotate(actor.getRotation()).add(loc),
                    new Vector2D(-image.getWidth(), 0).rotate(actor.getRotation())
                );
                edges[2] = new EdgeCache(currentEdge, currentEdge.edge().rotated(-90));

                currentEdge = new Edge2D(
                    new Vector2D(-hw, hh).rotate(actor.getRotation()).add(loc),
                    new Vector2D(0, -image.getHeight()).rotate(actor.getRotation())
                );
                edges[3] = new EdgeCache(currentEdge, currentEdge.edge().rotated(-90));

                cache = new ImageCache(edges, actor);
            }

            IMAGE_CACHE.put(actor, cache);
        }


        if(cache.edges.length == 1) {
            map.put(cache.edges[0].edge, actor);
            return;
        }

        int count = 0;
        for(int i=0; i<4; i++) {
            double angle = Vector2D.smallestAngle(Vector2D.between(root, cache.edges[i].edge.root()), cache.edges[i].normal);
            if(angle <= 90) continue;
            map.put(cache.edges[i].edge, actor);
            if(++count == 2) return;
        }
    }






    private static final class EdgeCache {
        public final Edge2D edge;
        public final Vector2D normal;

        public EdgeCache(Edge2D edge, Vector2D normal) {
            this.edge = edge;
            this.normal = normal;
        }
    }

    private static final class ImageCache {
        public final EdgeCache[] edges;
        public final Transform2D transform;
        public final int width, height;

        public ImageCache(EdgeCache[] edges, Actor actor) {
            this.edges = edges;
            transform = new Transform2D(
                new Vector2D(getX(actor), getY(actor)),
                actor.getRotation()
            );
            width = actor.getImage().getWidth();
            height = actor.getImage().getHeight();
        }
    }
















    public static final Vector2D[] getCorners(Actor...actors) {
        ArrayList<VectorCache> corners = new ArrayList<>();
        for(Actor actor : actors) {
            if(actor.getImage() == null) continue;
            Vector2D loc = new Vector2D(getX(actor), getY(actor));
            if(actor.getImage().getWidth() == 1) {
                if(actor.getImage().getHeight() == 1) {
                    corners.add(new VectorCache(loc));
                    continue;
                }
                corners.add(new VectorCache(new Vector2D(0, -actor.getImage().getHeight() / 2).rotate(actor.getRotation()).add(loc)));
                corners.add(new VectorCache(new Vector2D(0, actor.getImage().getHeight() / 2).rotate(actor.getRotation()).add(loc)));
                continue;
            }
            if(actor.getImage().getHeight() == 1) {
                corners.add(new VectorCache(new Vector2D(-actor.getImage().getWidth() / 2).rotate(actor.getRotation()).add(loc)));
                corners.add(new VectorCache(new Vector2D(actor.getImage().getWidth() / 2).rotate(actor.getRotation()).add(loc)));
                continue;
            }
            corners.add(new VectorCache(new Vector2D(-actor.getImage().getWidth() / 2, -actor.getImage().getHeight() / 2).rotate(actor.getRotation()).add(loc)));
            corners.add(new VectorCache(new Vector2D(actor.getImage().getWidth() / 2, -actor.getImage().getHeight() / 2).rotate(actor.getRotation()).add(loc)));
            corners.add(new VectorCache(new Vector2D(actor.getImage().getWidth() / 2, actor.getImage().getHeight() / 2).rotate(actor.getRotation()).add(loc)));
            corners.add(new VectorCache(new Vector2D(-actor.getImage().getWidth() / 2, actor.getImage().getHeight() / 2).rotate(actor.getRotation()).add(loc)));
        }
        corners.sort((a, b) -> {
            if(a.angle < b.angle) return -1;
            if(a.angle > b.angle) return 1;
            return 0;
        });
        for(int i=1; i<corners.size(); i++) if(corners.get(i).vector.equals(corners.get(i-1).vector)) corners.remove(i);
        Vector2D[] array = new Vector2D[corners.size()];
        for(int i=0; i<array.length; i++) array[i] = corners.get(i).vector;
        return array;
    }



    private static final class VectorCache {
        public final Vector2D vector;
        public final double angle;
        public VectorCache(Vector2D vector) {
            this.vector = vector;
            angle = vector.angle();
        }
    }








    /**
     * Indicates that a class should always be ignored on raycasts.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface IgnoreOnRaycasts { }







    /**
     * The actor that was hit by the ray. May be {@code null} if no actor was
     * hit by the ray.
     */
    public final Actor hit;

    /**
     * A line representing the complete ray from its root until the location of the hit.
     * May be {@code null} if the ray did not hit anything.
     */
    public final Edge2D connection;

    /**
     * The ray that this raycast was based on.
     */
    public final Ray2D ray;

    /**
     * The length of the ray from its root until the point of intersection. If there was
     * no hit this has the value of {@link Double#POSITIVE_INFINITY}.
     */
    public final double length;

    /**
     * The location of the intersection. May by {@code null} if the ray did not hit anything.
     */
    public final Vector2D hitLoc;

    /**
     * The root of the ray. Shound never be {@code null}.
     */
    public final Vector2D root;

    /**
     * The actor that is the source of this ray. May be {@code null} if the ray was not
     * specified to start from a specific actor but from a certail location.
     */
    public final Actor source;

    /**
     * Indicates weather the ray has hit anything. This is the same as {@code hit != null}.
     */
    public final boolean collided;

    /**
     * A {@link Raycast#RaycastResult2D} containing more detail about the result of the raycast.
     * <p>This may be only {@code null} if the collection of actors passed into the raycasting
     * function were {@code null}.
     */
    public final Raycast2D result;

    private Raycast(Raycast2D result, Actor source, Actor hit) {
        this.hit = hit;
        this.source = source;
        this.result = result;

        root = result.root;
        hitLoc = result.hitLoc;
        connection = result.connection;
        ray = result.ray;
        length = result.length;
        collided = hit != null;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Raycast)) return false;
        Raycast o = (Raycast)obj;
        return Objects.equals(hit, o.hit) && Objects.equals(result, result);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "Raycast{Hit object: " + hit + ", ray: " + connection + ", length: " + length + "}";
    }


    public void draw(Color color) {
        Objects.requireNonNull(source);
        draw(source.getWorld(), color);
    }
    
    public void draw(World world, Color color) {
        if(world == null) return;
        draw(world.getBackground(), color);
    }

    public void draw(GreenfootImage image, Color color) {
        if(image == null || color == null) return;
        if(length == Double.POSITIVE_INFINITY) return;
        image.setColor(color);
        image.drawLine((int)root.x(), (int)root.y(), (int)hitLoc.x(), (int)hitLoc.y());
    }
}
