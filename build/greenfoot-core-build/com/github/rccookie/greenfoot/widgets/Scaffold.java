package com.github.rccookie.greenfoot.widgets;

import java.util.function.Supplier;

import com.github.rccookie.greenfoot.widgets.prefabs.FPS;
import com.github.rccookie.greenfoot.widgets.prefabs.SizedButton;

import greenfoot.Greenfoot;
import greenfoot.World;

public class Scaffold extends Structure {

    protected static final int FPS_BORDER = 3;

    private final Container fpsBase;
    private final Container backButtonBase, nextButtonBase;
    private Visual background;

    public Scaffold(Widget... children) {
        super();
        addChildren(children);
        addChildren(
            backButtonBase = new Offset(0.1, 0.9).setId("back_button_base").as(Container.class),
            nextButtonBase = new Offset(0.9, 0.9).setId("next_button_base").as(Container.class)
        );
        addChild(
            fpsBase = new Offset(
                0,
                0,
                FPS.WIDTH / 2 + FPS_BORDER,
                FPS.HEIGHT / 2 + FPS_BORDER - 2
            )
        );
    }

    public Scaffold addFPS() {
        if(fpsBase.find(FPS.class) == null) fpsBase.addChild(new FPS());
        return this;
    }

    public Scaffold setFPS(FPS fps) {
        if(fpsBase.find(FPS.class) != null) fpsBase.removeChild(FPS.class);
        fpsBase.addChild(fps);
        return this;
    }

    public Scaffold setBackground(Visual background) {
        if(this.background != null) removeChild(this.background);
        insertChild(0, this.background = background);
        return this;
    }

    public Scaffold addBack(World previousWorld) {
        return addBack(() -> Greenfoot.setWorld(previousWorld));
    }

    public Scaffold addBack(Supplier<World> previousWorld) {
        return addBack(() -> Greenfoot.setWorld(previousWorld.get()));
    }

    public Scaffold addBack(Runnable onClick) {
        if(backButtonBase.find(Button.class) != null) backButtonBase.find(Button.class).addOnClick(onClick);
        else {
            backButtonBase.addChild(
                new SizedButton("Back", Color.BLACK, new greenfoot.Color(204, 204, 204), onClick)
            );
        }
        return this;
    }

    public Scaffold addNext(World nextWorld) {
        return addNext(() -> Greenfoot.setWorld(nextWorld));
    }

    public Scaffold addNext(Supplier<World> nextWorld) {
        return addNext(() -> Greenfoot.setWorld(nextWorld.get()));
    }

    public Scaffold addNext(Runnable onClick) {
        if(nextButtonBase.find(Button.class) != null) nextButtonBase.find(Button.class).addOnClick(onClick);
        else {
            nextButtonBase.addChild(
                new SizedButton("Next", Color.BLACK, new greenfoot.Color(204, 204, 204), onClick)
            );
        }
        return this;
    }
}
