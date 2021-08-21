package com.github.rccookie.greenfoot.core;

import com.github.rccookie.greenfoot.java.util.Optional;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import greenfoot.Greenfoot;
import greenfoot.World;
import greenfoot.core.Simulation;
import greenfoot.core.WorldHandler;
import greenfoot.event.SimulationListener.AsyncEvent;
import greenfoot.platforms.GreenfootUtilDelegate;
import greenfoot.platforms.ide.GreenfootUtilDelegateIDE;
import greenfoot.util.GreenfootUtil;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to work with the simulation and more, for example getting random numbers.
 *
 * @author RcCookie
 * @version 1.0
 */
public final class Core  {

    /**
     * The factor the target speed has to be scaled with to reach online the
     * same fps as offline.
     */
    public static final double ONLINE_SPEED_FACTOR = 1.1;

    /**
     * Weather the console settings have yet been initialized.
     */
    private static boolean initialized = false;

    private static final Set<Runnable> onEarlyGlobalUpdate = new HashSet<>();
    private static final Set<Runnable> onLateGlobalUpdate = new HashSet<>();

    /**
     * Indicates weather the current session is online or on the Greenfoot application.
     * Offline the code runs plain java ensuring that any java functionality will work.
     * Online however the code gets converted to javascript which is not very reliable
     * and does not have all classes that java has. Therefore, special handling when
     * operating online max be helpful or necessary.
     */
    private static final Session SESSION;

    private static Session sessionOverride = null;

    static {
        Session session;
        try {
            onlineTestCommand();
            try {
                Field delegateField = GreenfootUtil.class.getDeclaredField("delegate");
                delegateField.setAccessible(true);
                GreenfootUtilDelegate delegate = (GreenfootUtilDelegate) delegateField.get(null);
                session = delegate instanceof GreenfootUtilDelegateIDE ? Session.OFFLINE : Session.STANDALONE;
            } catch (Exception e) {
                e.printStackTrace();
                session = Session.OFFLINE;
            }
        } catch (Exception e) {
            session = Session.ONLINE;
        }
        SESSION = session;

        initialize();
        Console.split(SESSION.toString());
    }

    @SuppressWarnings("RedundantThrows")
    private static void onlineTestCommand() throws Exception {
        new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }



    /**
     * Should not be initiated.
     */
    private Core() { }



    /**
     * Indicates the running state of the simulation based on calls of {@link #run()}
     * and {@link #pause()}. May be incorrect because running state can also be changed
     * using {@link Greenfoot#start()} and {@link Greenfoot#stop()}. Only used if
     * reflection of {@link Simulation#paused} fails.
     */
    @SuppressWarnings("JavadocReference")
    private static boolean running = false;

    private static Map currentMap = null;



    /**
     * Opens a prompt for the user to enter some text and returns that text. The scenario
     * will be paused while the prompt is visible.
     *
     * @param prompt The information that describes what the user should enter
     * @return The text that the user entered
     */
    public static String ask(String prompt) {
        return Greenfoot.ask(prompt);
    }

    /**
     * Pauses the execution of the simulation for the specified number of time steps. The
     * duration of these depends on the current scenario speed.
     *
     * @param timeSteps The number of time steps to pause the scenario
     */
    public static void pause(int timeSteps) {
        Greenfoot.delay(timeSteps);
    }

    /**
     * Returns the current level of the microphone input, between {@code 0} and {@code 1}.
     *
     * @return The microphone input level
     */
    public static double getMicInLevel() {
        return Greenfoot.getMicLevel() / 100d;
    }

    /**
     * Returns a random number between {@code min} (inclusive) and {@code max} in the given
     * step size. For example, {@code random(2, 4, 0.5)} could return 2, 2.5, 3 or 3.5.
     *
     * @param min The lower limit
     * @param max The upper limit
     * @param step The step size of the numbers possibly returned.
     * @return A random number
     */
    public static double random(double min, double max, double step) { // 2, 4, 0.5
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
     * @return A random integer
     */
    public static int random(int min, int max, int step) {
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
    public static int random(int min, int max) {
        return random(min, max, 1);
    }

    /**
     * Returns a random integer between {@code 0} (inclusive) and {@code max}. For example,
     * {@code random(4)} could return 0, 1, 2 or 3.
     *
     * @param max The upper limit
     * @return A random integer in the specified range
     */
    public static int random(int max) {
        return random(0, max);
    }

    /**
     * Returns a random <i>double</i> between {@code min} (inclusive) and {@code max}.
     *
     * @param min The lower limit
     * @param max The upper limit
     * @return A random double in the specified range
     */
    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    /**
     * Returns a random <i>double</i> between {@code 0} (inclusive) and {@code max}.
     *
     * @param max The upper limit
     * @return A random double in the specified range
     */
    public static double random(double max) {
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
    public static void setSpeed(double speed) {
        if(speed <= 0) pause();
        else Greenfoot.setSpeed(Math.min(100, (int)(speed * 100)));
    }

    /**
     * Sets the simulation speed (the frequency of update() calls) to the given value.
     * {@code 100} means unlimited, {@code 1} is the slowest. Values less or equal to
     * {@code 0} will pause the scenario and leave the actual speed unchanged, other
     * values will <b>not</b> start it again! Values higher than {@code 100} will be
     * corrected to {@code 100}.
     *
     * @param speed The speed to set
     */
    public static void setIntSpeed(int speed) {
        setSpeed(speed / 100d);
    }

    /**
     * Sets the simulation speed (the frequency of update() calls) to render with the
     * given framerate. The specified framerate can only be targeted, if the executing
     * machine cannot run the scenario at the given framerate it will throttle down
     * accordingly. Also, the specified framerate may not be the exact framerate targeted
     * exactly but rather a slightly higher one due to limitations of Greenfoot's 100-step
     * speed system.
     *
     * @param fps The target fps. Passing {@code 0} will pause the scenario instead
     */
    public static void setFps(int fps) {

        long delay = 1000000000L / Math.max(fps, 1);
        double speed = fps == 0 ? 0 :  delayToSpeed(delay) * 0.01;
        if(getRealSession() == Session.ONLINE) speed *= ONLINE_SPEED_FACTOR;
        Console.debug("Speed for {} fps is {}", fps, speed);

        if(getRealSession() == Session.ONLINE) {
            setSpeed(speed);
            return;
        }

        try {
            synchronized(Simulation.getInstance()) {
                Field speedField = Simulation.class.getDeclaredField("speed");
                speedField.setAccessible(true);
                speedField.set(Simulation.getInstance(), (int)(speed * 100));

                Field delayField = Simulation.class.getDeclaredField("delay");
                delayField.setAccessible(true);
                delayField.set(Simulation.getInstance(), delay);

                Field pausedField = Simulation.class.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if(pausedField.getBoolean(Simulation.getInstance())) {

                    Field interruptLockField = Simulation.class.getDeclaredField("interruptLock");
                    interruptLockField.setAccessible(true);
                    synchronized(interruptLockField.get(Simulation.getInstance())) {
                        Simulation.getInstance().interrupt();
                    }
                }

                Method fireSimulationEventAsyncMethod = Simulation.class.getDeclaredMethod("fireSimulationEventAsync", AsyncEvent.class);
                fireSimulationEventAsyncMethod.setAccessible(true);
                fireSimulationEventAsyncMethod.invoke(Simulation.getInstance(), AsyncEvent.CHANGED_SPEED);
            }
            Console.info("Changed Simulation internal speed");
        } catch(Throwable t) {
            Console.debug("Failed to set Simulation internal delay");
            t.printStackTrace();
            setSpeed(speed);
        }
    }

    private static int delayToSpeed(long delay) {
        if(delay <= 0) return Simulation.MAX_SIMULATION_SPEED;
        return Simulation.MAX_SIMULATION_SPEED - (int)(Math.log((double)delay / 30000) / Math.log(1.1370685666958) + 1);
    }

    /**
     * Returns the current simulation speed. Weather the scenario is currently paused
     * is irrelevant.
     *
     * @return The current speed between 0.01 and 1
     */
    public static double getSpeed() {
        return Simulation.getInstance().getSpeed() / 100d;
    }

    /**
     * Sets the map to be shown and updated if the scenario is running.
     *
     * @param map The map to show
     */
    public static void setMap(Map map) {
        Arguments.checkNull(map);
        Console.mapDebug("World to set", map.world.getClass().getName());

        if(currentMap == map) return;
        if(currentMap != null) currentMap.onClose();

        currentMap = map;
        Greenfoot.setWorld(map.world);
        if(getRealSession() == Session.STANDALONE)
            adjustWindowSize(map, 5);
        map.onSet();
    }

    private static void adjustWindowSize(Map map, int remainingTries) {
        if(!map.isActiveMap()) return;
        Stage.getWindows().stream().findAny().ifPresent(w -> {
            w.setWidth(map.getWidth() * map.getCellSize() + 18);
            w.setHeight(map.getHeight() * map.getCellSize() + 101);
        });
        // Greenfoot may override this value if we are too fast, so just do it multiple times :)
        if(remainingTries > 0) Execution.runLater(() -> adjustWindowSize(map, remainingTries - 1), 0.2);
    }

    /**
     * Returns the currently shown map.
     *
     * @return An optional containing the current map, or an empty optional if no map is shown
     */
    public static Optional<Map> tryGetMap() {
        return Optional.ofNullable(getMap());
    }

    /**
     * Returns the currently shown map.
     *
     * @return The current map
     */
    public static Map getMap() {
        return currentMap;
    }

    /**
     * Returns the currently shown and updated world.
     *
     * @return The current world
     */
    public static World getWorld() {
        return WorldHandler.getInstance().getWorld();
    }

    /**
     * Starts / resumes the run execution. If the scenario is already running
     * this will have no effect.
     */
    public static void run() {
        setRun(true);
    }

    /**
     * Pauses the run execution. If the scenario was already in a paused state
     * this will have no effect.
     */
    public static void pause() {
        setRun(false);
    }

    /**
     * Sets weather the act loop should be executed. If the state is unchanged
     * this will have no effect.
     *
     * @param flag Weather to run the act loop.
     */
    public static void setRun(boolean flag) {
        Simulation.getInstance().setPaused(!flag);
        running = flag;
        Console.mapDebug("Now running", flag);
    }

    /**
     * Returns weather the scenario is currently running the act loop.
     *
     * @return Weather the act loop is running
     */
    public static boolean isRunning() {
        return running;
    }

    /**
     * Indicates weather the current session is online or on the Greenfoot application.
     * Offline the code runs plain java ensuring that any java functionality will work.
     * Online however the code gets converted to javascript which is not very reliable
     * and does not have all classes that java has. Therefore, special handling when
     * operating online max be helpful or necessary.
     * <p>The online state may be emulated using {@link #emulateSessionState(Session)}
     * and {@link #getRealSession()} returns the 'real' online state. This however should
     * not be used in general, as it lacks debugging abilities.
     *
     * @return The online state of this session
     */
    public static Session getSession() {
        return sessionOverride != null ? sessionOverride : SESSION;
    }

    /**
     * Overrides the online state of this session. Passing {@code null} will cause the
     * actual state to be returned.
     *
     * @param emulatedSession The online state to emulate
     */
    public static void emulateSessionState(Session emulatedSession) {
        sessionOverride = emulatedSession;
    }

    /**
     * Returns the 'real' state of this session, regardless of weather it is being emulated
     * or not. This method should only be used for very essential functionality as it
     * cannot be emulated for debugging purposes.
     *
     * @return The 'real' state of this session
     */
    public static Session getRealSession() {
        return SESSION;
    }


    public static void registerOnEarlyGlobalUpdate(Runnable action) {
        Arguments.checkNull(action);
        onEarlyGlobalUpdate.add(action);
    }

    public static void registerOnGlobalUpdate(Runnable action) {
        Arguments.checkNull(action);
        onLateGlobalUpdate.add(action);
    }

    static RuntimeException earlyGlobalUpdate() {
        RuntimeException exception = null;
        for (Runnable action : onEarlyGlobalUpdate) {
            try {
                action.run();
            } catch(RuntimeException e) {
                if(exception == null) exception = e;
                exception.addSuppressed(e);
            }
        }
        return exception;
    }

    static RuntimeException lateGlobalUpdate(RuntimeException exception) {
        for (Runnable action : onLateGlobalUpdate) {
            try {
                action.run();
            } catch(RuntimeException e) {
                if(exception == null) exception = e;
                else exception.addSuppressed(e);
            }
        }
        return exception;
    }



    /**
     * Initializes some settings.
     */
    static void initialize() {
        if(initialized) return;
        initialized = true;
        if(getSession() == Session.STANDALONE) {
            Console.Config.manualConsoleWidth = 245;
            Console.getDefaultFilter().setEnabled(Console.OutputFilter.DEBUG, true);
            Console.getFilter("com.github.rccookie").setEnabled(Console.OutputFilter.DEBUG, false);
        }
        else {
            Console.Config.coloredOutput = false;
            Console.Config.manualConsoleWidth = 60;
        }

        if(getRealSession() == Session.STANDALONE)
            System.setErr(Console.CONSOLE_ERROR_STREAM);

        Time.init();
        Execution.init();
    }
}
