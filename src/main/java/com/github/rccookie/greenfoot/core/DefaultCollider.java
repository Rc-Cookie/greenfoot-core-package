package com.github.rccookie.greenfoot.core;

import com.github.rccookie.geometry.Border;
import com.github.rccookie.geometry.Edge;
import com.github.rccookie.geometry.Vector;
import com.github.rccookie.geometry.Vector2D;
import com.github.rccookie.greenfoot.core.components.Collider;
import com.github.rccookie.greenfoot.core.raycast.Raycast;
import com.github.rccookie.greenfoot.java.util.Optional;
import com.github.rccookie.util.Arguments;
import greenfoot.ActorVisitor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultCollider extends Collider {

    static {
        registerPrefab(DefaultCollider.class, DefaultCollider::new);
    }


    private EdgeCache cache;


    public DefaultCollider(GameObject gameObject) {
        super(gameObject);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <A> Set<A> findAllIntersecting(Class<A> cls) {
        Arguments.checkNull(cls);
        return gameObject.actor.getIntersectingObjects(GameObject.SupportActor.class)
                .stream()
                .map(GameObject.SupportActor::gameObject)
                .filter(cls::isInstance)
                .map(o -> (A)o)
                .collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> A findIntersecting(Class<A> cls) {
        Arguments.checkNull(cls);
        return gameObject.getMap().stream()
                .filter(obj -> cls.isInstance(obj) && obj.hasComponent(Collider.class))
                .filter(other -> intersects(other.getComponent(Collider.class)))
                .map(o -> (A)o)
                .findAny()
                .orElse(null);
    }

    @Override
    protected <A> Optional<A> tryFindIntersecting(Class<A> cls) {
        Arguments.checkNull(cls);
        return Optional.ofNullable(findIntersecting(cls));
    }

    @Override
    public boolean intersects(Collider other) {
        Arguments.checkNull(other);
        if(other instanceof DefaultCollider)
            return gameObject.actor.intersects(other.gameObject.actor);
        return false;
    }

    @Override
    public boolean contains(Vector point) {
        return ActorVisitor.containsPoint(gameObject.actor, (int) (point.x() + 0.5), (int) (point.y() + 0.5));
    }

    @Override
    public Bounds getBounds() {
        return null;
    }

    @Override
    public Border[] getBorders(Vector towards) {
        Vector size = gameObject.getSize();
        if(size.isZero()) return new Border[0];
        final double rotation = gameObject.rotation();
        final Vector loc = gameObject.location();

        EdgeCache cache;


        if(Raycast.THREADSAFE) {
            synchronized (this) {
                cache = this.cache;
                if(cacheChanged(size, rotation, cache))
                    this.cache = cache = calculateBorders(size, rotation, loc);
            }
        }
        else {
            cache = this.cache;
            if(cacheChanged(size, rotation, cache))
                this.cache = cache = calculateBorders(size, rotation, loc);
        }


        if(cache.edges.length == 2) {
            double angle = Vector2D.smallestAngle(Vector.between(towards, cache.edges[0].start).get2D(), cache.normals[0].get2D());
            if(angle > 90)
                return new Border[]{ cache.edges[0] };
            angle = Vector2D.smallestAngle(Vector.between(towards, cache.edges[1].start).get2D(), cache.normals[1].get2D());
            return angle > 90 ? new Border[] { cache.edges[1] } : new Border[0];
        }

        Edge first = null;
        for (int i = 0; i < 4; i++) {
            double angle = Vector2D.smallestAngle(Vector.between(towards, cache.edges[i].start).get2D(), cache.normals[i].get2D());
            if(angle <= 90) continue;
            if(first != null)
                return new Border[]{first, cache.edges[i]};
            first = cache.edges[i];
        }
        return first != null ? new Border[] { first } : new Border[0];
    }

    private boolean cacheChanged(Vector size, double rotation, EdgeCache cache) {
        return cache == null || !cache.location.equals(gameObject.location()) || rotation != cache.rotation || !Objects.equals(size, cache.size);
    }

    private EdgeCache calculateBorders(Vector size, double rotation, Vector loc) {
        Edge[] edges;
        Vector[] normals;
        if(size.x() == 0 || size.y() == 0) {
            Vector edge = Vector2D.angled(size.x() == 0 ? rotation + 90 : rotation, size.y());
            Vector normal = edge.get2D().rotated(-90);
            Vector end1 = edge.scaled(-0.5).add(loc), end2 = edge.scaled(0.5).add(loc);

            edges = new Edge[] {
                    new Edge(end1, end2),
                    new Edge(end2, end1)
            };
            normals = new Vector[]{normal, normal.inverted()};
        } else {
            Vector topLeft = size.scaled(-0.5).get2D().rotate(rotation), bottomRight = topLeft.inverted();
            Vector topRight = size.multiplied(Vector.of(0.5, -0.5)).get2D().rotate(rotation), bottomLeft = topRight.inverted();
            Vector width = topRight.subtracted(topLeft), widthI = width.inverted();
            Vector height = topRight.inverted().subtract(topLeft), heightI = height.inverted();

            topLeft.add(loc);
            topRight.add(loc);
            bottomLeft.add(loc);
            bottomRight.add(loc);

            edges = new Edge[] {
                    new Edge(topLeft, topRight),
                    new Edge(topRight, bottomRight),
                    new Edge(bottomRight, bottomLeft),
                    new Edge(bottomLeft, topLeft)
            };
            normals = new Vector[] { heightI, width, height, widthI };
        }
        return new EdgeCache(edges, normals, loc, rotation, size);
    }


    private static class EdgeCache {
        public final Edge[] edges;
        public final Vector[] normals;
        public final Vector location;
        public final double rotation;
        public final Vector size;

        private EdgeCache(Edge[] edges, Vector[] normals, Vector location, double rotation, Vector size) {
            this.edges = edges;
            this.normals = normals;
            this.location = location;
            this.size = size;
            this.rotation = rotation;
        }

        @Override
        public String toString() {
            return "EdgeCache{" +
                    "edges: " + Arrays.toString(edges) +
                    ", normals: " + Arrays.toString(normals) +
                    ", location: " + location +
                    ", rotation: " + rotation +
                    ", size: " + size +
                    '}';
        }
    }
}
