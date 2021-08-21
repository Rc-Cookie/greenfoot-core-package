package com.github.rccookie.greenfoot.core.raycast;

import com.github.rccookie.geometry.Border;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Vector2D;
import com.github.rccookie.greenfoot.core.Core;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.Map;
import com.github.rccookie.greenfoot.core.components.Collider;
import com.github.rccookie.greenfoot.java.util.Collections;
import com.github.rccookie.util.Arguments;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class RaycastBuilder {

    private Vector origin = Vector.ZERO;
    private Vector direction = Vector.RIGHT;
    private double maxDistance = Double.POSITIVE_INFINITY;

    private GameObject gameObject = null;
    private Map map = Core.getMap();

    private Class<?> type = GameObject.class;
    private Collection<GameObject> objects = null;
    private Predicate<GameObject> filter = g -> true;
    private GameObject[] ignored = new GameObject[0];

    public RaycastBuilder setOrigin(Vector origin) {
        this.origin = Arguments.checkNull(origin);
        gameObject = null;
        return this;
    }

    public RaycastBuilder setDirection(Vector direction) {
        this.direction = Arguments.checkNull(direction);
        return this;
    }

    public RaycastBuilder setDirection(double angle) {
        return setDirection(Vector2D.angled(angle));
    }

    public RaycastBuilder setRay(com.github.rccookie.geometry.Ray ray) {
        return setOrigin(ray.root).setDirection(ray.direction);
    }

    public RaycastBuilder setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
        return this;
    }

    public RaycastBuilder setSource(GameObject source) {
        gameObject = source;
        if(gameObject != null) {
            map = null;
            origin = Objects.requireNonNull(gameObject.location());
            setDirection(gameObject.rotation());
        }
        else if(map == null) map = Core.getMap();
        return this;
    }

    public RaycastBuilder setMap(Map map) {
        this.map = map;
        if(map != null) gameObject = null;
        else if(gameObject == null && objects == null) this.map = Core.getMap();
        return this;
    }

    public RaycastBuilder setType(Class<?> type) {
        this.type = type != null ? type : GameObject.class;
        objects = null;
        return this;
    }

    public RaycastBuilder setObjects(Collection<GameObject> objects) {
        this.objects = Arguments.checkNull(objects);
        type = null;
        return this;
    }

    public RaycastBuilder setFilter(Predicate<GameObject> filter) {
        this.filter = filter != null ? filter : g -> true;
        return this;
    }

    public RaycastBuilder addFilter(Predicate<GameObject> filter) {
        Arguments.checkNull(filter);
        Predicate<GameObject> old = this.filter;
        return setFilter(g -> old.test(g) && filter.test(g));
    }

    public RaycastBuilder ignore(GameObject... objects) {
        ignored = objects != null ? objects : new GameObject[0];
        return this;
    }

    public Raycast calculate() {
        if(gameObject != null) {
            map = gameObject.getMap();
            Objects.requireNonNull(map, "The gameobject that is the source of the ray must be in a world");
        }

        // Get objects
        Set<GameObject> objects;
        if(this.objects != null)
            objects = new HashSet<>(this.objects);
        else
            objects = map.findAll(type).stream().map(c -> (GameObject)c).collect(Collectors.toCollection(HashSet::new));
        if(map.isBounded() && !shouldIgnore(map))
            objects.add(map);


        // Remove source and ignored objects
        objects.remove(gameObject);
        for(GameObject ignore : ignored)
            objects.remove(ignore);
        Collections.removeIf(objects, a -> shouldIgnore(a) || (a != map && (a.getImage() == null || !filter.test(a))));

        // Remove objects that are too far away
        if(Double.isFinite(maxDistance) && maxDistance > 0) {
            double sqrMaxDist = maxDistance * maxDistance;
            Collections.removeIf(objects, a -> {
                int sqrImgSize = (a.getImage().getWidth() * a.getImage().getWidth() + a.getImage().getHeight() * a.getImage().getHeight()) / 4;
                double sqrDist = Vector.sqrDistance(a.location(), origin);
                return sqrDist + sqrImgSize - 2 * Math.sqrt(sqrDist * sqrImgSize) > sqrMaxDist;
            });
        }

        // Load all borders connected to the object they belong to into the 'borders' map
        final java.util.Map<Border, GameObject> borders = new IdentityHashMap<>(objects.size());
        for (GameObject object : objects)
            for(Border border : object.tryGetComponent(Collider.class).map(c -> c.getBorders(origin)).orElse(new Border[0]))
                borders.put(border, object);


        // Letting the result be calculated
        com.github.rccookie.geometry.Raycast.Raycast2D raw = com.github.rccookie.geometry.Raycast.raycast2D(new com.github.rccookie.geometry.Ray(origin, direction), maxDistance, borders.keySet());
        // Return the object that is associated with the edge that was hit. If the edge hit is null, so will be the object hit
        gameObject = borders.get(raw.hitBorder);

        return new Raycast(
                borders.get(raw.hitBorder),
                gameObject,
                raw
        );
    }

    private static boolean shouldIgnore(final GameObject object) {
        if(object == null) return true;
        return object.getClass().isAnnotationPresent(IgnoreOnRaycasts.class);
    }
}
