package com.github.rccookie.greenfoot.core;

import com.github.rccookie.geometry.Vector;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Animation extends Component {

    static {
        registerPrefab(Animation.class, Animation::new);
    }


    // -- Relevant for animation setup --


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


    // -- Relevant while animation is running --


    Vector runningMovement;
    double runningRotation;

    boolean runningIsScaled;
    int runningWidthChange, runningHeightChange;
    int runningInitialWidth, runningInitialHeight;

    Double runningTransparencyChange;

    double runningCurrentWidth;
    double runningCurrentHeight;
    double RunningCurrentTransparency;

    double runningTotal = 0;




    // -- Relevant for animation setup --


    public Animation(GameObject gameObject) {
        super(gameObject);
    }

    public Animation copy(GameObject gameObject) {
        Animation clone = new Animation(gameObject);
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
        this.location = Arguments.checkNull(location);
        return this;
    }

    public Animation setMovement(Vector movement) {
        if(location != null || this.movement != null) throw new IllegalStateException();
        this.movement = Arguments.checkNull(movement);
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
        onEnd = onEnd.andThen(Arguments.checkNull(action));
        return this;
    }

    public Animation addPredicate(Predicate<GameObject> requirement) {
        predicate = predicate.and(Arguments.checkNull(requirement));
        return this;
    }


    // -- Relevant while animation is running --


    @Override
    public void start() {
        runningMovement = location != null ? Vector.between(gameObject.location(), location) : (Animation.this.movement != null ? Animation.this.movement : Vector.of());
        runningRotation = angle != null ? angle - gameObject.rotation() : (Animation.this.rotation != null ? Animation.this.rotation : 0);

        runningIsScaled = gameObject.getImage() != null && (width != null || scaleX != null);
        runningWidthChange = runningIsScaled ? (width != null ? width - gameObject.getWidth() : (int)(gameObject.getWidth() * (scaleX - 1))) : 0;
        runningHeightChange = runningIsScaled ? (height != null ? height - gameObject.getHeight() : (int)(gameObject.getHeight() * (scaleY - 1))) : 0;
        runningInitialWidth = runningIsScaled ? gameObject.getWidth() : 0;
        runningInitialHeight = runningIsScaled ? gameObject.getHeight() : 0;

        Console.mapDebug("Starting animation {} with", gameObject.getImage(), hashCode());
        if(runningIsScaled && runningHeightChange != 4 && runningHeightChange != -4)
            Console.line("error");
        //throw new RuntimeException("Width change: " + widthChange + ", height change: " + heightChange + "(x scale: " + scaleX + ", y scale: " + scaleY + ", initial x: " + initialWidth + ", initial y: " + initialHeight + ")");

        runningTransparencyChange = (transparency != null && gameObject.getImage() != null) ? 255 * transparency - gameObject.getImage().getTransparency() : null;


        runningCurrentWidth = runningIsScaled ? gameObject.getWidth() : 0;
        runningCurrentHeight = runningIsScaled ? gameObject.getHeight() : 0;
        RunningCurrentTransparency = runningTransparencyChange != null ? gameObject.getImage().getTransparency() : 0;
    }



    @Override
    public void update() {
        double newTotal = duration == 0 ? 1 : Math.min(runningTotal + Time.deltaTime() / duration, 1);
        double delta = newTotal - runningTotal;
        runningTotal = newTotal;

        gameObject.location().add(runningMovement.scaled(delta));
        gameObject.turn(runningRotation * delta);

        if(runningIsScaled) {
            double newWidth = runningCurrentWidth + runningWidthChange * delta;
            double newHeight = runningCurrentHeight + runningHeightChange * delta;

            if(runningTotal == 1)
                gameObject.getImage().scale(runningInitialWidth + runningWidthChange, runningInitialHeight + runningHeightChange);
            else if((int)newWidth != (int) runningCurrentWidth || (int)newHeight != runningCurrentHeight)
                gameObject.getImage().scale((int) (newWidth + 0.49), (int) (newHeight + 0.49));

            runningCurrentWidth = newWidth;
            runningCurrentHeight = newHeight;
            Console.map("Current dimensions", runningCurrentWidth, runningCurrentHeight);
        }

        if(runningTransparencyChange != null) {
            RunningCurrentTransparency += runningTransparencyChange * delta;
            if(RunningCurrentTransparency < 0) RunningCurrentTransparency = 0;
            else if(RunningCurrentTransparency > 255) RunningCurrentTransparency = 255;
            gameObject.getImage().setTransparency((int)(RunningCurrentTransparency + 0.49)); // 0.5 may round up to 256
        }

        if(runningTotal == 1 || !predicate.test(gameObject)) {
            Console.mapDebug("Ending animation {} with", gameObject.getImage(), Animation.this.hashCode());
            setEnabled(false);
            onEnd.accept(gameObject);
        }
    }
}
