package com.github.rccookie.greenfoot.widgets;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import greenfoot.GreenfootImage;

/**
 * A Detail is a type of widget that cannot contain any
 * child widgets, for example a {@link Text}.
 */
public abstract class Visual extends Widget {

    @Override
    protected abstract GreenfootImage createImage(Size maxSize);

    @Override
    final List<Widget> getChildren() {
        return Collections.emptyList();
    }

    @Override
    final List<Widget> getAllChildren() {
        return getChildren();
    }

    @Override
    final Widget addChild(Widget child) {
        throw new UnsupportedOperationException();
    }

    @Override
    final Widget removeChild(Widget child) {
        throw new UnsupportedOperationException();
    }

    @Override
    GreenfootImage getRenderedImage(Size maxSize) {
        if(!isVisible()) return null;
        if(!childModified && Objects.equals(lastSize, maxSize)) return getCachedRender(lastRender);

        modified = childModified = false;
        lastSize = maxSize;
        modifyLock = true;
        clearStateCache();

        lastRender = createImage(maxSize);

        for(Runnable method : onRender) method.run();

        modifyLock = false;
        return lastRender;
    }

    @Override
    final List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        return Collections.emptyList();
    }

    @Override
    final Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    final <W> W find(Class<W> type) {
        return null;
    }

    @Override
    final Widget find(String id) {
        return null;
    }

    @Override
    final void runUpdate(Size maxSize) {
        update(maxSize);
    }
}
