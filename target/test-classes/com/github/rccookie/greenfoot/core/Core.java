package com.github.rccookie.greenfoot.core;

import java.lang.reflect.Field;

import com.github.rccookie.common.util.Console;

import greenfoot.Greenfoot;
import greenfoot.World;
import greenfoot.core.Simulation;
import greenfoot.core.WorldHandler;

/**
 * Utility class to work with the simulation and more, for example getting random numbers.
 * 
 * @author RcCookie
 * @version 1.0
 */
public final class Core  {

    /**
     * Indicates weather the current session is online or on the Greenfoot application.
     * Offline the code runs plain java ansuring that any java functionallity will work.
     * Online however the code gets converted to javascript which is not very reliable
     * and does not have all classes that java has. Therefore special handling when
     * operating online max be helpful or neccecary.
     */
    private static final boolean IS_ONLINE;

    static {
        boolean isOnline = false;
        // Simple test that will throw an exception when online due to missing class
        // If offline this will do nothing else than some console settings
        try {
            System.setErr(Console.CONSOLE_ERROR_STREAM);
        } catch(Exception e) {
            isOnline = true;
            System.out.println("Online session (Exception)");
            e.printStackTrace();
        } catch(Error e) {
            isOnline = true;
            System.out.println("Online session (Error)");
            e.printStackTrace();
        }
        IS_ONLINE = isOnline;
    }

    private static Boolean onlineOverride = null;



    /**
     * Weather the console settings have yet been initialized.
     */
    private static boolean initialized = false;

    static {
        initialize();
    }



    /**
     * Should not be initiated.
     */
    private Core() { }



    /**
     * Indicates the running state of the simulation based on calls of {@link #run()}
     * and {@link #pause()}. My be incorrect because running state can also be changed
     * using {@link Greenfoot#start()} and {@link Greenfoot#stop()}. Only used if
     * reflection of {@link Simulation#paused} fails.
     */
    private static boolean propablyRunning = false;



    /**
     * Opens a prompt for the user to enter some text and returns that text. The scenario
     * will be paused while the prompt is visible.
     * 
     * @param prompt The information that describes what the user should enter
     * @return The text that the user entered
     */
    public static final String ask(String prompt) {
        return Greenfoot.ask(prompt);
    }

    /**
     * Pauses the execution of the simulation for the specified number of time steps. The
     * duration of these depends on the current scenario speed.
     * 
     * @param timeSteps The number of time steps to pause the scenario
     */
    public static final void pause(int timeSteps) {
        Greenfoot.delay(timeSteps);
    }

    /**
     * Returns the current level of the microphone input, between {@code 0} and {@code 1}.
     * 
     * @return The microphone input level
     */
    public static final double getMicInLevel() {
        return Greenfoot.getMicLevel() / 100d;
    }

    /**
     * Returns a random number between {@code min} (inclusive) and {@code max} in the given
     * step size. For example, {@code random(2, 4, 0.5)} could return 2, 2.5, 3 or 3.5.
     * 
     * @param min The lower limit
     * @param max The upper limit
     * @param step The step size of the numbers possibly returned.
     * @return A random number in the specified ruleset
     */
    public static final double random(double min, double max, double step) { // 2, 4, 0.5
        double range = max - min; // 4 - 2 = 2
        int oneStepRange = (int)(range / step); // 2 / 0.5 = 4
        return Greenfoot.getRandomNumber(oneStepRange) * 0.5 + min; // [0|1|2|3] * 0.5 + 2 = [0|0.5|1|1.5] + 2 = [2|2.5|3|3.5]
    }

    /**
     * Returns a random integer between {@code min} (inclusive) and {@code max} in the given
     * step size. For example, {@code random(2, 8, 2)} could return 2, 4 or 6.
     * 
     * @param min The lower limit
     * @param max The upper limit
     * @param step The step size of the numbers possibly returned.
     * @return A random integer in the specified ruleset
     */
    public static final int random(int min, int max, int step) {
        return (int)random((double)min, max, step);
    }

    /**
     * Returns a random integer between {@code min} (inclusive) and {@code max}. For example,
     * {@code random(2, 6)} could return 2, 3, 4 or 5.
     * 
     * @param min The lower limit
     * @param max The upper limit
     * @return A random integer between the limits
     */
    public static final int random(int min, int max) {
        return random(min, max, 1);
    }

    /**
     * Returns a random integer between {@code 0} (inclusive) and {@code max}. For example,
     * {@code random(4)} could return 0, 1, 2 or 3.
     * 
     * @param max The upper limit
     * @return A random integer in the specified range
     */
    public static final int random(int max) {
        return random(0, max);
    }

    /**
     * Returns a random <i>double</i> between {@code min} (inclusive) and {@code max}.
     * 
     * @param min The lower limit
     * @param max The upper limit
     * @return A random double in the specified range
     */
    public static final double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    /**
     * Returns a random <i>double</i> between {@code 0} (inclusive) and {@code max}.
     * 
     * @param max The upper limit
     * @return A random double in the specified range
     */
    public static final double random(double max) {
        return Math.random() * max;
    }

    /**
     * Sets the simulation speed (the frequency of act() calls) to the given percentage.
     * {@code 1} means unlimited, {@code 0.01} is the slowest. Values less or equal to
     * {@code 0} will pause the scenario and leave the actual speed unchanged, other
     * values will <b>not</b> start it again! Values higher than {@code 1} will be
     * corrected to {@code 1}.
     * 
     * @param speed The speed to set
     */
    public static final void setSpeed(double speed) {
        if(speed < 0) pause();
        else Greenfoot.setSpeed(Math.min(100, (int)(speed * 100)));
    }

    /**
     * Sets the simulation speed (the frequency of act() calls) to the given value.
     * {@code 100} means unlimited, {@code 1} is the slowest. Values less or equal to
     * {@code 0} will pause the scenario and leave the actual speed unchanged, other
     * values will <b>not</b> start it again! Values higher than {@code 100} will be
     * corrected to {@code 100}.
     * 
     * @param speed The speed to set
     */
    public static final void setIntSpeed(int speed) {
        setSpeed(speed / 100d);
    }

    /**
     * Returns the current simulation speed. Weather the scenario is currently paused
     * is irrelevant.
     * 
     * @return The current speed between 0.01 and 1
     */
    public static final double getSpeed() {
        return Simulation.getInstance().getSpeed() / 100d;
    }

    /**
     * Sets the map to be shown and updated if the scenario is running.
     * 
     * @param map The map to show
     */
    public static final void setMap(Map map) {
        Console.map("World to set", map.world.getClass().getName());
        Greenfoot.setWorld(map.world);
    }

    /**
     * Returns the currently shown map. Throws an error if no map is shown.
     * 
     * @return The current map
     */
    public static final Map getMap() {
        return ((Map.SupportWorld)WorldHandler.getInstance().getWorld()).map;
    }

    /**
     * Returns the currently shown and updated world.
     * 
     * @return The current world
     */
    public static final World getWorld() {
        return WorldHandler.getInstance().getWorld();
    }

    /**
     * Starts / resumes the run execution. If the scenario is already running
     * this will have no effect.
     */
    public static final void run() {
        setRun(true);
    }

    /**
     * Pauses the run execution. If the scenario was already in a paused state
     * this will have no effect.
     */
    public static final void pause() {
        setRun(false);
    }

    /**
     * Sets weather the act loop should be executed. If the state is unchenged
     * this will have no effect.
     * 
     * @param flag Weather to run the act loop.
     */
    public static final void setRun(boolean flag) {
        Simulation.getInstance().setPaused(!flag);
        propablyRunning = flag;
    }

    /**
     * Returns weather the scenario is currently running the act loop.
     * 
     * @return Weather the act loop is running
     */
    public static final boolean isRunning() {
        try {
            Field paused = Simulation.class.getDeclaredField("paused");
            //paused.trySetAccessible();
            paused.setAccessible(true);
            return !(Boolean)paused.get(Simulation.getInstance());
        } catch(Exception e) {
            Console.warn("Failed to load run state from framework");
            e.printStackTrace();
            return propablyRunning;
        }
    }

    /**
     * Indicates weather the current session is online or on the Greenfoot application.
     * Offline the code runs plain java ansuring that any java functionallity will work.
     * Online however the code gets converted to javascript which is not very reliable
     * and does not have all classes that java has. Therefore special handling when
     * operating online max be helpful or neccecary.
     * <p>The online state may be emulated using {@link #emulateOnlineState(Boolean)}
     * and {@link #isOnlineStateEmulated()} checks weather the online state is currently
     * being overridden. To get the 'real' online state you can use
     * <pre>
     * boolean realOnline = Core.isOnlineStateEmulated() ? !isOnline() : isOnline();
     * </pre>
     * but this should not be used in normal cases because it disallowes for any
     * debugging and online testing.
     * 
     * @return The online state of this session
     */
    public static final boolean isOnline() {
        return onlineOverride != null ? onlineOverride : IS_ONLINE;
    }

    /**
     * Overrides the online state of this session. Passing {@code null} will cause the
     * actual state to be returned.
     * 
     * @param emulatedOnlineState The online state to emulate
     */
    public static final void emulateOnlineState(Boolean emulatedOnlineState) {
        onlineOverride = emulatedOnlineState;
    }

    /**
     * Returns weather the value returned by {@link #isOnline()} is incorrect due to
     * it being emulated. A return of {@code false} does not neccecarily mean that the
     * online state is not overridden, but that a potential override does not change
     * the actual state.
     * 
     * @return Weather the online state returned by {@link #isOnline()} is incorrect due
     *         to emulation
     */
    public static final boolean isOnlineStateEmulated() {
        return onlineOverride != null && IS_ONLINE != onlineOverride;
    }



    /**
     * Initializes some settings.
     */
    static final void initialize() {
        if(initialized) return;
        initialized = true;
        Console.Config.coloredOutput = false;
        Console.Config.manualConsoleWidth = 60;
        try {
            System.setErr(Console.CONSOLE_ERROR_STREAM);
        } catch(Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        } catch(Error e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}
