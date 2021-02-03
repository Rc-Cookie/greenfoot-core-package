package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.greenfoot.event.Input;
import com.github.rccookie.greenfoot.widgets.prefabs.CloseButton;

import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;
import greenfoot.World;

public class Window extends Offset {

    static final int TOP_BAR_HEIGHT = 30;
    static final int BORDER_SIZE = 1;
    static final int CORNER_RESIZE_SIZE = 4;
    static final int MIN_WIDTH = 250, MIN_HEIGHT = 150;

    static final int MAX_DOUBLE_CLICK_DELAY = 350;

    static final String OVER_RESIZE_DIMENSION = "over_resize_dimension";
    static final String WINDOW_DIMENSION = "window_dimension";
    static final String MAXIMIZE_BUTTON = "maximize_button";
    static final String BODY = "window_body";
    static final String ICON = "window_icon";

    private boolean runIndependent = false;
    private boolean updateRunning = false;

    private Vector2D dragOffset = null;
    private Vector2D dragStartOffset = null;
    private boolean dragged = false;
    private long lastClickTime = 0l;

    private int resizeDir = -1;
    private Vector2D resizeStart;
    private Vector2D difBeforeResize;

    private boolean fullscreen = false;
    private int lastWidth, lastHeight;
    private Vector2D lastDif;

    private final List<Consumer<Window>> onClose = new ArrayList<>();



    public Window(String title, Widget... children) {
        this(title, 450, 300, children);
    }

    public Window(String title, int width, int height, Widget... children) {
        super(0.5, 0.5, 0, 0);
        addOwnChildren(
            new Dimension(
                width,
                height,
                new Color(Color.DARK_GRAY),
                new Structure(
                    Align.top(
                        new Dimension(
                            -2 * CORNER_RESIZE_SIZE,
                            1,
                            new ResizeButton(0)
                        )
                    ),
                    Align.bottom(
                        new Dimension(
                            -2 * CORNER_RESIZE_SIZE,
                            1,
                            new ResizeButton(1)
                        )
                    ),
                    Align.left(
                        new Dimension(
                            1,
                            -2 * CORNER_RESIZE_SIZE,
                            new ResizeButton(2)
                        )
                    ),
                    Align.right(
                        new Dimension(
                            1,
                            -2 * CORNER_RESIZE_SIZE,
                            new ResizeButton(3)
                        )
                    ),
                    Align.topleft(
                        new Dimension(
                            CORNER_RESIZE_SIZE,
                            CORNER_RESIZE_SIZE,
                            new ResizeButton(4)
                        )
                    ),
                    Align.topright(
                        new Dimension(
                            CORNER_RESIZE_SIZE,
                            CORNER_RESIZE_SIZE,
                            new ResizeButton(5)
                        )
                    ),
                    Align.bottomleft(
                        new Dimension(
                            CORNER_RESIZE_SIZE,
                            CORNER_RESIZE_SIZE,
                            new ResizeButton(6)
                        )
                    ),
                    Align.bottomright(
                        new Dimension(
                            CORNER_RESIZE_SIZE,
                            CORNER_RESIZE_SIZE,
                            new ResizeButton(7)
                        )
                    )
                ).setId("resize_buttons"),
                new Dimension(
                    -2,
                    -2,
                    Align.bottom(
                        new Dimension(
                            0,
                            -TOP_BAR_HEIGHT,
                            new Color(Color.LIGHT_GRAY),
                            Align.top(
                                new Scaffold(children).addFPS().setId(BODY)
                            )
                        )
                    ),
                    Align.top(
                        new Dimension(
                            0,
                            TOP_BAR_HEIGHT,
                            new Color(Color.DARK_GRAY),
                            new Button(
                                () -> {
                                    if(!dragged) lastClickTime = System.currentTimeMillis();
                                    else lastClickTime = 0l;
                                },
                                Align.left(
                                    new Dimension(
                                        TOP_BAR_HEIGHT,
                                        TOP_BAR_HEIGHT,
                                        Icon.load("default_icon").setId(ICON)
                                    )
                                ),
                                new Text(
                                    title,
                                    Color.LIGHT_GRAY
                                ).setId("title")
                            ).setAnimated(false)
                                .addOnPress(() -> {
                                    if(!dragged && System.currentTimeMillis() - lastClickTime <= MAX_DOUBLE_CLICK_DELAY) {
                                        toggleFullscreen();
                                        lastClickTime = 0l;
                                        return;
                                    }
                                    dragStartOffset = getDif();
                                    dragOffset = Vector2D.between(getMouseLoc(), dragStartOffset);
                                    dragged = false;
                                })
                                .addOnRelease(() -> dragOffset = null)
                                .setId("top_bar_button"),
                            Align.right(
                                new Line(
                                    new Dimension(
                                        46,
                                        30,
                                        new ImageButton(
                                            Icon.loadIconImage("minimize"),
                                            () -> { }
                                        ) {
                                            protected GreenfootImage renderPressed(GreenfootImage base) {
                                                return super.renderDisabled(base);
                                            };
                                        }.useHoverOutline(false).setId("minimize_button")
                                    ),
                                    new Dimension(
                                        46,
                                        30,
                                        new ImageButton(
                                            Icon.loadIconImage("maximize_1"),
                                            () -> {
                                                modify(() -> {
                                                    fullscreen = !fullscreen;
                                                    Dimension dimension = find(WINDOW_DIMENSION).as(Dimension.class);
                                                    Dimension overScaleDimension = find(OVER_RESIZE_DIMENSION).as(Dimension.class);
                                                    if(fullscreen) {
                                                        lastWidth = dimension.getMaxWidth();
                                                        lastHeight = dimension.getMaxHeight();
                                                        lastDif = getDif();
                                                        dimension.setMaxWidth(0);
                                                        dimension.setMaxHeight(0);
                                                        setDif(new Vector2D());
                                                        overScaleDimension.setMaxWidth(0).setMaxHeight(0);
                                                    }
                                                    else {
                                                        dimension.setMaxWidth(lastWidth);
                                                        dimension.setMaxHeight(lastHeight);
                                                        setDif(lastDif);
                                                        overScaleDimension.setMaxWidth(-2).setMaxHeight(-2);
                                                    }
                                                });
                                            }
                                        ) {
                                            boolean fullscreen = false;
                                            {
                                                addOnClick(() -> {
                                                    modify(() -> {
                                                        fullscreen = !fullscreen;
                                                        setImage(Icon.loadIconImage("maximize_" + (fullscreen ? "2" : "1")));
                                                    });
                                                });
                                            }
                                            protected GreenfootImage renderPressed(GreenfootImage base) {
                                                return super.renderDisabled(base);
                                            };
                                        }.useHoverOutline(false).setId(MAXIMIZE_BUTTON)
                                    ),
                                    new CloseButton(() -> close()).setId("close_button")
                                )
                            )
                        ).setId("top_bar")
                    )
                ).setId(OVER_RESIZE_DIMENSION)
            ).setId(WINDOW_DIMENSION)
        );
        checkMinDimensions();
    }

    @Override
    protected void update(Size maxSize) {
        haldleMoving();
        haldleResizing();
    }

    private void haldleResizing() {
        if(resizeDir != -1) {
            if(fullscreen) {
                toggleFullscreen();
                stopResizing();
                return;
            }
            Vector2D mouse = getMouseLoc();
            Vector2D dif = Vector2D.between(resizeStart, mouse);
            Dimension dimension = find(WINDOW_DIMENSION).as(Dimension.class);
            switch(resizeDir) {
                case 0: {
                    setDif(difBeforeResize.added(new Vector2D(0, dif.y() / 2)));
                    dimension.setMaxHeight((int)-(mouse.y() - dimension.getLocation().y()) * 2);
                    break;
                }
                case 1: {
                    setDif(difBeforeResize.added(new Vector2D(0, dif.y() / 2)));
                    dimension.setMaxHeight((int)(mouse.y() - dimension.getLocation().y()) * 2);
                    break;
                }
                case 2: {
                    setDif(difBeforeResize.added(new Vector2D(dif.x() / 2)));
                    dimension.setMaxWidth((int)-(mouse.x() - dimension.getLocation().x()) * 2);
                    break;
                }
                case 3: {
                    setDif(difBeforeResize.added(new Vector2D(dif.x() / 2)));
                    dimension.setMaxWidth((int)(mouse.x() - dimension.getLocation().x()) * 2);
                    break;
                }
                case 4: {
                    setDif(difBeforeResize.added(dif.scaled(0.5)));
                    dimension.setMaxWidth((int)-(mouse.x() - dimension.getLocation().x()) * 2);
                    dimension.setMaxHeight((int)-(mouse.y() - dimension.getLocation().y()) * 2);
                    break;
                }
                case 5: {
                    setDif(difBeforeResize.added(dif.scaled(0.5)));
                    dimension.setMaxWidth((int)(mouse.x() - dimension.getLocation().x()) * 2);
                    dimension.setMaxHeight((int)-(mouse.y() - dimension.getLocation().y()) * 2);
                    break;
                }
                case 6: {
                    setDif(difBeforeResize.added(dif.scaled(0.5)));
                    dimension.setMaxWidth((int)-(mouse.x() - dimension.getLocation().x()) * 2);
                    dimension.setMaxHeight((int)(mouse.y() - dimension.getLocation().y()) * 2);
                    break;
                }
                case 7: {
                    setDif(difBeforeResize.added(dif.scaled(0.5)));
                    dimension.setMaxWidth((int)(mouse.x() - dimension.getLocation().x()) * 2);
                    dimension.setMaxHeight((int)(mouse.y() - dimension.getLocation().y()) * 2);
                    break;
                }
            }
        }
    }

    private void startResizing(int dir) {
        resizeDir = dir;
        resizeStart = getMouseLoc();
        difBeforeResize = getDif();
    }

    private void stopResizing() {
        resizeDir = -1;
        lastWidth = lastSize.width;
        lastHeight = lastSize.height;
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

    private void haldleMoving() {
        if(dragOffset != null) {
            Vector2D newLoc = getMouseLoc().add(dragOffset);
            if(!newLoc.equals(getDif())) {
                dragged = true;
                if(fullscreen) {
                    toggleFullscreen();
                    setDif(new Vector2D(getMouseLoc().x(), lastHeight / 2).subtracted(new Vector2D(lastSize.width, lastSize.height).scale(0.5)));
                    dragOffset = getDif().subtracted(getMouseLoc());
                }
                else setDif(newLoc);
            }
        }
    }

    public Window toggleFullscreen() {
        find(MAXIMIZE_BUTTON).as(Button.class).click();
        return this;
    }

    public Window setFullscreen(boolean flag) {
        if(fullscreen == flag) return this;
        return toggleFullscreen();
    }

    private void checkMinDimensions() {
        Dimension dimension = find(WINDOW_DIMENSION).as(Dimension.class);
        if(dimension.getMaxWidth() < MIN_WIDTH) dimension.setMaxWidth(MIN_WIDTH);
        if(dimension.getMaxHeight() < MIN_HEIGHT) dimension.setMaxHeight(MIN_HEIGHT);
    }

    @Override
    void runUpdate(Size maxSize) {
        if(isRunningIndependent()) {
            if(updateRunning) return;
            Thread updateThread = new Thread(() -> {
                updateRunning = true;
                super.runUpdate(maxSize);
                updateRunning = false;
            });
            updateThread.setDaemon(true);
            updateThread.setPriority(Thread.MIN_PRIORITY);
            updateThread.start();
        }
        else super.runUpdate(maxSize);
    }

    @Override
    public Container addChild(Widget child) {
        find(BODY).as(Scaffold.class).addChild(child);
        return this;
    }

    @Override
    public Container removeChild(Widget child) {
        find(BODY).as(Scaffold.class).removeChild(child);
        return this;
    }

    private void addOwnChildren(Widget... children) {
        if(children != null)
            for(Widget child : children) super.addChild(child);
    }

    public boolean isRunningIndependent() {
        return runIndependent;
    }

    public Window setRunIndependent(boolean flag) {
        runIndependent = flag;
        return this;
    }

    public GreenfootImage getIcon() {
        return find(ICON).as(Icon.class).getIconImage();
    }

    public Window setIcon(GreenfootImage icon) {
        modify(() -> find(ICON).as(Icon.class).setIconImage(icon != null ? icon : Icon.loadIconImage("default_icon")));
        return this;
    }

    public Window addOnClose(Runnable method) {
        return addOnClose(method != null ? w -> method.run() : null);
    }

    public Window addOnClose(Consumer<Window> method) {
        if(method == null) return this;
        onClose.add(method);
        return this;
    }

    public boolean removeOnClose(Consumer<Window> method) {
        return onClose.remove(method);
    }

    public void close() {
        for(Consumer<Window> method : onClose) method.accept(this);
        if(holder != null) holder.setWidget(null);
        Widget parent = getParent();
        if(parent != null) parent.removeChild(this);
    }



    private class ResizeButton extends Button {
        public ResizeButton(int dir) {
            super(() -> { });
            addOnPress(() -> startResizing(dir));
            addOnRelease(() -> stopResizing());
            setAnimated(false);
            setDynamic(false);
        }
    }
}
