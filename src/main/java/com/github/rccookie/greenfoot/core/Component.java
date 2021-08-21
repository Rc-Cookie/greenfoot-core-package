package com.github.rccookie.greenfoot.core;

import com.github.rccookie.util.Arguments;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Component {

    static {
        Core.initialize();
    }

    private static final java.util.Map<Class<? extends Component>, BiFunction<GameObject, Object[], ? extends Component>> PREFABS = new HashMap<>();



    /**
     * The gameobject this component is attached to.
     */
    public final GameObject gameObject;

    /**
     * Weather this component is currently enabled.
     */
    private boolean enabled = true;

    /**
     * Weather the start method was already called or should
     * be called before the next update loop.
     */
    private boolean started = false;

    /**
     * Weather the {@link #start()} method should be called
     * again after the component was disabled and re-enabled.
     */
    private boolean restartOnEnable = false;



    /**
     * Creates a new component attached to the given gameobject. The
     * gameobject must not contain a component of the same type just
     * yet or an {@link IllegalStateException} will be thrown.
     *
     * @param gameObject The gameobject to attach this component to.
     *                   Must not be {@code null}.
     */
    public Component(GameObject gameObject) {
        this(gameObject, false, false);
    }

    /**
     * Creates a new Component attached to the given gameobject.
     *
     * @param gameObject The gameobject to attach this component to.
     *                   Must not be {@code null}
     * @param allowDuplicates Whether this component allows other components
     *                        of the same type on the same gameobject
     * @param autoRemoveDuplicates If this gameobject disallows
     *                             duplicates, should the old component
     *                             be removed automatically. If not,
     *                             a {@link IllegalStateException} will
     *                             be thrown
     */
    public Component(GameObject gameObject, boolean allowDuplicates, boolean autoRemoveDuplicates) {
        this.gameObject = Arguments.checkNull(gameObject, "gameObject");
        if(!allowDuplicates && gameObject.hasComponent(getClass())) {
            if(autoRemoveDuplicates)
                gameObject.removeComponent(getClass());
            else throw new IllegalArgumentException("The component of type " + getClass().getName() + " does not allow multiple instances attached to the same gameobject");
        }
        gameObject.components.add(this);
    }



    public void start() {

    }

    public void earlyUpdate() {

    }

    public void update() {

    }

    public void lateUpdate() {

    }

    void runEarlyUpdate() {
        if(!started) {
            started = true;
            start();
        }
        earlyUpdate();
    }

    public void onRemove() {

    }



    public void setEnabled(boolean enabled) {
        if(this.enabled == enabled) return;
        this.enabled = enabled;
        if(enabled && isRestartOnEnable()) started = false;
    }

    public void disable() {
        setEnabled(false);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRestartOnEnable(boolean restartOnEnable) {
        this.restartOnEnable = restartOnEnable;
    }

    public boolean isRestartOnEnable() {
        return restartOnEnable;
    }

    public static Component onEarlyUpdate(GameObject gameObject, Runnable action) {
        Arguments.checkNull(gameObject, "gameObject");
        Arguments.checkNull(action, "action");
        return new Component(gameObject) {
            @Override
            public void earlyUpdate() {
                action.run();
            }
        };
    }

    public static Component onUpdate(GameObject gameObject, Runnable action) {
        Arguments.checkNull(gameObject, "gameObject");
        Arguments.checkNull(action, "action");
        return new Component(gameObject) {
            @Override
            public void update() {
                action.run();
            }
        };
    }

    public static Component onLateUpdate(GameObject gameObject, Runnable action) {
        Arguments.checkNull(gameObject, "gameObject");
        Arguments.checkNull(action, "action");
        return new Component(gameObject) {
            @Override
            public void lateUpdate() {
                action.run();
            }
        };
    }



    protected static <C extends Component> void registerPrefab(Class<C> cls, Function<GameObject, C> generator) {
        Arguments.checkNull(generator, "generator");
        registerPrefab(cls, (gameObject, $) -> generator.apply(gameObject));
    }

    protected static <C extends Component> void registerPrefab(Class<C> cls, BiFunction<GameObject, Object[], C> generator) {
        Arguments.checkNull(cls, "cls");
        Arguments.checkNull(generator, "generator");
        PREFABS.put(cls, generator);
    }

    @SuppressWarnings("unchecked")
    static <C extends Component> C instantiatePrefab(Class<C> cls, GameObject gameObject, Object... arguments) {
        Arguments.checkNull(cls, "cls");
        Arguments.checkNull(gameObject, "gameObject");

        // Ensure the class actually had a chance to register itself. This seems to be the only way to initialize a class
        // without calling a discrete static method, which ain't be possible because we don't know the class at compile
        // time. Unfortunately simply calling 'XY.class' does not initialize the class.
        try {
            // Turns out this is actually working just fine online despite looking like recursion
            Class.forName(cls.getName());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        if(!PREFABS.containsKey(cls))
            throw new IllegalStateException("The component type '" + cls.getName() + "' does not have a prefab generator registered");
        return Objects.requireNonNull(
                ((BiFunction<GameObject, Object[], C>) PREFABS.get(cls)).apply(gameObject, arguments),
                "The prefab generator for '" + cls.getName() + "' produced a null result."
        );
    }
}
