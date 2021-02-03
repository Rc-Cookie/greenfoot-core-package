package com.github.rccookie.greenfoot.ui.advanced;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.greenfoot.ui.basic.Text;
import com.github.rccookie.greenfoot.ui.basic.TextButton;
import com.github.rccookie.greenfoot.ui.util.Design;
import com.github.rccookie.greenfoot.ui.util.Theme;

/**
 * The fps display shows the current fps in one of two modes: either
 * in realtime (based on only the last frame) or the average (of the
 * last second) (as calculated in packages.tools.Time). When clicked
 * on it it switches modes.
 * 
 * @see com.github.rccookie.common.tools.Time
 * @author RcCookie
 * @version 1.0
 */
public class FpsDisplay extends TextButton {

    private static final int STATISTICS_SIZE_MAX = 100;

    /**
     * Weather the output is the average(=true) or realtime (=false).
     */
    boolean stableMode;


    private final ArrayList<Integer> statistics = new ArrayList<>();


    /**
     * Constructs a new fps display in stable mode.
     */
    public FpsDisplay() {
        this(true);
    }

    /**
     * Constructs a new fps display in the given mode.
     * 
     * @param stableMode Weather the display shows stable fps by default
     */
    public FpsDisplay(boolean stableMode) {
        super(new Text("FPS: --"));
        setUseBigBorder(false);
        setMaxWidth(Integer.MAX_VALUE);
        setDesign(new Design(getDesign().theme().modified(0, Theme.C_TRANSPARENT), getDesign().textTheme()));
        this.stableMode = stableMode;
        addClickAction(() -> switchMode());
        time.addSecondListener(delta -> {
            getText().setContent("FPS: " + currentModeFps());
            statistics.add(stableFps());
            if(statistics.size() > STATISTICS_SIZE_MAX) statistics.remove(0);
        });
    }


    /**
     * Runs the time object and updates the text output.
     */
    @Override
    protected void physicsUpdate() {
        super.physicsUpdate();
        if(!stableMode) getText().setContent("FPS: " + currentModeFps());
    }


    /**
     * Returns the current realtime fps.
     * 
     * @return The current realtime fps
     */
    public int fps() {
        return time.fps();
    }

    /**
     * Returns the current average fps.
     * 
     * @return The current average fps
     */
    public int stableFps() {
        return time.stableFps();
    }

    /**
     * Returns the fps in the current mode.
     * 
     * @return The current fps in the current mode
     */
    public int currentModeFps() {
        if(stableMode) return stableFps();
        else return fps();
    }


    /**
     * Returns the duration of the last frame as a fraction of a second.
     * 
     * @return The duration of the last frame as a fraction of a second
     * @see packages.tools.Time.deltaTime()
     */
    public double deltaTime(){
        return time.deltaTime();
    }
    

    /**
     * Switches fps mode.
     */
    public void switchMode() {
        stableMode = !stableMode;
    }


    /**
     * Returns the stable mode statistics of the last 100 seconds.
     * 
     * @return The stable mode statistics
     */
    public List<Integer> getStatistics() {
        return statistics;
    }
}
