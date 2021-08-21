package com.github.rccookie.greenfoot.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An input axis.
 */
public interface Axis {

    /**
     * The left / right arrow keys.
     */
    String LEFT_RIGHT = "leftRight";

    /**
     * The up / down arrow keys.
     */
    String UP_DOWN = "upDown";

    /**
     * The w / s keys.
     */
    String W_S = "ws";

    /**
     * The a / d keys.
     */
    String A_D = "ad";

    /**
     * The q / e keys.
     */
    String Q_E = "qe";

    /**
     * The {@link #LEFT_RIGHT} and {@link #A_D} axis combined.
     */
    String HORIZONTAL = "horizontal";

    /**
     * The {@link #UP_DOWN} and {@link #W_S} axis combined.
     */
    String VERTICAL = "vertical";

    /**
     * The shift / control keys.
     */
    String BOOST = "boost";

    /**
     * The + / - keys.
     */
    String PLUS_MINUS = "plusMinus";

    /**
     * The page up / page down keys.
     */
    String PAGE_UP_DOWN = "pageUpDown";

    /**
     * The relative mouse's x position.
     */
    String MOUSE_X = "mouseX";

    /**
     * The relative mouse's y position.
     */
    String MOUSE_Y = "mouseY";

    /**
     * The mouse's x movement speed.
     */
    String MOUSE_MOVE_X = "mouseMoveX";

    /**
     * The mouse's y movement speed.
     */
    String MOUSE_MOVE_Y = "mouseMoveY";



    /**
     * Returns the value of the axis at the current point in time. Usually
     * the returned value should be within {@code -1} and {@code 1}.
     *
     * @return The axis value
     */
    double getCurrentValue();



    /**
     * Returns the current value of the specified input axis. Throws a
     * {@link NullPointerException} if the specified axis is not defined.
     *
     * @param axisName The name of the axis
     * @return The value of the axis
     */
    static double get(String axisName) {
        return Objects.requireNonNull(AxisUtility.AXIS.get(axisName), "The axis '" + axisName + "' is not defined").getCurrentValue();
    }

    /**
     * Registers the given axis with the given name so that it can be used
     * with {@link #get(String)}. Use {@code null} to remove an existing axis.
     *
     * @param name The name to register the axis as. Any previous axis with
     *             that name will be overridden.
     * @param axis The axis to register
     */
    static void registerAxis(String name, Axis axis) {
        AxisUtility.AXIS.put(name, axis);
    }
}

final class AxisUtility {

    private static final double MOUSE_SPEED_FACTOR = 0.001;

    static final Map<String, Axis> AXIS = new HashMap<>();

    static {
        Axis.registerAxis(Axis.LEFT_RIGHT, () ->   (KeyState.of("left").down ? -1 : 0) +   (KeyState.of("right").down ? 1 : 0));
        Axis.registerAxis(Axis.UP_DOWN, () ->      (KeyState.of("up").down ? 1 : 0) +      (KeyState.of("down").down ? -1 : 0));
        Axis.registerAxis(Axis.W_S, () ->          (KeyState.of("w").down ? 1 : 0) +       (KeyState.of("s").down ? -1 : 0));
        Axis.registerAxis(Axis.A_D, () ->          (KeyState.of("a").down ? -1 : 0) +      (KeyState.of("d").down ? 1 : 0));
        Axis.registerAxis(Axis.Q_E, () ->          (KeyState.of("q").down ? -1 : 0) +      (KeyState.of("e").down ? 1 : 0));
        Axis.registerAxis(Axis.BOOST, () ->        (KeyState.of("shift").down ? 1 : 0) +   (KeyState.of("control").down ? -1 : 0));
        Axis.registerAxis(Axis.PLUS_MINUS, () ->   (KeyState.of("+").down ? 1 : 0) +       (KeyState.of("-").down ? -1 : 0));
        Axis.registerAxis(Axis.PAGE_UP_DOWN, () -> (KeyState.of("page up").down ? 1 : 0) + (KeyState.of("page down").down ? -1 : 0));
        Axis.registerAxis(Axis.HORIZONTAL, () ->   Math.max(-1, Math.min(1, Axis.get(Axis.LEFT_RIGHT) + Axis.get(Axis.A_D))));
        Axis.registerAxis(Axis.VERTICAL, () ->     Math.max(-1, Math.min(1, Axis.get(Axis.UP_DOWN) + Axis.get(Axis.W_S))));
        Axis.registerAxis(Axis.MOUSE_X, () ->      2 * MouseState.get().location.x() / Core.tryGetMap().map(com.github.rccookie.greenfoot.core.Map::getWidth).orElse(600) - 1);
        Axis.registerAxis(Axis.MOUSE_Y, () ->      2 * MouseState.get().location.y() / Core.tryGetMap().map(com.github.rccookie.greenfoot.core.Map::getHeight).orElse(400) - 1);
        Axis.registerAxis(Axis.MOUSE_MOVE_X, new Axis() {
            double lastPos, speed = 0;
            {
                Core.registerOnGlobalUpdate(this::update);
                lastPos = MouseState.get().location.x();
            }
            private void update() {
                double nowPos = MouseState.get().location.x();
                speed = MOUSE_SPEED_FACTOR * (nowPos - lastPos) / Time.deltaTime();
                lastPos = nowPos;
            }
            @Override
            public double getCurrentValue() { return speed; }
        });
        Axis.registerAxis(Axis.MOUSE_MOVE_Y, new Axis() {
            double lastPos, speed = 0;
            {
                Core.registerOnGlobalUpdate(this::update);
                lastPos = MouseState.get().location.y();
            }
            private void update() {
                double nowPos = MouseState.get().location.y();
                speed = MOUSE_SPEED_FACTOR * (nowPos - lastPos) / Time.deltaTime();
                lastPos = nowPos;
            }
            @Override
            public double getCurrentValue() { return speed; }
        });
    }
}
