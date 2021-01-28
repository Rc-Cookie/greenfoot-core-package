package com.github.rccookie.greenfoot.core;

import com.github.rccookie.common.event.Time;

/**
 * A time that cannot me updated using {@link #update()} to
 * prevent unwanted updating. Use {@link #actualUpdate()} instead!
 */
class NoExternalUpdateTime extends Time {

    /**
     * Does nothing.
     */
    @Override
    public void update() { }

    /**
     * Updates this time.
     */
    void actualUpdate() {
        super.update();
    }
}
