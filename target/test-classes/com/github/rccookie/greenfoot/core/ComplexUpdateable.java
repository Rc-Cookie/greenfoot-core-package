package com.github.rccookie.greenfoot.core;

import com.github.rccookie.common.util.Updateable;

/**
 * Base class for both {@link GameObject} and {@link Map}.
 * 
 * @author RcCookie
 * @version 1.0
 */
public abstract class ComplexUpdateable implements Updateable {

    /**
     * An update that should be called before the other update calls.
     */
    protected abstract void earlyUpdate();

    /**
     * An update that should be called after the other update calls.
     */
    protected abstract void lateUpdate();

    /**
     * An update that should be called after the {@link #update()} method
     * containing physics and similar calculations.
     */
    protected abstract void physicsUpdate();

    /**
     * An update called before the {@link #update()} method is intended for
     * some internal functionallity updates.
     */
    abstract void internalUpdate();
}
