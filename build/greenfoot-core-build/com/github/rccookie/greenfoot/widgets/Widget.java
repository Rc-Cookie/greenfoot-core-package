package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.common.geometry.Vectors;
import com.github.rccookie.greenfoot.event.Input;

import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;
import greenfoot.World;

/**
 * A widget is an user interface element. It is <i>stackable</i>,
 * meaning it can contain one or more other widgets that will be
 * rendered on top of itself.
 * <p>Not every widget is visible. Non-visible widgets should be
 * implementing {@link Structure}. StructureWidgets don't
 * have a design themselfes but affect how other widgets are
 * stacked on top of them. For example, an {@link Align} can make
 * its child widget align with the top left corner instead of
 * being centered as by default.
 * <p>Visible widgets that are not a StructureWidget should be
 * fully resizable. More precisely, the size of any
 * non-StructureWidget is defined by the maxSize in which it is
 * rendered, usually defined by its parent widget. It does not
 * control its size itself and should be able to be rendered to
 * any requested size.
 * <p>If a child widget should be smaller than its parent, there
 * should be used an appropriate StructureWidget. For example,
 * if the child widget of some widget should be at most
 * {@code 100} * {@code 100} pixels big, a {@link Dimension} can
 * be used to limit its size:
 * <pre>
 * new SomeWidget(
 *     new Dimension(
 *         100,
 *         100,
 *         new SomeSmallWidget()
 *     )
 * )
 * </pre>
 * Widgets themselfes are no actors, they cannot be added into a
 * Greenfoot world. To display a widget stack the class
 * {@link WidgetHolder} should be used. It holds and displays a
 * widget and will have live updates if needed. It also
 * functions as a bridge to the Greenfoot world for widgets to
 * access their world locaiton and be able to interact with
 * mouse input.
 */
public abstract class Widget {

    private Widget parent; // Used to notify about child modifications
    boolean modified = true; // Weather this widget itself has been modified
    boolean childModified = true; // Weather this or a child widget was modified
    boolean modifyLock = false; // Prevents unintentional modification during rendering
    GreenfootImage lastRender = null; // Cached endproduct
    GreenfootImage lastImage = null; // Cached image of this widget without children
    Size lastSize = null; // Last render maxSize to ensure it has not changed
    private boolean dynamic = true; // Weather transparent edges should be cut
    private boolean visible = true; // Weather it should be rendered and updated

    private final HashMap<Integer, GreenfootImage> stateCache = new HashMap<>();

    final List<Runnable> onRender = new ArrayList<>(); // Methods to run after rerendering

    WidgetHolder holder = null; // If != null a widget holder holds this widget. Used for location
    private Vector2D offset = Vectors.immutableVector(new Vector2D()); // Offset to location of parent
    private String id = null; // Identifier of instance

    /**
     * Returns the image of this widget with all its children's images
     * drawn on top. If neccecary it will be rerendered.
     * 
     * @param maxSize The maxSize in which this widget is being rendered
     * @return This rendered image of this widget
     */
    GreenfootImage getRenderedImage(Size maxSize) {
        if(!childModified && !modified && Objects.equals(lastSize, maxSize)) return getLastRenderedImage();

        if(!isVisible()) {
            lastRender = null;
            return getLastRenderedImage();
        }

        childModified = false;
        modifyLock = true;
        clearStateCache();

        if(modified || !Objects.equals(lastSize, maxSize)) {
            modified = false;
            lastSize = maxSize;
            lastRender = createImage(maxSize);
            lastImage = lastRender != null ? new GreenfootImage(lastRender) : null;
        }
        else lastRender = lastImage != null ? new GreenfootImage(lastImage) : null;

        boolean defaultImage = lastRender == null;
        if(defaultImage) {
            lastRender = new GreenfootImage(maxSize.width, maxSize.height);
        }

        List<RenderTask> tasks = getChildrenRenderTasks(maxSize, getChildren());
        for(int i=0; i<tasks.size();) {
            if(tasks.get(i) == null) tasks.remove(i);
            else i++;
        }

        if(defaultImage && isDynamic() && !tasks.isEmpty()) {
            int minX = maxSize.width / 2, minY = maxSize.height / 2, maxX = minX, maxY = minY;

            for(RenderTask task : tasks) {
                if(task.x < minX) minX = task.x;
                if(task.y < minY) minY = task.y;
                if(task.x + task.image.getWidth() > maxX) maxX = task.x + task.image.getWidth();
                if(task.y + task.image.getHeight() > maxY) maxY = task.y + task.image.getHeight();
            }

            minX = Math.min(minX, maxSize.width - maxX);
            minY = Math.min(minY, maxSize.height - maxY);

            if(minX < 0) minX = 0;
            if(minY < 0) minY = 0;

            if(minX != 0 || minY != 0) lastRender = new GreenfootImage(maxSize.width - 2 * minX, maxSize.height - 2 * minY);

            for(RenderTask task : tasks) lastRender.drawImage(task.image, task.x - minX, task.y - minY);
        }
        else for(RenderTask task : tasks) lastRender.drawImage(task.image, task.x, task.y);
        clearStateCache(); // May have been created during children rendering

        for(Runnable method : onRender) method.run();

        modifyLock = false;
        return getLastRenderedImage(); // To enable post processing and state-dependent returning using getCachedRender
    }

    GreenfootImage getLastRenderedImage() {
        return getCachedRender(lastRender);
    }

    /**
     * Returns a <i>mutable</i> list of the rendering tasks of the children.
     * By default all children will be rendered centered.
     * 
     * @param maxSize The maxSize in which this widget was rendered.
     * @param children The children that should be rendered
     * @param onto The image to render the children onto
     */
    List<RenderTask> getChildrenRenderTasks(Size maxSize, List<Widget> children) {
        final List<RenderTask> tasks = new ArrayList<>();
        for(int i=0; i<children.size(); i++) {
            GreenfootImage childRender = children.get(i).getRenderedImage(getChildSize(children.get(i), children, maxSize));
            if(childRender != null) {
                tasks.add(new RenderTask(
                    childRender,
                    getWidth(maxSize) / 2 - childRender.getWidth() / 2,
                    getHeight(maxSize) / 2 - childRender.getHeight() / 2
                ));
            }
            children.get(i).setOffset(null);
        }
        return tasks;
    }

    GreenfootImage getCachedRender(GreenfootImage lastRender) {
        return lastRender;
    }

    protected GreenfootImage getStateCache(int state, Supplier<GreenfootImage> generator) {
        if(stateCache.containsKey(state)) return stateCache.get(state);
        GreenfootImage supplied = generator.get();
        stateCache.put(state, supplied);
        return supplied;
    }

    protected void cacheState(int state, GreenfootImage rendering) {
        stateCache.put(state, rendering);
    }

    void clearStateCache() {
        stateCache.clear();
    }

    /**
     * Recreates this widgets own image without its children's images
     * drawn onto it from the current state.
     * <p>By default this method simply returns {@code null} which
     * causes all its children to be rendered in the maximum area
     * allowed by the maxSize.
     * 
     * @param width The (maximum) width that the image should have
     * @param height The (maximum) height that the image should have
     * @return The image of this widget
     */
    protected abstract GreenfootImage createImage(Size maxSize);

    Size getChildSize(Widget child, List<Widget> children, Size maxSize) {
        return maxSize;
    }

    /**
     * The parent widget that contains this widget. This returns only
     * {@code null} if this widget is the root of this widget stack.
     * 
     * @return This widget's parent widget
     */
    public Widget getParent() {
        return parent;
    }

    /**
     * Returns the width of this widget under the given maxSize.
     * 
     * @param maxSize The maxSize in which the widget was drawn
     * @return This widgets width
     */
    public int getWidth(Size maxSize) {
        GreenfootImage render = getRenderedImage(maxSize);
        return render != null ? render.getWidth() : 0;
    }

    /**
     * Returns the height of this widget under the given maxSize.
     * 
     * @param maxSize The maxSize in which the widget was drawn
     * @return This widgets height
     */
    public int getHeight(Size maxSize) {
        GreenfootImage render = getRenderedImage(maxSize);
        return render != null ? render.getHeight() : 0;
    }

    /**
     * Returns the angle of this widget. Since widgets cannot be rotated,
     * this will return the angle of the widget holder, or {@code 0} if
     * there is no.
     * 
     * @return The angle of this widget
     */
    public double getAngle() {
        if(holder != null) return holder.getAngle();
        Widget parent = getParent();
        return parent != null ? parent.getAngle() : 0;
    }

    /**
     * Returns the location of the center of this widget in world
     * coordinates. If there is no widget holder, this will return
     * {@code null}.
     * 
     * @return The location of this widget
     */
    public Vector2D getLocation() {
        if(holder != null) return holder.getLocation();
        Widget parent = getParent();
        if(parent == null || lastSize == null) return null;
        Vector2D parentLoc = parent.getLocation();
        return parentLoc != null ? parentLoc.add(offset) : null;
    }

    /**
     * Returns the offset of this widget to the parent widget's center.
     * Will not be {@code null}.
     * 
     * @return This widgets offset
     */
    protected Vector2D getOffset() {
        return offset;
    }

    /**
     * Sets the offset of this widget to its parent.
     * <p><b>This is an internal method</b> and does not affect any drawing!
     * It is exclusively used for calculating the location and should
     * only be used if the widget is a {@link Structure} and
     * does not render its children centered!
     * 
     * @param offset The new offset
     */
    protected void setOffset(Vector2D offset) {
        this.offset = offset != null ? Vectors.immutableVector(offset.clone()) : new Vector2D();
    }

    /**
     * Weather this widget's size is dynamic, meaning that it will size its
     * rendering only as big as needed. This has no direct visual effect but
     * can effect the collision of a widget. {@code true} by default.
     * 
     * @return Weather this widget is dynamic
     */
    boolean isDynamic() {
        return dynamic;
    }

    /**
     * Sets weather this widget is dynamic.
     * 
     * @param flag Weather this widget should be dynamic
     */
    Widget setDynamic(boolean flag) {
        if(dynamic == flag) return this;
        modify(() -> dynamic = flag);
        return this;
    }

    /**
     * Returns weather this widget is currently visible and updated.
     * 
     * @return The visibility state of this widget
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets weather this widget should be visible and therefore be updated
     * or not.
     * 
     * @param flag
     * @return This widget
     */
    public Widget setVisible(boolean flag) {
        if(visible == flag) return this;
        modify(() -> visible = flag);
        return this;
    }

    /**
     * Returns the customly set identifier of this widget. The default is
     * {@code null}
     * 
     * @return The id of this widget.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this widget.
     * 
     * @param id The new id
     */
    public Widget setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns this widget casted to the specified class. Will throw an
     * {@link ClassCastException} if that is not possible.
     * 
     * @param <W> The type of the class to cast to
     * @param widgetClass The class to cast to
     * @return This widget, as the specified class
     */
    @SuppressWarnings("unchecked")
    public final <W> W as(Class<W> widgetClass) {
        return (W)this;
    }

    /**
     * Returns a child of this widget of the specified type, or {@code null}.
     *
     * @param type The type of child to search for
     * @return A child of the specified type, or {@code null}
     */
    @SuppressWarnings("unchecked")
    <W> W find(Class<W> type) {
        Objects.requireNonNull(type);
        for(Widget child : getAllChildren()) if(type.isInstance(child)) return (W)child;
        return null;
    }

    /**
     * Returns a child of this widget with the specified id, or {@code null}.
     * 
     * @param id The id to search for
     * @return A child with the specified id, or {@code null}
     */
    Widget find(String id) {
        for(Widget child : getAllChildren()) if(Objects.equals(id, child.getId())) return child;
        return null;
    }

    /**
     * Returns all children of this widget. The returned set must not be
     * or contain {@code null} elements but may be empty.
     * 
     * @return All child widgets of this widget ordered in drawing order
     */
    abstract List<Widget> getChildren();

    /**
     * Returns a set of all child widgets of this widget and all their
     * children and so on.
     * 
     * @return All children of this widget stack
     */
    List<Widget> getAllChildren() {
        List<Widget> children = new ArrayList<>();
        List<Widget> ownChildren = getChildren();
        children.addAll(ownChildren);
        for(Widget ownChild : ownChildren)
            children.addAll(ownChild.getAllChildren());
        return children;
    }

    protected WidgetHolder getHolder() {
        if(holder != null) return holder;
        Widget parent = getParent();
        if(parent == null) return null;
        return parent.getHolder();
    }

    protected Widget getRoot() {
        Widget parent = getParent();
        if(parent == null) return this;
        return parent.getRoot();
    }

    /**
     * Returns the widget that may be itself or one of its children
     * that is currently being touched my the mouse, or {@code null}.
     * 
     * @param maxSize The current render maxSize
     * @return The touched child or itself or {@code null}
     */
    public Widget mouseTouchedChild(Size maxSize) {
        Vector2D mouseLoc = getMouseLoc();
        return mouseLoc != null && mouseTouching() ? getTouching(mouseLoc, maxSize) : null;
    }

    /**
     * Returns weather the mouse is currently touching this widget or
     * one of its child widgets or their child widgets and so on.
     * 
     * @param maxSize The current render maxSize
     * @return {@code true} if the mouse is contained within this widget
     *         and it is on top at that point
     */
    public boolean mouseTouching() {
        Vector2D mouseLoc = getMouseLoc();
        if(mouseLoc == null || !contains(mouseLoc)) return false;
        Widget parent = getParent();
        if(parent == null) return true;
        return parent.isChildOnTop(this, mouseLoc);
    }

    private boolean isChildOnTop(Widget target, Vector2D loc) {
        List<Widget> children = getChildren();
        for(int i=children.size()-1; i>=0; i--) {
            if(children.get(i) == target) break;
            if(children.get(i).isVisible() && children.get(i).contains(loc)) return false;
        }
        Widget parent = getParent();
        if(parent == null) return true;
        return parent.isChildOnTop(this, loc);
    }

    /**
     * Returns weather the mouse is currently touching this exact widget.
     * 
     * @param maxSize The current render maxSize
     * @return {@code true} if the mouse is contained within this widget,
     *         is not touching any child widgets and it is on top at that
     *         point
     */
    public boolean mouseTouchingThis(Size maxSize) {
        if(!mouseTouching()) return false;
        Vector2D mouseLoc = getMouseLoc();
        if(mouseLoc == null) return false;
        List<Widget> children = getChildren();
        for(int i=children.size()-1; i>=0; i--) {
            if(children.get(i).contains(mouseLoc)) return false;
        }
        return false;
    }

    private Vector2D getMouseLoc() {
        MouseInfo mouse = Input.mouseInfo();
        if(mouse == null) return null;
        WidgetHolder holder = getHolder();
        if(holder == null) return null;
        World world = holder.getWorld();
        if(world == null) return null;
        return new Vector2D(mouse.getX(), mouse.getY()).scale(world.getCellSize());
    }

    /**
     * Returns the widget from the whole stack that is currently at the
     * specified location, or {@code null}.
     * 
     * @param x The x coordinate of the location to check for
     * @param y The y coordinate of the location to check for
     * @param maxSize The current render maxSize
     * @return The widget on top at that location, or {@code null}
     */
    public Widget getTouched(double x, double y, Size maxSize) {
        return getTouched(new Vector2D(x, y), maxSize);
    }

    /**
     * Returns the widget from the whole stack that is currently at the
     * specified location, or {@code null}.
     * 
     * @param loc The location to check for
     * @param maxSize The current render maxSize
     * @return The widget on top at that location, or {@code null}
     */
    public Widget getTouched(Vector2D loc, Size maxSize) {
        return getRoot().getTouching(loc, maxSize);
    }

    private Widget getTouching(Vector2D loc, Size maxSize) {
        if(!contains(loc)) return null;
        List<Widget> children = getChildren();
        for(int i=children.size()-1; i>=0; i--) {
            Widget touching = children.get(i).getTouching(loc, maxSize);
            if(touching != null) return touching;
        }
        return this;
    }

    boolean contains(Vector2D loc) {
        if(!isVisible()) return false;
        Vector2D center = getLocation();
        GreenfootImage image = getLastRenderedImage();
        if(image == null) return false;
        double hWidth = image.getWidth() * 0.5, hHeight = image.getHeight() * 0.5;
        return (
            loc.x() >= center.x() - hWidth &&
            loc.x() < center.x() + hWidth &&
            loc.y() >= center.y() - hHeight &&
            loc.y() < center.y() + hHeight
        );
    }

    /**
     * Called once per frame. Default does nothing. Children are called
     * immediatly after this widget, in rendering order from first (bottom)
     * to last (top)
     * 
     * @param maxSize The current maxSize, mainly needed to access a lot
     *                of information
     */
    protected void update(Size maxSize) { }

    void runUpdate(Size maxSize) {
        update(maxSize);
        List<Widget> children = getChildren();
        for(Widget child : children) {
            if(child.isVisible()) child.runUpdate(getChildSize(child, children, maxSize));
        }
    }

    /**
     * Prints this widget stack into the standart output stream.
     */
    public void printStack() {
        printStack(false);
    }

    /**
     * Prints this widget stack into the standart output stream.
     * 
     * @param withState Weather the state of each widget should be shown
     */
    public void printStack(boolean withState) {
        printStack(withState, new ArrayList<>());
    }

    private void printStack(boolean withState, List<Boolean> indent) {
        StringBuilder line = new StringBuilder();
        for(int i=0; i<indent.size(); i++) {
            if(i == indent.size() - 1) line.append((indent.get(i) ? '\u251C' : '\u2514') + "\u2500");
            else line.append(indent.get(i) ? "\u2502 " : "  ");
        }
        line.append(this);
        if(withState && childModified) line.append(" (needs rendering)");
        System.out.println(line);
        List<Widget> children = getChildren();
        for(int i=0; i<children.size(); i++) {
            List<Boolean> childIndent = new ArrayList<>(indent);
            childIndent.add(i != children.size() - 1);
            children.get(i).printStack(withState, childIndent);
        }
    }

    /**
     * Notifies the given widget that its parent is now this widget.
     * 
     * @param child The new child of this widget
     */
    Widget addChild(Widget child) {
        if(child == null) return this;
        if(child.parent != null) child.parent.removeChild(child);
        child.parent = this;
        return this;
    }

    /**
     * Notifies the given widget that it is no longer a child of this
     * widget.
     * 
     * @param child The child widget to remove
     */
    Widget removeChild(Widget child) {
        if(child == null) return this;
        child.parent = null;
        return this;
    }



    /**
     * Adds the given method that will be executed whenever this widget
     * gets rerendered.
     * 
     * @param method The code to run after the widget was rerendered
     */
    public Widget addOnRender(Runnable method) {
        if(method == null) return this;
        onRender.add(method);
        return this;
    }

    /**
     * Removes the given method from those that are executed whenever
     * this widget gets rerendered.
     * 
     * @param method The method to remove
     * @return Weather the method to remove was active before
     */
    public boolean removeOnRender(Runnable method) {
        return onRender.remove(method);
    }

    /**
     * Runs the given code that modifys some for the rendering relevant
     * content and notifys the widget that it has to rerender.
     * 
     * @param method The code to run that modifys the widget's content
     */
    public void modify(Runnable method) {
        method.run();
        modify();
    }

    /**
     * Notifys this widget that some of its for the rendering relevant
     * content as modified and it has to rerender.
     */
    public void modify() {
        if(modifyLock) return;
        modified = childModified = true;
        Widget parent = getParent();
        if(parent != null) parent.modifyChild();
    }

    /**
     * Notifys the parent of this widget that this widget has changed,
     * causing it to rerender. However, this widget will not be marked
     * as modified and will therefore not rerender. This is useful if a
     * widget has a few different visual states that can be cached and
     * don't have to be recreated from scratch as long as the children
     * or the maxSize don't change, like for example a checkbox.
     * 
     * @param method The code to run that modifies the widget's state
     */
    protected void modifyState(Runnable method) {
        method.run();
        modifyState();
    }

    /**
     * Notifys the parent of this widget that this widget has changed,
     * causing it to rerender. However, this widget will not be marked
     * as modified and will therefore not rerender. This is useful if a
     * widget has a few different visual states that can be cached and
     * don't have to be recreated from scratch as long as the children
     * or the maxSize don't change, like for example a checkbox.
     */
    protected void modifyState() {
        if(modifyLock) return;
        Widget parent = getParent();
        if(parent != null) parent.modifyChild();
    }

    void modifyChild() {
        if(modifyLock) return;
        childModified = true;
        Widget parent = getParent();
        if(parent != null) parent.modifyChild();
    }



    @Override
    public String toString() {
        String id = getId();
        Class<?> cls = getClass();
        while(cls.getSimpleName().equals("")) cls = cls.getSuperclass();
        return cls.getSimpleName() + (id != null ? " id:\"" + id + '"' : "");
    }
}
