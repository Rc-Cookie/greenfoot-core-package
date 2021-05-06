package com.github.rccookie.greenfoot.core;

import com.github.rccookie.geometry.Vector;
import com.github.rccookie.util.Console;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Animation implements Cloneable {

    private static final Consumer<GameObject> EMPTY_ON_END = o -> { };
    private static final Predicate<GameObject> EMPTY_PREDICATE = o -> true;

    private Vector location = null;
    private Vector movement;
    private Double angle = null;
    private Double rotation;
    private Integer width = null, height = null;
    private Double scaleX = null, scaleY = null;
    private Double transparency = null;
    private double duration = 1;

    private Consumer<GameObject> onEnd = EMPTY_ON_END;
    private Predicate<GameObject> predicate = EMPTY_PREDICATE;



    public Animation() { }

    @Override
    public Animation clone() {
        Animation clone = new Animation();
        clone.location = location;
        clone.movement = movement;
        clone.angle = angle;
        clone.rotation = rotation;
        clone.width = width;
        clone.height = height;
        clone.scaleX = scaleX;
        clone.scaleY = scaleY;
        clone.transparency = transparency;
        clone.duration = duration;
        clone.onEnd = onEnd;
        clone.predicate = predicate;
        return clone;
    }

    public Animation setLocation(Vector location) {
        if(this.location != null || movement != null) throw new IllegalStateException();
        this.location = Objects.requireNonNull(location);
        return this;
    }

    public Animation setMovement(Vector movement) {
        if(location != null || this.movement != null) throw new IllegalStateException();
        this.movement = Objects.requireNonNull(movement);
        return this;
    }

    public Animation setAngle(double angle) {
        if(this.angle != null || rotation != null) throw new IllegalStateException();
        this.angle = angle;
        return this;
    }

    public Animation setRotation(double rotation) {
        if(angle != null || this.rotation != null) throw new IllegalStateException();
        this.rotation = rotation;
        return this;
    }

    public Animation setDimension(int width, int height) {
        if(this.width != null || scaleX != null) throw new IllegalStateException();
        if(width <= 0 || height <= 0) throw new IllegalArgumentException();
        this.width = width;
        this.height = height;
        return this;
    }

    public Animation setScale(double x, double y) {
        if(width != null || scaleX != null) throw new IllegalStateException();
        if(x <= 0 || y <= 0) throw new IllegalArgumentException();
        scaleX = x;
        scaleY = y;
        return this;
    }

    public Animation setScale(double scale) {
        return setScale(scale, scale);
    }

    public Animation setTransparency(double transparency) {
        this.transparency = transparency;
        return this;
    }

    public Animation setDuration(double duration) {
        if(duration < 0) throw new IllegalArgumentException();
        this.duration = duration;
        return this;
    }

    public Animation addOnEnd(Consumer<GameObject> action) {
        onEnd = onEnd.andThen(Objects.requireNonNull(action));
        return this;
    }

    public Animation addPredicate(Predicate<GameObject> requirement) {
        predicate = predicate.and(Objects.requireNonNull(requirement));
        return this;
    }



    Runnable build(GameObject object) {
        Vector movement = location != null ? Vector.between(object.getLocation(), location) : (this.movement != null ? this.movement : Vector.of());
        double rotation = angle != null ? angle - object.getAngle() : (this.rotation != null ? this.rotation : 0);

        final boolean isScaled = object.getImage() != null && (width != null || scaleX != null);
        int widthChange = isScaled ? (width != null ? width - object.getWidth() : (int)(object.getWidth() * (scaleX - 1))) : 0;
        int heightChange = isScaled ? (height != null ? height - object.getHeight() : (int)(object.getHeight() * (scaleY - 1))) : 0;
        int initialWidth = isScaled ? object.getWidth() : 0, initialHeight = isScaled ? object.getHeight() : 0;

        Console.mapDebug("Starting animation {} with", object.getImage(), hashCode());
        if(isScaled && heightChange != 4 && heightChange != -4)
            Console.line("error");
            //throw new RuntimeException("Width change: " + widthChange + ", height change: " + heightChange + "(x scale: " + scaleX + ", y scale: " + scaleY + ", initial x: " + initialWidth + ", initial y: " + initialHeight + ")");

        Double transparencyChange = (transparency != null && object.getImage() != null) ? 255 * transparency - object.getImage().getTransparency() : null;
        double duration = this.duration;

        Consumer<GameObject> onEnd = this.onEnd;
        Predicate<GameObject> predicate = this.predicate;

        return new Runnable() {

            double total = 0;

            double currentWidth = isScaled ? object.getWidth() : 0;
            double currentHeight = isScaled ? object.getHeight() : 0;
            double currentTransparency = transparencyChange != null ? object.getImage().getTransparency() : 0;

            @Override
            public void run() {
                double newTotal = duration == 0 ? 1 : Math.min(total + object.time.deltaTime() / duration, 1);
                double delta = newTotal - total;
                total = newTotal;

                object.move(movement.scaled(delta));
                object.turn(rotation * delta);

                if(isScaled) {
                    double newWidth = currentWidth + widthChange * delta;
                    double newHeight = currentHeight + heightChange * delta;

                    if(total == 1)
                        object.getImage().scale(initialWidth + widthChange, initialHeight + heightChange);
                    else if((int)newWidth != (int)currentWidth || (int)newHeight != currentHeight)
                        object.getImage().scale((int) (newWidth + 0.49), (int) (newHeight + 0.49));

                    currentWidth = newWidth;
                    currentHeight = newHeight;
                    Console.map("Current dimensions", currentWidth, currentHeight);
                }

                if(transparencyChange != null) {
                    currentTransparency += transparencyChange * delta;
                    if(currentTransparency < 0) currentTransparency = 0;
                    else if(currentTransparency > 255) currentTransparency = 255;
                    object.getImage().setTransparency((int)(currentTransparency + 0.49)); // 0.5 may round up to 256
                }

                if(total == 1 || !predicate.test(object)) {
                    Console.mapDebug("Ending animation {} with", object.getImage(), Animation.this.hashCode());
                    object.removeOnUpdate(this);
                    onEnd.accept(object);
                }
            }
        };
    }
}
