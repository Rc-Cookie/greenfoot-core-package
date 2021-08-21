package com.github.rccookie.greenfoot.core;

import com.github.rccookie.util.Arguments;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Listener extends Component {

    private static final Runnable NO_ACTION = () -> { };

    static {
        registerPrefab(Listener.class, (gameObject, arguments) -> {
            Arguments.checkNull(arguments, "arguments");
            if(arguments.length < 2) throw new IllegalArgumentException("Missing arguments");
            BooleanSupplier conditionSupplier = (BooleanSupplier) arguments[0];
            if(arguments[1] instanceof Consumer) {
                //noinspection unchecked
                return new Listener(gameObject, conditionSupplier,
                        (Consumer<Listener>) arguments[1],
                        (Consumer<Listener>) arguments[2],
                        (Consumer<Listener>) arguments[3],
                        (boolean) arguments[4]
                );
            }
            Runnable onTrueAction = (Runnable) arguments[1];
            if(arguments.length == 2) return new Listener(gameObject, conditionSupplier, onTrueAction);
            if(arguments[2] instanceof Boolean)
                return new Listener(gameObject, conditionSupplier, onTrueAction, (boolean) arguments[2]);

            Runnable onFalseAction = (Runnable) arguments[2];
            if(arguments.length == 3) return new Listener(gameObject, conditionSupplier, onTrueAction, onFalseAction);
            if(arguments[3] instanceof Boolean)
                return new Listener(gameObject, conditionSupplier, onTrueAction, onFalseAction, (boolean) arguments[3]);

            Runnable duringAction = (Runnable) arguments[3];
            return arguments.length == 4 ?
                    new Listener(gameObject, conditionSupplier, onTrueAction, onFalseAction, duringAction) :
                    new Listener(gameObject, conditionSupplier, onTrueAction, onFalseAction, duringAction, (boolean) arguments[4]);
        });
    }



    private final BooleanSupplier conditionSupplier;

    private final Runnable onTrueAction, onFalseAction, duringTrueAction;

    private boolean wasEnabled = false;



    public Listener(GameObject gameObject, BooleanSupplier conditionSupplier, Consumer<Listener> onTrueAction, Consumer<Listener> onFalseAction, Consumer<Listener> duringTrueAction, boolean active) {
        super(gameObject, true, false);
        this.conditionSupplier = Arguments.checkNull(conditionSupplier);
        this.onTrueAction = onTrueAction != null ? () -> onTrueAction.accept(this) : NO_ACTION;
        this.onFalseAction = onFalseAction != null ? () -> onFalseAction.accept(this) : NO_ACTION;
        this.duringTrueAction = duringTrueAction != null ? () -> duringTrueAction.accept(this) : NO_ACTION;
        if(active) wasEnabled = conditionSupplier.getAsBoolean();
    }

    public Listener(GameObject gameObject, BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction, Runnable duringTrueAction, boolean active) {
        super(gameObject, true, false);
        this.conditionSupplier = Arguments.checkNull(conditionSupplier);
        this.onTrueAction = onTrueAction != null ? onTrueAction : NO_ACTION;
        this.onFalseAction = onFalseAction != null ? onFalseAction : NO_ACTION;
        this.duringTrueAction = duringTrueAction != null ? duringTrueAction : NO_ACTION;
        if(active) wasEnabled = conditionSupplier.getAsBoolean();
    }

    public Listener(GameObject gameObject, BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction, Runnable duringTrueAction) {
        this(gameObject, conditionSupplier, onTrueAction, onFalseAction, duringTrueAction, true);
    }

    public Listener(GameObject gameObject, BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction, boolean active) {
        this(gameObject, conditionSupplier, onTrueAction, onFalseAction, null, active);
    }

    public Listener(GameObject gameObject, BooleanSupplier conditionSupplier, Runnable onTrueAction, Runnable onFalseAction) {
        this(gameObject, conditionSupplier, onTrueAction, onFalseAction, true);
    }

    public Listener(GameObject gameObject, BooleanSupplier conditionSupplier, Runnable action, boolean active) {
        this(gameObject, conditionSupplier, action, null, active);
    }

    public Listener(GameObject gameObject, BooleanSupplier conditionSupplier, Runnable action) {
        this(gameObject, conditionSupplier, action, true);
    }



    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(!enabled) wasEnabled = false;
    }

    public void disable(boolean fireFalseEvent) {
        if(wasEnabled && fireFalseEvent) onFalseAction.run();
        disable();
    }



    @Override
    public void update() {
        if(!isEnabled()) return;

        boolean active = conditionSupplier.getAsBoolean();
        if(active) {
            if(!wasEnabled) onTrueAction.run();
            duringTrueAction.run();
        }
        else if(wasEnabled) onFalseAction.run();
        wasEnabled = active;
    }



    public static Listener once(GameObject gameObject, BooleanSupplier conditionSupplier, Runnable oneTimeOnTrue) {
        return new Listener(gameObject, conditionSupplier, l -> {
            oneTimeOnTrue.run();
            l.disable();
        }, null, null, true);
    }
}
