package com.github.rccookie.greenfoot.core;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import com.github.rccookie.util.Updateable;

public class Listener implements Updateable {

    private static final Runnable NO_ACTION = () -> { };



    private final BooleanSupplier conditionSupplier;

    private final Runnable onTrueAction, onFalseAction, duringTrueAction;

    private boolean wasActive = false;

    private boolean active = true;



    public Listener(BooleanSupplier conditionSupplier, Consumer<Listener> onTrueAction, Consumer<Listener> onFalseAction, Consumer<Listener> duringTrueAction, boolean active) {
        this.conditionSupplier = Objects.requireNonNull(conditionSupplier);
        this.onTrueAction = onTrueAction != null ? () -> onTrueAction.accept(this) : NO_ACTION;
        this.onFalseAction = onFalseAction != null ? () -> onFalseAction.accept(this) : NO_ACTION;
        this.duringTrueAction = duringTrueAction != null ? () -> duringTrueAction.accept(this) : NO_ACTION;
        this.active = active;
        if(active) wasActive = conditionSupplier.getAsBoolean();
    }

    public Listener(BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction, Runnable duringTrueAction, boolean active) {
        this.conditionSupplier = Objects.requireNonNull(conditionSupplier);
        this.onTrueAction = onTrueAction != null ? onTrueAction : NO_ACTION;
        this.onFalseAction = onFalseAction != null ? onFalseAction : NO_ACTION;
        this.duringTrueAction = duringTrueAction != null ? duringTrueAction : NO_ACTION;
        this.active = active;
        if(active) wasActive = conditionSupplier.getAsBoolean();
    }

    public Listener(BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction, Runnable duringTrueAction) {
        this(conditionSupplier, onTrueAction, onFalseAction, true);
    }

    public Listener(BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction, boolean active) {
        this(conditionSupplier, onTrueAction, onFalseAction, null, active);
    }

    public Listener(BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction) {
        this(conditionSupplier, onTrueAction, onFalseAction, true);
    }

    public Listener(BooleanSupplier conditionSupplier, Runnable action, boolean active) {
        this(conditionSupplier, action, null, active);
    }

    public Listener(BooleanSupplier conditionSupplier, Runnable action) {
        this(conditionSupplier, action, true);
    }



    public void setActive(boolean active) {
        this.active = active;
        if(!active) wasActive = false;
    }

    public Listener deactivate() {
        setActive(false);
        return this;
    }

    public void deactivate(boolean fireFalseEvent) {
        if(wasActive && fireFalseEvent) onFalseAction.run();
        deactivate();
    }

    public boolean isActive() {
        return active;
    }



    @Override
    public void update() {
        if(!isActive()) return;

        boolean active = conditionSupplier.getAsBoolean();
        if(active) {
            if(!wasActive) onTrueAction.run();
            duringTrueAction.run();
        }
        if(wasActive) onFalseAction.run();
        wasActive = active;
    }



    public static Listener once(BooleanSupplier conditionSupplier, Runnable oneTimeOnTrue) {
        return new Listener(conditionSupplier, l -> {
            oneTimeOnTrue.run();
            l.deactivate();
        }, null, null, true);
    }
}
