package com.github.rccookie.greenfoot.core.physics;

import com.github.rccookie.common.geometry.*;

import java.util.List;

import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;
import greenfoot.World;

import java.util.ArrayList;

/**
 * The collider is a type of actor that is used to have a custom collision box on other actors.
 * It is abstract and contains a handful of methods that have to be implemented by extending classes.
 * 
 * @author RcCookie
 * @version 1.0
 */
public abstract class Collider extends Actor{

    /**
     * The actor this collider belongs to.
     */
    public Actor host;
    
    /**
     * The transform that describes the offset of the host's location. The collider can be offset by a
     * x and y distance and a rotation.
     */
    public final Transform2D offset;

    /**
     * A list of colliders to not collide with.
     */
    public ArrayList<Collider> noCollision = new ArrayList<Collider>();
    
    /**
     * Weather the collision area of the collider should be drawn or not.
     */
    protected boolean debug = false;
    

    /**
     * Creates a new collider with the get given actor as host, the specified offset and the given size
     * for an image. The size does not mean that that is the actual collision box.
     * 
     * @param host The actor this collider belongs to
     * @param offset The offset of this collider to the host
     * @param size The size of the image of the collider in pixels
     */
    protected Collider(Actor host, Transform2D offset, Vector2D size){
        this.host = host;
        this.offset = offset;
        setImage(new GreenfootImage((int)size.x(), (int)size.y()));
    }

    /**
     * Updates the location and rotation of the collider to match the ones of the host
     */
    protected void addedToWorld(World w){
        if(host != null && host.getWorld() == w)
            setRotation(host.getRotation());
        else setRotation(0);
    }
    
    

    
    /**
     * The main difference between different colliders. Has to be overridden to make the collider behave
     * like the form that it is meant to represent. To access the initial method, use
     * {@code imageIntersects(Actor a)}.
     * <p>The method has to return {@code false} if {@code isIgnored} returns {@code true}. Therefore, the
     * first line of code in every implementation should be:
     * <p>{@code if(isIgnored(a)) return false;}
     * 
     * @param a The actor to check collision with
     * @return Weather the collider intersects the given actor
     */
    public abstract boolean intersects(Actor a);
    
    /**
     * Returns the 'normal' result of {@code intersects(Actor a)} to use in implementation. Only exception: does
     * return {@code false} for {@code host} as argument.
     * 
     * @param a The actor to check collision with
     * @return Weather the collider intersects the given actor with exception of the host
     */
    protected boolean imageIntersects(Actor a){
        if(isIgnored(a)) return false;
        return super.intersects(a);
    }

    protected boolean isIgnored(Actor a){
        return a == host || noCollision.contains(a);
    }
    
    /**
     * Retruns a vector that is parallel to the world edge that the collider is hitting currently, or null if it is
     * within the world bounds. The vector has the length 1.
     * 
     * @return A vector that represents the world edge the collider is currently hitting with a length of 1
     */
    public abstract Vector2D getWorldEdge();
    
    /**
     * Returns the area of the collider in pixels.
     * 
     * @return The area of the collider in pixels
     */
    public abstract int getArea();
    
    /**
     * Returns a vector that is parallel to the edge of the collider that is closest to the actor. The vector has
     * the length 1.
     * 
     * @param a The actor to check for
     * @return A vector that represents the closest edge of the collider to the actor, with a length of 1
     */
    public abstract Vector2D getEdgeTowards(Actor a);
    
    /**
     * Weather the collider touches the world bounds.
     * 
     * @return Weather the collider touches the world bounds
     */
    public boolean isAtEdge(){
        return getWorldEdge() != null;
    }
    
    
    
    /*
     * The following methods are overridden to be public, so that you can access the collision state of the collider
     */
    @Override
    public <A> List<A> getIntersectingObjects(Class<A> cls){
        return super.getIntersectingObjects(cls);
    }
    @Override
    public Actor getOneIntersectingObject(Class<?> cls){
        return super.getOneIntersectingObject(cls);
    }
    @Override
    public boolean isTouching(Class<?> cls){
        return super.isTouching(cls);
    }
    @Override
    public void removeTouching(Class<?> cls){
        super.removeTouching(cls);
    }
    
    
    
    
    
    
    /**
     * Returns the collider itself.
     * <p>Use this to access the collider as it updates its location and rotation.
     * 
     * @return The collider itself
     */
    public Collider get(){
        if(host == null) return this;
        setRotation(host.getRotation());
        if(getWorld() != host.getWorld()){
            if(getWorld() != null)
                getWorld().removeObject(this);
            if(host.getWorld() != null)
                host.getWorld().addObject(this, host.getX(), host.getY());
        }
        else if(host.getWorld() != null) setLocation(host.getX(), host.getY());
        return this;
    }
    
    
    
    public void setImage(GreenfootImage image){
        super.setImage(image);
        
        //debug
        if(debug){
            image.setColor(new Color(255, 0, 0, 100));
            image.fill();
        }
    }

    

    


    @Override
    public void setLocation(int x, int y){
        Vector2D angularOffset = Vector2D.angledVector(
            getRotation() - offset.rotation + offset.location.angle(),
            offset.location.abs()
        );
        super.setLocation(x + (int)angularOffset.x(), y + (int)angularOffset.y());
    }
    
    @Override
    public int getX(){
        Vector2D angularOffset = Vector2D.angledVector(
            getRotation() - offset.rotation + offset.location.angle(),
            offset.location.abs()
        );
        return super.getX() - (int)angularOffset.x();
    }
    @Override
    public int getY(){
        Vector2D angularOffset = Vector2D.angledVector(
            getRotation() - offset.rotation + offset.location.angle(),
            offset.location.abs()
        );
        return super.getY() - (int)angularOffset.y();
    }
    
    @Override
    public void setRotation(int angle){
        int x, y;
        try{
            x = getX();
            y = getY();
            super.setRotation(angle + (int)offset.rotation);
            setLocation(x, y);
        }catch(IllegalStateException e){
            super.setRotation(angle + (int)offset.rotation);
        }
    }
    @Override
    public int getRotation(){
        return super.getRotation() - (int)offset.rotation;
    }
    @Override
    public void turn(int angle){
        setRotation(getRotation() + angle);
    }
    
    
    /**
     * Sets the debug state to the given one.
     * @param state The new debug state
     */
    public void debug(boolean state){
        debug = state;
        setImage(new GreenfootImage(getImage().getWidth(), getImage().getHeight()));
    }
}