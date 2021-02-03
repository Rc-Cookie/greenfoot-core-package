package com.github.rccookie.common.event;

import java.util.function.Consumer;

/**
 * A class that allows to execute methods on different threads easily, for example with a delay.
 * <p>To make the use of this class more conveniant you can write
 * <p>{@code import static packages.event.Threads.*;}<p>instead of
 * <p>{@code import packages.event.Threads;}
 * <p>to be able to write for example {@code delay(...)} instead of {@code Delay.delay(...)}.
 */
public final class Threads extends Thread {



    // ---------------------------------------------------------
    // Internal thread implementation
    // ---------------------------------------------------------



    /**
     * The time at which the execution will actually start.
     */
    final long start;

    /**
     * The action to perform.
     */
    final Consumer<Object> action;

    /**
     * The objects to pass in as argument when the action gets executed.
     */
    final Object argument;

    /**
     * Creates and starts a new thread to run after the specified delay the given action.
     * 
     * @param nanoDelay The nanoseconds to delay the execution
     * @param action The action to run
     */
    private Threads(final long nanoDelay, Object argument, final Consumer<Object> action) {
        start = System.nanoTime() + nanoDelay;
        this.argument = argument;
        this.action = action;

        // Increase performance of other threads
        setPriority(Thread.MIN_PRIORITY);

        start();
    }

    /**
     * Waits until the start time is reached and calls {@code run(null)} on the given action.
     */
    @Override
    public void run() {
        while(System.nanoTime() < start);
        action.accept(argument);
    }



    // ---------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------



    /**
     * Executes {@code action} after the specified amount of time. Note that seconds is a {@code double},
     * time will be measured using {@code System.nanoTime()}.
     * <p>To make the use of this class more conveniant you can write
     * <p>{@code import static packages.event.Delay.*;}<p>instead of
     * <p>{@code import packages.event.Delay;}
     * <p>to be able to write {@code delay(...)} instead of {@code Delay.delay(...)}.
     * 
     * @param seconds The number of seconds to delay the execution, as a double
     * @param argument The argument to be passed into the run method on execution
     * @param action The action to execute delayed.
     */
    public static final void delay(double seconds, Object argument, Consumer<Object> action) {
        new Threads((long)(seconds * 1000000000), argument, action);
    }

    /**
     * Executes {@code action} after the specified amount of time. Note that seconds is a {@code double},
     * time will be measured using {@code System.nanoTime()}.
     * <p>To make the use of this class more conveniant you can write
     * <p>{@code import static packages.event.Delay.*;}<p>instead of
     * <p>{@code import packages.event.Delay;}
     * <p>to be able to write {@code delay(...)} instead of {@code Delay.delay(...)}.
     * 
     * @param seconds The number of seconds to delay the execution, as a double
     * @param action The action to execute delayed. As argument will be passsed {@code null}
     */
    public static final void delay(double seconds, Consumer<Object> action) {
        delay(seconds, null, action);
    }


    /**
     * Runs the given action with the specified argument on a new thread simultaniously to the executing method.
     * <p>To make the use of this class more conveniant you can write
     * <p>{@code import static packages.event.Delay.*;}<p>instead of
     * <p>{@code import packages.event.Delay;}
     * <p>to be able to write {@code runParallel(...)} instead of {@code Delay.runParallel(...)}.
     * 
     * @param argument The argument to be passed into the run method on execution
     * @param action The action to be exectuted simultaniously
     */
    public static final void runParralel(Object argument, Consumer<Object> action) {
        new Threads(0, argument, action);
    }

    /**
     * Runs the given action on a new thread simultaniously to the executing method.
     * <p>To make the use of this class more conveniant you can write
     * <p>{@code import static packages.event.Delay.*;}<p>instead of
     * <p>{@code import packages.event.Delay;}
     * <p>to be able to write {@code runParallel(...)} instead of {@code Delay.runParallel(...)}.
     * 
     * @param action The action to be exectuted simultaniously. As argument will be passsed {@code null}
     */
    public static final void runParralel(Consumer<Object> action) {
       runParralel(null, action);
    }
}
