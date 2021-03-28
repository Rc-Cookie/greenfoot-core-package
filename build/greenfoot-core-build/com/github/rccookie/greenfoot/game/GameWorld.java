package com.github.rccookie.greenfoot.game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.UserInfo;
import com.github.rccookie.greenfoot.game.GameWorld;
import com.github.rccookie.common.geometry.Geometry;
import com.github.rccookie.common.util.ClassTag;
import com.github.rccookie.common.util.Lists;
import com.github.rccookie.greenfoot.game.util.ActorTag;
import com.github.rccookie.greenfoot.ui.Scoreboard;
import com.github.rccookie.greenfoot.ui.Fade;
import com.github.rccookie.greenfoot.ui.Text;
import com.github.rccookie.greenfoot.ui.TextButton;
import com.github.rccookie.greenfoot.ui.UIPanel;
import com.github.rccookie.greenfoot.ui.UIWorld;

public abstract class GameWorld extends UIWorld {

    /**
     * The precition the score is being saved with. It describes the factor the
     * score is being scaled with when being saved as an interger. For displaying
     * this will be reverted.
     */
    public static final int SCORE_PRECITION = 100;


    static {
        /*@SuppressWarnings("rawtypes")
        Class[] newOrder = {
            FpsDisplay.class,
            Fade.class,
            PausePanel.PauseUIButton.class,
            PausePanel.PauseUIText.class,
            PausePanel.class,
            PausePanel.PauseUIBackground.class,
            TextButton.class,
            DropDownMenu.Backpanel.class,
            Slider.Handle.class,
            Slider.class,
            Text.class,
            UIPanel.class
        };
        UI_CLASS_PAINT_ORDER = newOrder;*/
    }



    private static final String QUIT_INFO_STRING = "Quitting is not implemented.\nOverride the 'quit' method in your world class to add functionallity.\nFor example:\n@Override\npublic void quit() {\n    Greenfoot.setWorld(new MainMenu());\n}";
    private static final String RESTART_INFO_STRING = "Restarting is not implemented.\nOverride the 'restart' method in your world class to add functionallity.\nFor example:\n@Override\npublic void restart() {\n    Greenfoot.setWorld(new MyWorld());\n}\nwhere 'MyWorld' is this class";
    private static final String TIME_REQUEST_BEFORE_START_STRING = "The start time cannot be requested before the game was started";
    private static final String TIME_REQUEST_BEFORE_END_STRING = "The end time cannot be requested before the game has ended";
    private static final String NO_TIME_LIMIT_STRING = "The game has no time limit";


    
    // ---------------------------------------------------------------------------------------
    // UI design
    // ---------------------------------------------------------------------------------------



    /**
     * Weather the current fps should be displayed.
     */
    public static boolean SHOW_FPS = true;

    /**
     * Weather the default pause ui shall be used.
     * <p>The default pause ui includes a pause button while the game is running and a pause
     * overlay that displays the current score (if score should be displayed) and offers a
     * resume and quit button. If restarts are enabled, a button for that will be displayed, too.
     * <p>Enabled by default.
     * @see #allowRestart
     */
    public static boolean USE_PAUSE_UI = true;

    /**
     * If {@code true} a fade-in will be used when the world is created.
     * <p>Enabled by default.
     */
    public static boolean FADE_IN_AT_START = true;


    /**
     * The main ui color. Effects e.g. backgrounds.
     */
    public static Color UI_COLOR_1 = new Color(40, 40, 40);

    /**
     * The second ui color. Effects e.g. button colors.
     */
    public static Color UI_COLOR_2 = Color.GRAY;

    /**
     * The main text color. Used generally for text on {@link #UI_COLOR_1}.
     */
    public static Color TEXT_COLOR_1 = Color.LIGHT_GRAY;

    /**
     * A color for text. Used e.g. on buttons.
     */
    public static Color TEXT_COLOR_2 = Color.BLACK;

    /**
     * The contrast text color. Used e.g. on titles
     */
    public static Color TEXT_COLOR_3 = Color.RED;



    // ---------------------------------------------------------------------------------------
    // Fields for adjusting the build-in features
    // ---------------------------------------------------------------------------------------



    /**
     * Weather the game time should be limited. If so if will be limited to
     * {@code gameDuration}.
     * <p>Disabled by default.
     * @see #setGameDuration(double)
     * @see #showTimer
     */
    protected boolean limitTime = false;

    /**
     * Weather a timer should be displayed.
     * <p>If the game is limited in time, the timer will count down the remaining time and switch
     * to a contrast color in the final 3 seconds.
     * <p>If the game is not limited in time, the timer will display the elapsed time since the beginning.
     * <p>Of course the timer only while the game is running.
     * <p>Enabled by default.
     * @see #gameDuration
     * @see #showTimer
     */
    protected boolean showTimer = true;

    /**
     * Effects the duration of the game, if it is limited in time. If {@link #limitTime} is {@code false}, it
     * will be reasigned with {@code true}.
     * <p>By default the game length is 30 seconds.
     * 
     * @param seconds The maximum duration of the game, in seconds
     */
    protected void setGameDuration(double seconds) {
        if(seconds < 0) {
            limitTime = false;
            return;
        }
        limitTime = true;
        gameDuration = (long)(seconds * 1000000000);
    }
    private long gameDuration = 30000000000l;

    /**
     * Effects the delay of the game start if the game should automatically start after a delay. If {@link #startAfterDelay}
     * is {@code false}, this will not have an effect.
     * <p>By default the delay is 3 seconds.
     * 
     * @param seconds The start delay
     * @see #startAfterDelay
     */
    protected void setStartDelay(double seconds) {
        if(seconds < 0) return;
        startDelay = (long)(seconds * 1000000000);
    }
    private long startDelay = 3000000000l;

    /**
     * Weather the game should automatically start after the time specified using {@link #setStartDelay(double)}.
     * <p>Enabled by default.
     * @see #setStartDelay(double)
     */
    protected boolean startAfterDelay = true;


    /**
     * Weather the highscore should be saved automatically once the game ended. Does not have an effect if
     * {@link #useScore} is {@code false}.
     * <p>Enabled by default.
     * @see #useScore
     */
    protected boolean autoSaveScore = true;

    /**
     * Weather the build-in score system should be used.
     * <p>Enabled by default.
     * @see #setScore(double)
     * @see #addScore(double)
     * @see #resetScore()
     * @see #getScore()
     * @see #autoSaveScore
     * @see #showScore
     */
    protected boolean useScore = true;

    /**
     * Weather the current score should be displayed in-game. The score will still be shown on the endscreen.
     * <p>Enabled by default.
     * @see #useScore
     */
    protected boolean showScore = true;

    /**
     * Weather restart buttons should be shown. If so, they will automatically call {@link #restart()} when
     * clicked.
     * <p>Enabled by default.
     * @see #restart()
     */
    protected boolean allowRestart = true;

    /**
     * Weather the screen should be cleared when the game starts. If so it will remove any actors added during
     * the starting time ignoring those tagged with {@code "ignoreOnClear"}.
     * <p>Disabled by default.
     * @see com.github.rccookie.greenfoot.game.util.ActorTag ActorTag
     */
    protected boolean clearWhenStarting = false;

    /**
     * Weather all objects added during a pause session should automatically be removed once the game is resumed.
     * Use this as an easy way to 'close' your pause ui.
     * <p><b>Does not effect objects not added while paused</b> nor objects tagged with {@code "ignoreOnClear"};
     * <p>Enabled by default.
     * @see com.github.rccookie.greenfoot.game.util.ActorTag ActorTag
     */
    protected boolean clearAfterResumed = true;

    /**
     * Weather the build-in endscreen shound be used. The endscreen includes the score, a quit button and, if
     * set so, a restart button and a link to the highscores.
     * <p>Enabled by default.
     * @see #linkHighscores
     * @see #allowRestart
     */
    protected boolean useEndscreen = true;

    /**
     * Weather a button linking to the build-in online highscores should be placed in the endscreen. If the
     * endscreen is not used, this will not have an effect.
     * <p>Enabled by default.
     * @see #useEndscreen
     * @see com.github.rccookie.greenfoot.ui.advanced.Scoreboard Scoreboard
     */
    protected boolean linkHighscores = true;


    /**
     * The name of the score when displayed. Has no effect is the score should not be displayed.
     * <p>By default this is 'Score'.
     * @see #useScore
     * @see #showScore
     */
    protected String scoreTitle = "Score";

    /**
     * The title of the endscreen, if used.
     * @see #useEndscreen
     */
    protected String endingText = "Game finished";
    

    
    // ---------------------------------------------------------------------------------------
    // Fields saving the games current state
    // ---------------------------------------------------------------------------------------



    private final List<Consumer<Long>> createdActions = new ArrayList<>();
    private final List<Consumer<Long>> gameStartActions = new ArrayList<>();
    private final List<Consumer<Long>> pausedActions = new ArrayList<>();
    private final List<Consumer<Long>> resumedActions = new ArrayList<>();
    private final List<Consumer<Double>> gameEndActions = new ArrayList<>();

    private final List<Actor> startupActors = new ArrayList<>();
    private final List<Actor> gameActors = new ArrayList<>();
    private final List<Actor> pauseActors = new ArrayList<>();



    /**
     * Current game state.
     */
    private State state;


    /**
     * Weather the next frame is the first frame.
     */
    private boolean firstFrame = true;

    /**
     * The current score.
     */
    private double score;


    /**
     * Start and end time of the game sequence.
     */
    private long createTime, startTime, endTime;

    /**
     * Last timestamp when the game resumed playing, including the start of the game.
     */
    private long lastResumeTime;

    /**
     * The time that has elapsed before the last pause.
     */
    private long timeElapsedUntilLastPause = 0;


    /**
     * The key pressed most lately.
     */
    private String lastKey = null;


    
    // ---------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------


    
    /**
     * Constructs a new GameWorld with the given size and cell size.
     * 
     * @param width The width of the world, in cells
     * @param height The height of the world, in cells
     * @param cellSize The size of a cell, in pixels
     */
    public GameWorld(int width, int height, int cellSize) {
        super(width, height, cellSize);
    }
    
    /**
     * Constructs a new GameWorld with the given size and cell size.
     * 
     * @param width The width of the world, in cells
     * @param height The height of the world, in cells
     * @param cellSize The size of a cell, in pixels
     * @param bounded Weather the world should be bounded
     */
    public GameWorld(int width, int height, int cellSize, boolean bounded) {
        super(width, height, cellSize, bounded);
    }

    {
        state = State.NOT_STARTED;
        colorBackground(UI_COLOR_1);
        if(SHOW_FPS) addFps();
        if(FADE_IN_AT_START) addRelative(Fade.fadeIn(UI_COLOR_1, 1), 0.5, 0.5);
        Greenfoot.start();
    }



    // ---------------------------------------------------------------------------------------
    // General updating
    // ---------------------------------------------------------------------------------------



    /**
     * This method cannot be used because is is used internally. Use the update methods
     * instead!
     * @see #update()
     * @see #notStartedUpdate()
     * @see #gameUpdate()
     * @see #pauseUpdate()
     * @see #endedUpdate()
     */
    @Override
    public void update() {
        lastKey = Greenfoot.getKey();
        if(firstFrame) runCreated();
        runUpdate();
    }


    /***
     * Ran only in the first frame.
     */
    private void runCreated() {
        firstFrame = false;
        createTime = System.nanoTime();
        if(startAfterDelay) addStartCountdown();
        for(Consumer<Long> action : createdActions) action.accept(System.nanoTime());
    }


    /**
     * Runs all update methods.
     */
    private void runUpdate() {
        generalUpdate();
        if(state == State.NOT_STARTED) internalNotStartedUpdate();
        else if(state == State.RUNNING) internalGameUpdate();
        else if(state == State.PAUSED) pauseUpdate();
        else if(state == State.ENDED) internalEndedUpdate();
        else throw new RuntimeException("Unexpected game state");
    }

    /**
     * Internal method usde while the game has not started yet. Also calls {@link #notStartedUpdate()}.
     */
    private void internalNotStartedUpdate() {
        if(startCountdown != null) {
            startCountdown.setTitle((int)Math.ceil((startDelay - System.nanoTime() + createTime) / 1000000000d) + "");
            if(startAfterDelay && System.nanoTime() - createTime >= startDelay) {
                removeObject(startCountdown);
                start();
            }
        }
        notStartedUpdate();
    }

    /**
     * Internal method used while the game is running. Also calls {@link #gameUpdate()}.
     */
    private void internalGameUpdate() {
        if(timer != null) {
            if(limitTime) {
                timer.setTitle(formatDouble(getRemainingTime()));
                //if(getRemainingTime() < 3 && !timer.getColor().equals(TEXT_COLOR_3)) timer.setColor(TEXT_COLOR_3);
            }
            else timer.setTitle(formatDouble(timeElapsed()));
        }
        if(limitTime && nanosElapsed() >= gameDuration) {
            if(timer != null) timer.setTitle("0.0");
            end();
        }
        gameUpdate();
        if(!isAlive()) end();
    }

    /**
     * Internal method used while the game has ended. Also calls {@code endedUpdate()}.
     */
    private void internalEndedUpdate() {
        if(useEndscreen && System.nanoTime() - getEndTime() >= 1000000000) Greenfoot.setWorld(new Result());
        endedUpdate();
    }


    
    // ---------------------------------------------------------------------------------------
    // Methods to be overridden
    // ---------------------------------------------------------------------------------------



    /**
     * Called once per frame independent of the game's state.
     */
    public void generalUpdate() { }

    /**
     * Called once per frame only as long as the game has not started.
     */
    public void notStartedUpdate() { }

    /**
     * Called once per frame only while the game is actively running. Use this for game mechanics.
     */
    public void gameUpdate() { }

    /**
     * Called once per frame only while the game is paused.
     */
    public void pauseUpdate() { }

    /**
     * Called once per frame only once the game has ended.
     */
    public void endedUpdate() { }


    /**
     * Returns is the game is still running or if the player has lost. In that case the game will
     * autimatically end. Override this method for an easy way to check the players state once per
     * frame.
     * <p>By default this always return {@code true}.
     * 
     * @return Weather the player/ the game is still alive
     */
    public boolean isAlive() { return true; }


    /**
     * Called whenever a 'quit' button is clicked. Override this to e.g. switch to a different world.
     */
    public void quit() { System.out.println(QUIT_INFO_STRING); }

    /**
     * Called whenever a 'restart' button is clicked. Override this to implement a restart function.
     */
    public void restart() { System.out.println(RESTART_INFO_STRING); }

    /**
     * Called when the highscore button is clicked. Override this to link to your own highscore
     * world.
     * <p>By default this opens the build-in highscore world.
     */
    public void openHighscores() { Greenfoot.setWorld(new Highscores()); }




    // ---------------------------------------------------------------------------------------
    // Input support methods
    // ---------------------------------------------------------------------------------------



    /**
     * Returns the key pressed most lately just like {@code Greenfoot.getKey()} would, with the difference
     * that is can be called multiple times per frame and will return the same every time.
     * 
     * @return The key pressed most lately, or {@code null}
     * @see greenfoot.Greenfoot#getKey() Greenfoot.getKey()
     */
    public String getKey() {
        return lastKey;
    }



    // ---------------------------------------------------------------------------------------
    // Automatic object removing
    // ---------------------------------------------------------------------------------------




    @Override
    public void addObject(Actor object, int x, int y) {
        if(object == null) return;
        if(state == State.RUNNING) gameActors.add(object);
        else if(state == State.NOT_STARTED) startupActors.add(object);
        else if(state == State.PAUSED) pauseActors.add(object);
        super.addObject(object, x, y);
    }

    @Override
    public void removeObject(Actor object) {
        if(object == null) return;
        gameActors.remove(object);
        startupActors.remove(object);
        pauseActors.remove(object);
        super.removeObject(object);
    }



    /**
     * Removes all objects that have been added while the game has been actively running.
     * <p>Objects in {@code ignore} and those tagged with {@code "ignoreOnClear"} will
     * not be removed.
     * <p>This does exactly the same as invoking
     * <pre>removeGameObjects(ignore, true);</pre>where {@code ignore} is an array of
     * ignored actors.
     * 
     * @param ignore Actors to ignore while removing
     * @see com.github.rccookie.greenfoot.game.util.ActorTag ActorTag
     */
    public void removeGameObjects(Actor... ignore) { removeGameObjects(false, ignore); }

    /**
     * Removes all objects that have been added while the game has been actively running.
     * <p>Objects in {@code ignore} and those tagged with {@code "ignoreOnClear"} will
     * not be removed.
     * 
     * @param ui Weather ui elements should be removed, too
     * @param ignore Actors to ignore while removing
     * @see com.github.rccookie.greenfoot.game.util.ActorTag ActorTag
     */
    public void removeGameObjects(boolean ui, Actor... ignore) {
        removeObjects(Lists.only(gameActors, a -> {
            if(ActorTag.getTags(a).contains("ignoreOnClear")) return false;
            if(!ui && ClassTag.getTags(a.getClass()).contains("ui")) return false;
            return !contains(ignore, a);
        }));
    }


    /**
     * Removes all objects that have been added while the game has not started.
     * <p>Objects in {@code ignore} and those tagged with {@code "ignoreOnClear"} will
     * not be removed.
     * <p>This does exactly the same as invoking
     * <pre>removeStartObjects(ignore, true);</pre>where {@code ignore} is an array of
     * ignored actors.
     * 
     * @param ignore Actors to ignore while removing
     * @see com.github.rccookie.greenfoot.game.util.ActorTag ActorTag
     */
    public void removeStartObjects(Actor... ignore) { removeStartObjects(true, ignore); }

    /**
     * Removes all objects that have been added while the game has not started.
     * <p>Objects in {@code ignore} and those tagged with {@code "ignoreOnClear"} will
     * not be removed.
     * 
     * @param ui Weather ui elements should be removed, too
     * @param ignore Actors to ignore while removing
     * @see com.github.rccookie.greenfoot.game.util.ActorTag ActorTag
     */
    public void removeStartObjects(boolean ui, Actor... ignore) {
        removeObjects(Lists.only(startupActors, a -> {
            if(ActorTag.getTags(a).contains("ignoreOnClear")) return false;
            if(!ui && ClassTag.getTags(a.getClass()).contains("ui")) return false;
            return !contains(ignore, a);
        }));
    }

    
    /**
     * Removes all objects that have been added while the game was paused.
     * <p>Objects in {@code ignore} and those tagged with {@code "ignoreOnClear"} will
     * not be removed.
     * 
     * @param ignore Actors to ignore while removing
     * @see com.github.rccookie.greenfoot.game.util.ActorTag ActorTag
     */
    public void removePauseObjects(Actor... ignore) {
        removeObjects(Lists.only(pauseActors, a -> {
            if(ActorTag.getTags(a).contains("ignoreOnClear")) return false;
            return !contains(ignore, a);
        }));
    }

    /**
     * Utility method to check weather an array contains an element.
     * @param <T> The type of the array
     * @param array The array
     * @param element The element to check for
     * @return Weather the array contains the element at least once
     */
    private <T> boolean contains(T[] array, T element) {
        for(T t : array) if(t == element) return true;
        return false;
    }



    // ---------------------------------------------------------------------------------------
    // Game states handeling
    // ---------------------------------------------------------------------------------------



    /**
     * Represents the games state.
     * <p>A game has at any point in time exactly one of the following states:
     * <ul><li>{@link State#NOT_STARTED NOT_STARTED} The game is yet to begin.
     * At this point in time it cannot have been in any other state and therefore you
     * cannot go back to this state once the game has started.
     * <li>{@link State#RUNNING RUNNING} The game is currently actively running. This
     * means that it is currently not paused. However it may have been paused before.
     * From this state the game can eather be paused or ended.
     * <li>{@link State#PAUSED PAUSED} The game is currently paused but has not ended
     * yet. It must have been running before (but that does not neccecarilly mean that
     * {@link #gameUpdate()} has been executed). From here the game can be resumed
     * going back into a running state. It may also be ended using {@link #end()}, however
     * this will first resume the game back into a running state and immediatly after
     * that enter the ended state.
     * <li>{@link State#ENDED ENDED} The game gas ended. Once in this state no other
     * state can be reached no more.
     * </ul>
     */
    public enum State {
        /**
         * The game is yet to begin.
         */
        NOT_STARTED,
        /**
         * The game is currently running (not paused).
         */
        RUNNING,
        /**
         * The game is paused but not yet finished.
         */
        PAUSED,
        /**
         * The game has finished.
         */
        ENDED
    }

    /**
     * Returns the current state of the game.
     * <p>A game has at any point in time exactly one of the following states:
     * <ul><li>{@link State#NOT_STARTED NOT_STARTED} The game is yet to begin.
     * At this point in time it cannot have been in any other state and therefore you
     * cannot go back to this state once the game has started.
     * <li>{@link State#RUNNING RUNNING} The game is currently actively running. This
     * means that it is currently not paused. However it may have been paused before.
     * From this state the game can eather be paused or ended.
     * <li>{@link State#PAUSED PAUSED} The game is currently paused but has not ended
     * yet. It must have been running before (but that does not neccecarilly mean that
     * {@link #gameUpdate()} has been executed). From here the game can be resumed
     * going back into a running state. It may also be ended using {@link #end()}, however
     * this will first resume the game back into a running state and immediatly after
     * that enter the ended state.
     * <li>{@link State#ENDED ENDED} The game gas ended. Once in this state no other
     * state can be reached no more.
     * </ul>
     * 
     * @return The state the game is currently in
     * @see State
     */
    public State getState() { return state; }


    /**
     * Starts the game either for the first time or from being paused.
     * <p>If the game is already running, this will do nothing.
     * 
     * @throws IllegalStateException If the game has already ended
     * @see #getState()
     */
    public void start() throws IllegalStateException {
        setState(State.RUNNING);
    }

    /**
     * Pauses the game after running.
     * <p>If the game is already paused, this will do nothing.
     * 
     * @throws IllegalStateException If the game has not started yet or has already ended
     * @see #getState()
     */
    public void pause() throws IllegalStateException {
        setState(State.PAUSED);
    }

    /**
     * Ends the game either after running or being paused.
     * <p>If the game is currently paused, the game will first be resumed and immediatly after that
     * ended.
     * <p>If the game is already running, this will do nothing.
     * 
     * @throws IllegalStateException If the game has not started yet
     * @see #getState()
     */
    public void end() throws IllegalStateException {
        setState(State.ENDED);
    }

    /**
     * Trys to set the games state to the given one. If the game is already in that state, this
     * will do nothing.
     * 
     * @param state The new state for the game
     * @throws IllegalStateException If the game cannot enter the given state from the current one
     * @see #getState()
     */
    private void setState(State state) throws IllegalStateException {
        if(this.state == state) return;

        if(this.state == State.ENDED) throw new IllegalStateException("The game cannot enter state " + state + " because it already ended");
        if(state == State.NOT_STARTED) throw new IllegalStateException("The game cannot enter NOT_STARTED once it was started");
        if(this.state == State.NOT_STARTED && state != State.RUNNING) throw new IllegalStateException("The game cannot enter state " + state + " before being started");

        if(this.state == State.PAUSED && state == State.ENDED) enterState(State.RUNNING);
        enterState(state);
    }

    /**
     * Enters the given state by invoking the appropriate {@code enter...()} method. If the game was
     * paused and should be ended, the {@link #enterRunFromPause()} method will be called first.
     * 
     * @param state The new state
     * @see #getState()
     */
    private void enterState(State state) {
        switch (state) {
            case NOT_STARTED: break;

            case PAUSED:
                enterPaused();
                break;
            
            case RUNNING:
                if(this.state == State.NOT_STARTED) enterRunFromStart();
                else if(this.state == State.PAUSED) enterRunFromPause();
                break;
            
            case ENDED:
                enterEnded();
                break;
        }
    }


    /**
     * Enters the {@link State#RUNNING RUNNING} state for the first time.
     * @see #getState()
     */
    private void enterRunFromStart() {
        state = State.RUNNING;
        startTime = lastResumeTime = System.nanoTime();
        if(clearWhenStarting) removeStartObjects();
        if(useScore && showScore) addScoreDisplay();
        if(showTimer) addTimer();
        if(USE_PAUSE_UI) addPauseButton();
        for(Consumer<Long> action : gameStartActions) action.accept(startTime);
    }

    /**
     * Enters the {@link State#RUNNING RUNNING} state after the game was paused.
     * @see #getState()
     */
    private void enterRunFromPause() {
        state = State.RUNNING;
        lastResumeTime = System.nanoTime();
        if(USE_PAUSE_UI) removePauseUI();
        if(clearAfterResumed) removePauseObjects();
        for(Consumer<Long> action : resumedActions) action.accept(System.nanoTime());
    }

    /**
     * Enters the {@link State#PAUSED PAUSED} state after the game was running.
     * @see #getState()
     */
    private void enterPaused() {
        state = State.PAUSED;
        timeElapsedUntilLastPause += System.nanoTime() - lastResumeTime;
        if(USE_PAUSE_UI) addPauseUI();
        for(Consumer<Long> action : pausedActions) action.accept(System.nanoTime());
    }

    /**
     * Enters the {@link State#ENDED ENDED} state after the game was running.
     * @see #getState()
     */
    private void enterEnded() {
        state = State.ENDED;
        endTime = System.nanoTime();
        timeElapsedUntilLastPause += endTime - lastResumeTime;
        if(autoSaveScore) saveScore();
        removePauseButton();
        for(Consumer<Double> action : gameEndActions) action.accept(getScore());
        if(useEndscreen) addRelative(Fade.fadeOut(UI_COLOR_1, 1), 0.5, 0.5);
    }


    /**
     * Adds an action that is executed after initialization in the first frame. As argument
     * the timestamp in nanoseconds will be passed.
     * <p>Example how to use this with lambda statements:
     * <pre>game.addCreatedAction(time -> System.out.println("Created at " + time));</pre>
     * where {@code game} is an instance of GameWorld.
     * 
     * @param action The action to add
     */
    public void addCreatedAction(Consumer<Long> action) {
        if(action == null) return;
        createdActions.add(action);
    }

    /**
     * Adds an action that is executed when the game starts. As argument
     * the timestamp in nanoseconds will be passed.
     * <p>Example how to use this with lambda statements:
     * <pre>game.addStartAction(time -> System.out.println("Started at " + time));</pre>
     * where {@code game} is an instance of GameWorld.
     * 
     * @param action The action to add
     */
    public void addGameStartAction(Consumer<Long> action) {
        if(action == null) return;
        gameStartActions.add(action);
    }

    /**
     * Adds an action that is executed whenever the game gets paused. As argument
     * the timestamp in nanoseconds will be passed.
     * <p>Example how to use this with lambda statements:
     * <pre>game.addPausedAction(time -> System.out.println("Paused at " + time));</pre>
     * where {@code game} is an instance of GameWorld.
     * 
     * @param action The action to add
     */
    public void addPausedAction(Consumer<Long> action) {
        if(action == null) return;
        pausedActions.add(action);
    }

    /**
     * Adds an action that is executed whenever the game gets resumed. As argument
     * the timestamp in nanoseconds will be passed.
     * <p><b>This will also be executed when the game ends after being paused!</b> Under normal
     * conditions (starting in state NOT_STARTED and ending in state ENDED) all 'Paused' actions
     * will be executed exactly as often as all 'Resumed' actions.
     * <p>Example how to use this with lambda statements:
     * <pre>game.addPausedAction(time -> System.out.println("Paused at " + time));</pre>
     * where {@code game} is an instance of GameWorld.
     * 
     * @param action The action to add
     */
    public void addResumedAction(Consumer<Long> action) {
        if(action == null) return;
        resumedActions.add(action);
    }

    /**
     * Adds an action that is executed when the game ends. As argument
     * the <b>players score</b> will be passed.
     * <p>Example how to use this with lambda statements:
     * <pre>game.addGameEndAction(score -> System.out.println("Game ended. Score: " + score));</pre>
     * where {@code game} is an instance of GameWorld.
     * 
     * @param action The action to add
     */
    public void addGameEndAction(Consumer<Double> action) {
        if(action == null) return;
        gameEndActions.add(action);
    }


    /**
     * Removes an action from being executed on the first frame.
     * 
     * @param action The action to remove
     */
    public void removeCreatedAction(Consumer<Long> action) {
        createdActions.remove(action);
    }

    /**
     * Removes an action from being executed on the game start.
     * 
     * @param action The action to remove
     */
    public void removeGameStartAction(Consumer<Long> action) {
        gameStartActions.remove(action);
    }

    /**
     * Removes an action from being executed when pausing.
     * 
     * @param action The action to remove
     */
    public void removePausedAction(Consumer<Long> action) {
        pausedActions.remove(action);
    }

    /**
     * Removes an action from being executed when resuming.
     * 
     * @param action The action to remove
     */
    public void removeResumedAction(Consumer<Long> action) {
        resumedActions.remove(action);
    }

    /**
     * Removes an action from being executed when the game ends.
     * 
     * @param action The action to remove
     */
    public void removeGameEndAction(Consumer<Double> action) {
        gameEndActions.remove(action);
    }



    // ---------------------------------------------------------------------------------------
    // Time tracking
    // ---------------------------------------------------------------------------------------



    /**
     * Returns the exact time stamp in nanoseconds when the game started.
     * 
     * @return The start time
     * @throws IllegalStateException If the game has not started yet
     */
    public final long getStartTime() throws IllegalStateException {
        if(state == State.NOT_STARTED) throw new IllegalStateException(TIME_REQUEST_BEFORE_START_STRING);
        return startTime;
    }

    /**
     * Returns the exact time stamp in nanoseconds when the game ended.
     * 
     * @return The end time
     * @throws IllegalStateException If the game has not ended yet
     */
    public final long getEndTime() throws IllegalStateException {
        if(state != State.ENDED) throw new IllegalStateException(TIME_REQUEST_BEFORE_END_STRING);
        return endTime;
    }

    /**
     * Returns the time that has elapsed while the game was actively running since the game was started.
     * 
     * @return The elapsed game time in nanoseconds
     * @throws IllegalStateException If the game has not started yet
     */
    public final long nanosElapsed() throws IllegalStateException {
        if(state == State.NOT_STARTED) throw new IllegalStateException(TIME_REQUEST_BEFORE_START_STRING);
        if(state == State.PAUSED) return timeElapsedUntilLastPause;
        return timeElapsedUntilLastPause + (System.nanoTime() - lastResumeTime);
    }

    /**
     * Returns the time that has elapsed while the game was actively running since the game was started.
     * 
     * @return The elapsed game time in seconds
     * @throws IllegalStateException If the game has not started yet
     */
    public final double timeElapsed() throws IllegalStateException {
        return nanosElapsed() / 1000000000d;
    }

    /**
     * Returns the remaining time in seconds if the game is limited in time
     * 
     * @return The elapsed game time in nanoseconds
     * @throws IllegalStateException If the game is not limited in time or has not started yet
     */
    public final double getRemainingTime() throws IllegalStateException {
        if(!limitTime) throw new IllegalStateException(NO_TIME_LIMIT_STRING);
        return (gameDuration - nanosElapsed()) / 1000000000d;
    }



    // ---------------------------------------------------------------------------------------
    // Build-in UI (in-game)
    // ---------------------------------------------------------------------------------------



    private Text startCountdown = null;

    /**
     * Creates and adds a text displaying the time until the game start.
     */
    private void addStartCountdown() {
        addRelative(startCountdown = new Text((int)Math.ceil(startDelay / 1000000000d) + "", 25), 0.5, 0.5);
    }

    private Text timer = null;

    /**
     * Creates and adds a text displaying either the time elapsed or the remaining time.
     */
    private void addTimer() {
        if(limitTime) addRelative(timer = new Text(formatDouble(getRemainingTime()), 25), 0.5, 0.2);
        else addRelative(timer = new Text(formatDouble(timeElapsed()), 25), 0.5, 0.2);
    }


    private Text scoreText = null;

    /**
     * Creates and and adds a text displaying the players current score.
     */
    private void addScoreDisplay() {
        addRelative(scoreText = new Text(scoreTitle + ": " + Geometry.floor(getScore(), -2)), 0.5, 0, 0, 20);
    }


    TextButton pauseButton;

    /**
     * Creates and adds a pause button.
     */
    private void addPauseButton() {
        addRelative(pauseButton = new TextButton(new Text("Pause")).addClickAction(() -> pause()), 1, 0, -27, 12);
    }

    /**
     * Removes the pause button.
     */
    private void removePauseButton() {
        removeObject(pauseButton);
    }


    UIPanel pausePanel;

    /**
     * Creates and adds a pause ui.
     */
    private void addPauseUI() {
        addRelative(pausePanel = new PausePanel(), 0.5, 0.5);
    }

    /**
     * Removes the pause ui.
     */
    private void removePauseUI() {
        pausePanel.remove();
    }

    /**
     * a simple pause interface.
     */
    private class PausePanel extends UIPanel {

        /**
         * Creates a new pause interface.
         */
        public PausePanel() {
            super(GameWorld.this.getWidth(), 200, UI_COLOR_1);
            addRelative(new PauseUIText("Paused", 25, TEXT_COLOR_3), 0.5, 0.2);
            addRelative(new PauseUIText(getGameStateString(), TEXT_COLOR_1), 0.5, 0.5);
            if(allowRestart) {
                addRelative(new PauseUIButton(new Text("Quit")).addClickAction(() -> quit()), 0.5, 0.8, -100, 0);
                addRelative(new PauseUIButton(new Text("Restart")).addClickAction(() -> restart()), 0.5, 0.8);
                addRelative(new PauseUIButton(new Text("Resume")).addClickAction(() -> start()), 0.5, 0.8, 100, 0);
            }
            else {
                addRelative(new PauseUIButton(new Text("Quit")).addClickAction(() -> quit()), 0.5, 0.8, -50, 0);
                addRelative(new PauseUIButton(new Text("Resume")).addClickAction(() -> start()), 0.5, 0.8, 50, 0);
            }
            addRelative(new PauseUIBackground(), 0.5, 0.5);
        }

        /**
         * A text that is painted above other ui.
         */
        private class PauseUIText extends Text {

            public PauseUIText(String content, Color color) {
                super(content, color);
            }
            public PauseUIText(String content, int fontSize, Color color) {
                super(content, fontSize);
            }
        }

        /**
         * A ui panel that is painted above other ui.
         */
        private class PauseUIBackground extends UIPanel {

            public PauseUIBackground() {
                super(GameWorld.this, new Color(0, 0, 0, 80));
            }
        }

        /**
         * A button that is painted above other buttons.
         */
        private class PauseUIButton extends TextButton {

            public PauseUIButton(Text text) {
                super(text);
                setMinWidth(80).setMinHeight(30);
            }
        }
    }

    /**
     * Returns a descriptive, formatted string that represents the current game state. This
     * will be displayed e.g. in the pause menu. Make sure to include newlines!
     * <p>By default this returns the players score with a title or an empty string if the
     * score is not used or not meant to be displayed.
     * 
     * @return A descriptive string
     */
    public String getGameStateString() {
        if(useScore && showScore) return scoreTitle + ": " + Geometry.floor(getScore(), -2);
        return "";
    }

    /**
     * Formattes the double into an easy-to-read string by limiting the past-comma digits to
     * 2 and converting it into a string.
     * 
     * @param x The double to format
     * @return The formatted text
     */
    private String formatDouble(double x) {
        return new DecimalFormat("#0.00").format(x).replaceAll(",", ".");
    }



    // ---------------------------------------------------------------------------------------
    // Build-in UI (in-game)
    // ---------------------------------------------------------------------------------------



    /**
     * A simple result screen displaying the players score and highscore, and showing a restart, quit,
     * and, if set so, a highscores button linking to a {@link Highscores} world.
     */
    private class Result extends UIWorld {

        /**
         * Creates a new result world.
         */
        public Result() {
            super(
                GameWorld.this.getWidth() * GameWorld.this.getCellSize(),
                GameWorld.this.getHeight() * GameWorld.this.getCellSize(),
                1
            );
            if(SHOW_FPS) addFps();
            colorBackground(UI_COLOR_1);
            addRelative(new Text(endingText, 30), 0.5, 0.15);
            if(useScore) {
                addRelative(new Text(scoreTitle + ": " + formatDouble(getScore()), 20), 0.5, 0.5);
                addRelative(new Text("Personal highscore: " + formatDouble(getHighscore()), 16), 0.5, 0.6);
            }
            if(allowRestart) {
                addRelative(new TextButton(new Text("Quit")).setMinWidth(80).setMinHeight(30).addClickAction(() -> quit()), 0.5, 0.9, -50, 0);
                addRelative(new TextButton(new Text("Restart")).setMinWidth(80).setMinHeight(30).addClickAction(() -> restart()), 0.5, 0.9, 50, 0);
            }
            else addRelative(new TextButton(new Text("Quit")).setMinWidth(80).setMinHeight(30).addClickAction(() -> quit()), 0.5, 0.9);
            if(linkHighscores) addRelative(new TextButton(new Text("Highscores")).setMinWidth(80).setMinHeight(30).addClickAction(() -> openHighscores()), 0.1, 0.9);
            addRelative(Fade.fadeIn(UI_COLOR_1, 1.5), 0.5, 0.5);
        }
    }


    /**
     * A simple highscores world showing the personal highscore and the top scores
     * using {@link com.github.rccookie.greenfoot.ui.advanced.Scoreboard Scoreboard}.
     * <p>Includes a 'quit' button and, if allowed, a 'restart button'
     */
    private class Highscores extends UIWorld {

        /**
         * Creates a new Highscore world.
         */
        public Highscores() {
            super(
                GameWorld.this.getWidth() * GameWorld.this.getCellSize(),
                GameWorld.this.getHeight() * GameWorld.this.getCellSize(),
                1
            );
            if(SHOW_FPS) addFps();
            colorBackground(UI_COLOR_1);
            addRelative(new Text(endingText, 30), 0.5, 0.15);

            Scoreboard.ScorePanel.SCORE_PRECITION = 100;
            addRelative(new Scoreboard(getWidth() - 150, getHeight() - 180), 0.5, 0.55);

            addRelative(new TextButton(new Text("Quit")).setMinWidth(80).setMinHeight(30).addClickAction(() -> quit()), 0.1, 0.9);
            if(allowRestart) addRelative(new TextButton(new Text("Restart")).setMinWidth(80).setMinHeight(30).addClickAction(() -> restart()), 0.9, 0.9);

            addRelative(Fade.fadeIn(UI_COLOR_1, 0.5), 0.5, 0.5);
        }
    }



    // ---------------------------------------------------------------------------------------
    // Build-in highscore system
    // ---------------------------------------------------------------------------------------



    /**
     * Sets the players current score to the given one.
     * 
     * @param score The new score
     */
    public void setScore(double score) {
        this.score = score;
        if(scoreText != null) scoreText.setTitle(scoreTitle + ": " + Geometry.floor(getScore(), -2));
    }

    /**
     * Adds the given amount of score onto the players score.
     * 
     * @param amount The amount of score to add
     */
    public void addScore(double amount) {
        setScore(getScore() + amount);
    }

    /**
     * Resets the players score to 0.
     */
    public void resetScore() {
        setScore(0);
    }

    /**
     * Returns the players current score.
     */
    public double getScore() {
        return score;
    }


    /**
     * Returns the personal highscore.
     * <p>If the highscore could not be loaded, {@code 0} will be returned.
     * 
     * @return The personal highscore
     */
    public double getHighscore() {
        try{
            return UserInfo.getMyInfo().getScore() / (double)SCORE_PRECITION;
        }
        catch(Exception e) { return 0; }
    }

    /**
     * Returns the global highscore.
     * <p>If the highscore could not be loaded, {@code 0} will be returned.
     * 
     * @return The global highscore
     */
    public double getGlobalHighscore() {
        try{
            return ((UserInfo)UserInfo.getTop(1).get(0)).getScore() / (double)SCORE_PRECITION;
        }
        catch(Exception e) { return 0; }
    }

    /**
     * Returns the players rank in the global highscore.
     * <p>If the rank could not be loaded, {@code 0} will be returned.
     */
    public int getRank() {
        try{
            return UserInfo.getMyInfo().getRank();
        }
        catch(Exception e) { return 0; }
    }

    /**
     * Saves the players score to the online highscore (if the current score is a new highscore).
     * 
     * @return Weather the score could be synced.
     */
    public boolean saveScore() {
        try{
            UserInfo user = UserInfo.getMyInfo();
            if(getScore() * SCORE_PRECITION > user.getScore()) {
                user.setScore((int)(getScore() * SCORE_PRECITION));
                return user.store();
            }
            return true;
        }
        catch(Exception e) {
            System.out.println("Score could not be synced");
            return false;
        }
    }
}
