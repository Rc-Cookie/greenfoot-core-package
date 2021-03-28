package com.github.rccookie.greenfoot.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;
import greenfoot.World;
import com.github.rccookie.common.util.ClassTag;


/**
 * The container is a type of actor that can display and run worlds. This allowes to
 * display multiple worlds at once, or having one small world (for example a game) inside
 * of a big world (for example the ui and menu).
 * <p>Containers can either show an existing world or act as their own. Therefore, they
 * offer all the methods of the world class (except {@code showText()}) and can be threaten
 * as one.
 * 
 * @author RcCookie
 * @version 1.0
 */
public class Container extends UIPanel {

    static {
        ClassTag.tag(Container.class, "ui");
    }



    /**
     * The order in which the actors will be painted. First will be painted last.
     */
    ArrayList<Class<?>> paintOrder = new ArrayList<>();

    /**
     * The order in which the actors will be acted.
     */
    ArrayList<Class<?>> actOrder = new ArrayList<>();


    /**
     * The underlying world, or the given world to be displayed.
     */
    protected final World manageWorld;

    /**
     * Weather the world should be bounded.
     */
    protected final boolean bounded;


    /**
     * Determines weather the rendering should run in performance mode.
     * <p><b>Effect:</b> If performance mode is enabled, the container will only render once per
     * frame. This works fine for programms that constantly run on a high speed. However,
     * moving, adding and removing objects will only be <i>visible</i> after the next frame.
     * <p>Therefore, on slow running or not running programms like scripts, performance mode
     * should be turned of, or the {@code repaint()} method has to me called manually.
     * <p>Performance mode is off by default. Simply override the value of this variable.
     */
    public boolean usePerformanceMode = false;


    /**
     * Saves the remaining time until each managed world should be acted.
     */
    static HashMap<World, Integer> timeUntilAct = new HashMap<>();




    /**
     * Creates a new container which has the given width and height and the given cell size.
     * The container will be bounded.
     * 
     * @param width The width of the container
     * @param height The height of the container
     * @param cellSize The cell size
     */
    public Container(int width, int height, int cellSize){
        this(width, height, cellSize, true);
    }

    /**
     * Creates a new container wiich has the given width and height and the given cell size.
     * Specify weather the world should be bounded.
     * 
     * @param width The width of the container
     * @param heightThe height of the container
     * @param cellSize The cell size of the container
     * @param bounded Weather the container should be bounded
     */
    public Container(int width, int height, int cellSize, boolean bounded){
        super(width, height, Color.WHITE);
        manageWorld = new World(width, height, cellSize, bounded) {
            @Override
            public void repaint() {
                //Do nothing: This world is never displayed, so we don't have to calculate how it would look like

                //This optimization can only be made if the container is its own world, rather than showing an existing one.
            }
        };
        this.bounded = bounded;
        setBackground((GreenfootImage)null);
        repaint();
    }

    /**
     * Creates a new container that shows the given world. You can also interact with that world through the methods of
     * this container.
     * 
     * @param shownWorld The world that should be shown
     */
    public Container(World shownWorld){
        super(shownWorld.getWidth(), shownWorld.getHeight(), Color.WHITE);
        manageWorld = shownWorld;
        this.bounded = false;
        repaint();
    }





    /**
     * If the underlying world wasn't acted this frame, all its actors and finally itself will be acted. Also, the
     * container will be repainted.
     */
    @Override
    public void physicsUpdate() {

        super.physicsUpdate();

        //Make sure that having multiple containers pointing at the same map won't act them multiple times
        if(getTimeUntilAct(manageWorld) <= 0){
            timeUntilAct.put(manageWorld, numberOfActsPerFrame());
            for(Actor a : objectsInActOrder()) a.act();
            manageWorld.act();
        }
        repaint();

        timeUntilAct.put(manageWorld, getTimeUntilAct(manageWorld) - 1);
    }


    /**
     * Returns the remaining time until the given world has to be acted again. Makes sure that the output is not {@code null}.
     * 
     * @param manageWorld The world to get the remaining time for
     * @return The remaining time
     */
    static int getTimeUntilAct(World manageWorld){
        Integer count = timeUntilAct.get(manageWorld);
        if(count == null) count = 0;
        return count;
    }

    /**
     * Returns the number of containers in this world that have the same underlying world, including itself.
     * 
     * @return The number of containers with this underlying world in this world
     */
    int numberOfActsPerFrame(){
        int count = 0;
        for(Container c : getWorld().getObjects(Container.class)){
            if(c.manageWorld == manageWorld) count++;
        }
        return count;
    }










    /**
     * Sets the order that objects will be painted. The first class will be painted at the top. Objects of other classes
     * will be painted in the order of adding them to the world: The one first added at the top.
     * <p><b>Important:</b> This will not be kept from a given world and has to be reassigned.
     * 
     * @param classes The classes that should be painted at the top
     */
    public void setPaintOrder(Class<?>... classes){
        paintOrder = new ArrayList<Class<?>>();
        for(Class<?> cls : classes) paintOrder.add(cls);
    }

    /**
     * Sets the order that objects will be acted in. The first class will be acted first. Objects of other classes
     * will be acted after that in the order of adding them to the world: The one first added at first.
     * <p><b>Important:</b> This will not be kept from a given world and has to be reassigned.
     * 
     * @param classes The classes that should be acted in
     */
    public void setActOrder(Class<?>... classes){
        actOrder = new ArrayList<Class<?>>();
        for(Class<?> cls : classes) actOrder.add(cls);
    }

    



    /**
     * Sets the background to the given image. If it is smaller than the world, it will be repeated.
     * 
     * @param background The new background image
     */
    public void setBackground(GreenfootImage background){
        manageWorld.setBackground(background);
    }

    /**
     * Sets the background to the given image. If it is smaller than the world, it will be repeated.
     * 
     * @param filename The new background image's filename
     */
    public void setBackground(String filename){
        manageWorld.setBackground(filename);
    }

    /**
     * Adds an object at the specified locaiton into the container.
     * 
     * @param object The actor to add
     * @param x The actors x coordinate
     * @param y The actors y coordinate
     */
    public void addObject(Actor object, int x, int y){
        manageWorld.addObject(object, x, y);
        if(!usePerformanceMode) repaint();
    }

    /**
     * Removes the given object from the container.
     * 
     * @param object The actor to remove
     */
    public void removeObject(Actor object){
        manageWorld.removeObject(object);
        if(!usePerformanceMode) repaint();
    }

    /**
     * Removes the given collection of actors from the container.
     * 
     * @param objects The actors to remove
     */
    public void removeObjects(Collection<? extends Actor> objects){
        manageWorld.removeObjects(objects);
        if(!usePerformanceMode) repaint();
    }



    //World getters

    /**
     * Returns the background image of the container.
     * 
     * @return The background image
     */
    public GreenfootImage getBackground() {
        return manageWorld.getBackground();
    }

    /**
     * Returns the width of the container.
     * 
     * @return The width of the container
     */
    public int getWidth() {
        return manageWorld.getWidth();
    }

    /**
     * Returns the height of the container.
     * 
     * @return The height of the container
     */
    public int getHeight() {
        return manageWorld.getHeight();
    }

    /**
     * Returns the cell size of the container.
     * 
     * @return The cell size of the container
     */
    public int getCellSize() {
        return manageWorld.getCellSize();
    }

    /**
     * Returns weather the container is bounded.
     * 
     * @return Weather the container is bounded
     */
    public boolean bounded(){
        return bounded;
    }

    /**
     * Returns the color of the background at the given pixel location.
     * 
     * @param x The x coordinate of the pixel
     * @param y The y coordinate of the pixel
     * @return The color at the location
     */
    public Color getColorAt(int x, int y){
        return manageWorld.getColorAt(x, y);
    }

    /**
     * Returns all objects of the given class, or all, if cls is {@code null}.
     * 
     * @param cls The class of the objects, or null
     * @return The objects of the given class
     */
    public <A> List<A> getObjects(Class<A> cls){
        return manageWorld.getObjects(cls);
    }

    /**
     * Returns all objects of the given class, or all, if cls is {@code null}.
     * 
     * @param cls The class of the objects, or null
     * @return The objects of the given class
     */
    public <A> List<A> getObjectsAt(int x, int y, Class<A> cls){
        return manageWorld.getObjectsAt(x, y, cls);
    }

    /**
     * Get the number of actors currently in the container.
     * 
     * @return The number of actors
     */
    public int numberOfObjects(){
        return manageWorld.numberOfObjects();
    }



    












    /**
     * Renders the look of the container.
     */
    public void repaint(){

        GreenfootImage displayed = new GreenfootImage(getWidth(), getHeight());
        displayed.drawImage(getBackground(), 0, 0);

        for(Actor object : objectsInPaintOrder()){

            int objX = object.getX() * (getImage().getWidth() / getWidth());
            int objY = object.getY() * (getImage().getHeight() / getHeight());

            //calculate the maximum size of the rotated image
            GreenfootImage objectsImage = object.getImage();
            int diagonal = (int)Math.hypot(objectsImage.getWidth(), objectsImage.getHeight());

            if(bounded){
                if(objX < 0) objX = 0;
                else if(objX >= getWidth()) objX = getWidth() - 1;
                if(objY < 0) objY = 0;
                else if(objY >= getHeight()) objY = getHeight() - 1;
            }
            //'else' because in a bounded world every object is constantly on the screem
            else if(
                objX * getCellSize() + diagonal / 2 < 0 ||
                objY * getCellSize() + diagonal / 2 < 0 ||
                objX * getCellSize() - diagonal / 2 >= getWidth() * getCellSize() ||
                objY * getCellSize() - diagonal / 2 >= getHeight() * getCellSize()
            ) continue; //Don't render it - it's outside of vision

            //gererate the rotated image
            GreenfootImage image = new GreenfootImage(diagonal, diagonal);
            image.drawImage(objectsImage, diagonal / 2 - objectsImage.getWidth() / 2, diagonal / 2 - objectsImage.getHeight() / 2);
            image.rotate((int)object.getRotation());

            //draw rotated image onto displayed image
            int objectX = (int)objX * getCellSize() + (int)(0.5 * getCellSize());
            int objectY = (int)objY * getCellSize() + (int)(0.5 * getCellSize());
            displayed.drawImage(image, objectX - diagonal / 2, objectY - diagonal / 2);

            setImage(image);
        }

        setImage(displayed);
    }
    


    







    /**
     * Returns the objects in the world in an order so that if you paint them on top of
     * each other they will match the paint order.
     * 
     * @return The objects in working paint order
     */
    protected List<Actor> objectsInPaintOrder(){
        ArrayList<Actor> ordered = new ArrayList<>();

        //Add non-listed class objects
        for(Actor current : getObjects(Actor.class)){
            if(paintOrder.contains(current.getClass())) continue;
            ordered.add(current);
        }

        //Add listed class objects in reverse to paint the highest last
        for(int i=paintOrder.size()-1; i>=0; i--){
            objectLoop:
            for(Actor current : getObjects(Actor.class)) {
                if(current.getClass() != paintOrder.get(i)) continue objectLoop;
                ordered.add(current);
            }
        }
        
        return ordered;
    }




    /**
     * Returns all objects in the world in the act order.
     * 
     * @return All objects in act order
     */
    protected List<Actor> objectsInActOrder(){
        ArrayList<Actor> ordered = new ArrayList<>();

        //Add listed class objects
        for(Class<?> cls : actOrder){
            objectLoop:
            for(Actor current : getObjects(Actor.class)) {
                if(current.getClass() != cls) continue objectLoop;
                ordered.add(current);
            }
        }

        //Add non-listed class objects
        for(Actor current : getObjects(Actor.class)){
            if(actOrder.contains(current.getClass())) continue;
            ordered.add(current);
        }
        
        return ordered;
    }
}