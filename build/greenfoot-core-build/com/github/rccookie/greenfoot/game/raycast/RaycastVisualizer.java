package com.github.rccookie.greenfoot.game.raycast;

import greenfoot.Color;
import greenfoot.GreenfootImage;
import greenfoot.World;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.greenfoot.core.CoreActor;
import com.github.rccookie.greenfoot.game.raycast.Raycast.*;
import com.github.rccookie.greenfoot.ui.util.Theme;

public class RaycastVisualizer {

    private final Marker[] components;
    private final World world;

    private boolean active = false;

    public RaycastVisualizer(Theme theme, World world) {
        components = new Marker[] {
            new RayMarker(theme.second(), world),
            new HitMarker(theme.main())
        };
        this.world = world;
    }


    public void update(Raycast raycast) {
        setActive(raycast != null);
        if(!active) return;
        for(Marker component : components) component.update(raycast);
    }


    public void setActive(boolean flag) {
        if(active == flag) return;
        active = flag;
        if(active) for(Marker component : components) world.addObject(component, -1, -1);
        else for(Marker component : components) world.removeObject(component);
    }



    @IgnoreOnRaycasts
    private abstract class Marker extends CoreActor {

        final Color color;

        Marker(int width, int height, Color color) {
            this.color = color;
            setImage(new GreenfootImage(width, height));
            getImage().setColor(color);
            getImage().fill();
        }

        abstract void update(Raycast raycast);
    }


    @IgnoreOnRaycasts
    private class HitMarker extends Marker {

        private boolean visible = false;
        
        HitMarker(Color color) {
            super(6, 6, color);
            getImage().setTransparency(0);
        }

        private void setVisibility(boolean flag) {
            if(visible == flag) return;
            visible = flag;
            getImage().setTransparency(flag ? 255 : 0);
        }

        @Override
        void update(Raycast raycast) {
            setVisibility(raycast.hitLoc != null);
            if(!visible) return;
            setLocation(raycast.hitLoc);
        }
    }


    @IgnoreOnRaycasts
    private class RayMarker extends Marker {
        
        private final int maxLength;

        private int currentLength;

        RayMarker(Color color, World world) {
            super(1, 1, color);
            currentLength = 1;
            maxLength = (int)Math.hypot(world.getWidth(), world.getHeight());
        }

        @Override
        void update(Raycast raycast) {
            int newLength = raycast.ray != null && raycast.length < maxLength ? (int)raycast.length : maxLength;

            if(newLength != currentLength) {
                currentLength = newLength;
                setImage(new GreenfootImage(newLength > 0 ? newLength : 1, 1));
                getImage().setColor(color);
                getImage().fill();
            }

            double angle = raycast.ray.direction().angle();
            setLocation(raycast.root.added(Vector2D.angledVector(angle, newLength).scale(0.5)));
            setRotation(angle);
        }
    }
}
