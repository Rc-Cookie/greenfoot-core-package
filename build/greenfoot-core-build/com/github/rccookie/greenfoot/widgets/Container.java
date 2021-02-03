package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A container is an invisible widget that can contains child
 * widgets. It is the super class to all widgets that can carry
 * other child widgets. Subclasses may add visibile elements to
 * this or implement {@link Structure} and may stay
 * invisible.
 */
public abstract class Container extends Widget {

    private final List<Widget> children = new ArrayList<>();

    /**
     * Creates a new container with the given children.
     * 
     * @param children The containers children
     */
    public Container(Widget... children) {
        addChildren(children);
    }

    @Override
    protected List<Widget> getChildren() {
        return Collections.unmodifiableList(new ArrayList<>(children));
    }

    // To make protected
    @Override
    protected List<Widget> getAllChildren() {
        return super.getAllChildren();
    }

    // To make protected
    @Override
    protected Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        return super.getChildSize(child, children, maxSize);
    }

    // To make protected
    @Override
    protected boolean isDynamic() {
        return super.isDynamic();
    }

    // To make protected
    @Override
    protected Container setDynamic(boolean flag) {
        super.setDynamic(flag);
        return this;
    }

    @Override
    public Container addChild(Widget child) {
        modify(() -> {
            children.add(child);
            super.addChild(child);
        });
        return this;
    }

    public Container addChildren(Widget... children) {
        if(children != null)
            for(Widget child : children) addChild(child);
        return this;
    }

    public Container insertChild(int index, Widget child) {
        modify(() -> {
            children.add(index, child);
        });
        return this;
    }

    @Override
    public Container removeChild(Widget child) {
        modify(() -> {
            children.remove(child);
            super.removeChild(child);
        });
        return this;
    }

    public Container removeChild(Class<?> type) {
        List<Widget> children = getChildren();
        Optional<Widget> child = children.stream().filter(c -> type.isInstance(c)).findFirst();
        if(child.isPresent()) modify(() -> removeChild(child.get()));
        return this;
    }

    public int childCount() {
        return getChildren().size();
    }

    protected void setChildren(List<Widget> children) {
        while(!children.isEmpty()) removeChild(children.get(0));
        if(children != null) for(Widget child : children) addChild(child);
    }

    // To make public
    /**
     * @param type The type of child to search for
     */
    @Override
    public <W> W find(Class<W> type) {
        return super.find(type);
    }

    // To make public
    /**
     * @param id The id to search for
     */
    @Override
    public Widget find(String id) {
        return super.find(id);
    }

    // To make protected
    @Override
    protected List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        return super.getChildrenRenderTasks(maxSize, children);
    }
}
