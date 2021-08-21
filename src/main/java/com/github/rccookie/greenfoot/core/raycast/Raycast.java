package com.github.rccookie.greenfoot.core.raycast;

import com.github.rccookie.geometry.Ray;
import com.github.rccookie.geometry.Raycast.Raycast2D;
import com.github.rccookie.geometry.*;
import com.github.rccookie.greenfoot.core.Color;
import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.Image;
import com.github.rccookie.greenfoot.core.Map;
import com.github.rccookie.util.Arguments;

import java.util.Objects;

public final class Raycast {

    public static boolean THREADSAFE = false;

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



    Raycast(GameObject gameObject, GameObject source, Raycast2D raw) {
        this.gameObject = gameObject;
        this.source = source;
        this.raw = raw;
        border = raw.hitBorder;
        edge = border instanceof Edge ? (Edge) border : null;
        circle = border instanceof Circle ? (Circle) border : null;
        collided = gameObject != null;
        location = raw.hitLoc;
        ray = raw.ray;
        root = ray.root;
        line = collided ? new Edge(root, location) : null;
    }



    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Raycast)) return false;
        Raycast o = (Raycast)obj;
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
        return "Raycast{Object: " + gameObject + ", ray: " + line + ", length: " + length() + "}";
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


    /**
     * Creates and returns a new raycast builder.
     *
     * @return A new raycast builder. Never {@code null}.
     */
    public static RaycastBuilder builder() {
        return new RaycastBuilder();
    }
}
