package com.github.rccookie.greenfoot.widgets;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.github.rccookie.common.geometry.Vector2D;

import greenfoot.GreenfootImage;

public class Script extends Widget {

    private final Consumer<Script> onUpdate;

    public Script(Runnable onUpdate) {
        this(s -> onUpdate.run());
    }

    public Script(Consumer<Script> onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    protected void update(Size maxSize) {
        onUpdate.accept(this);
    }



    @Override
    protected final GreenfootImage createImage(Size maxSize) {
        return null;
    }

    @Override
    final List<Widget> getChildren() {
        return Collections.emptyList();
    }

    @Override
    final Widget addChild(Widget child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Widget addOnRender(Runnable method) {
        return this;
    }

    @Override
    final boolean contains(Vector2D loc) {
        return false;
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
    final GreenfootImage getCachedRender(GreenfootImage lastRender) {
        return null;
    }

    @Override
    final List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        return Collections.emptyList();
    }

    @Override
    final Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        return null;
    }

    @Override
    public final int getHeight(Size maxSize) {
        return 0;
    }

    @Override
    public final int getWidth(Size maxSize) {
        return 0;
    }

    @Override
    final GreenfootImage getLastRenderedImage() {
        return null;
    }
}
