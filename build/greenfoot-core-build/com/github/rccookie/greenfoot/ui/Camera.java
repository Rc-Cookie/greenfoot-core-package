package com.github.rccookie.greenfoot.ui;

import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

/**
 * The camera can display a specific area of a world. This area's locaiton is fixed to an actor
 * in that world.
 * <p>The camera is an actor, not a world. It can be moved, you can have multiple cameras in one world
 * (=> splitscreen) and they don't have to match the current world's size.
 * 
 * @author RcCookie
 * @version 1.0
 */
public class Camera extends Container {

    /**
     * The default background color. This color is shown anywhere where displayed
     * world ends.
     */
    public static final Color BACKGROUND = Color.DARK_GRAY;

    /**
     * The default size of a camera.
     */
    public static final int DEFAULT_WIDTH = 300, DEFAULT_HEIGHT = 200;


    public static final double MIN_ZOOM = 0.1, MAX_ZOOM = 4;


    /**
     * The actor that the camera follows. You may create an actor that is stationary to have a camera fixed to a certain point.
     */
    final Actor follow;

    /**
     * The background color. This color is shown anywhere where displayed world ends.
     */
    Color backgroundColor = BACKGROUND;

    /**
     * The size of the camera.
     */
    final int camWidth, camHeight;


    double zoom = 1;




    /**
     * Creates a new Camera with the specified size following the given actor in its world.
     * 
     * @param camWidth The width of the cameras screen
     * @param camHeight The height of the cameras screen
     * @param follow The actor to follow
     */
    public Camera(int camWidth, int camHeight, Actor follow){
        super(follow.getWorld());
        this.follow = follow;
        this.camWidth = camWidth;
        this.camHeight = camHeight;
        repaint();
    }

    /**
     * Creates a new Camera with a default size following the given actor in its worls.
     * 
     * @param follow The actor to follow
     */
    public Camera(Actor follow){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, follow);
    }



    /**
     * Returns the width of the cameras screen.
     * 
     * @return The width of the cameras screen
     */
    public int getCamWidth() {
        return camWidth;
    }

    /**
     * Returns the height of the cameras screen.
     * 
     * @return The height of the cameras screen
     */
    public int getCamHeight() {
        return camHeight;
    }

    /**
     * Returns the current background color of the camera.
     * 
     * @return The current background color
     * @see #BACKGROUND
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the current zoom factor.
     * 
     * @return The current zoom
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Sets the background color of the camera to the given one.
     * 
     * @param backgroundColor The new background color
     * @see #BACKGROUND
     */
    @Deprecated
    public Camera setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Sets the zoom of the camera. Zoom will be corrected to be within 0.1x and 4x.
     * <p><b>Warning:</b> Zooming has a great performance impact. Any zoom other than
     * 1x will decrease performance, especially zooming in. Zooming in only 2x will
     * increase the calculation time by about 300%. The reason for that is that
     * image processing is really slow and if the scale isn't being changed, there has
     * not got to be made a copy of each image. Also when zooming out more objects may
     * have to be rendered.
     * 
     * @param zoom The new zoom
     */
    public void setZoom(double zoom) {
        if(zoom < MIN_ZOOM) zoom = MIN_ZOOM;
        else if(zoom > MAX_ZOOM) zoom = MAX_ZOOM;
        this.zoom = zoom;
        repaint();
    }





    /**
     * Renders the cameras screen.
     */
    @Override
    public void repaint(){

        //When super constructor is called, follow is not assigned yet
        if(follow == null) return;

        //The offset due to the follow actor moving or generally, not being at the center
        int offX = camWidth / 2 - (int)(follow.getX() * getCellSize() * zoom);
        int offY = camHeight / 2 - (int)(follow.getY() * getCellSize() * zoom);

        GreenfootImage displayed = new GreenfootImage(camWidth, camHeight);
        displayed.setColor(backgroundColor);
        displayed.fill();
        if(zoom != 1){
            GreenfootImage background = new GreenfootImage(getBackground());
            background.scale((int)(background.getWidth() * zoom), (int)(background.getHeight() * zoom));
            displayed.drawImage(background, offX, offY);
        }
        else displayed.drawImage(getBackground(), offX, offY);

        for(Actor object : objectsInPaintOrder()){

            

            //create rotated image of actors image
            GreenfootImage objectsImage = object.getImage();
            if(zoom != 1) {
                objectsImage = new GreenfootImage(objectsImage);
                objectsImage.scale((int)(objectsImage.getWidth() * zoom), (int)(objectsImage.getHeight() * zoom));
            }
            int diagonal = (int)Math.hypot(objectsImage.getWidth(), objectsImage.getHeight());

            int objX = object.getX(), objY = object.getY();

            
            if(bounded){
                if(objX < 0) objX = 0;
                else if(objX >= getWidth()) objX = getWidth() - 1;
                if(objY < 0) objY = 0;
                else if(objY >= getHeight()) objY = getHeight() - 1;
            }
            //no 'else' because even if the world is bounded the objects might still not be visible to the camera
            if(
                (int)(objX * getCellSize() * zoom) + diagonal / 2 < -offX ||
                (int)(objY * getCellSize() * zoom) + diagonal / 2 < -offY ||
                (int)(objX * getCellSize() * zoom) - diagonal / 2 >= -offX + getWidth() * getCellSize() ||
                (int)(objY * getCellSize() * zoom) - diagonal / 2 >= -offY + getHeight() * getCellSize()
            ) continue; //Don't render it - it's outside of vision

            GreenfootImage image = new GreenfootImage(diagonal, diagonal);
            image.drawImage(objectsImage, diagonal / 2 - objectsImage.getWidth() / 2, diagonal / 2 - objectsImage.getHeight() / 2);
            image.rotate((int)object.getRotation());

            //draw rotated image onto displayed image
            int objectX = (int)((int)objX * getCellSize() * zoom) + (int)(0.5 * getCellSize() * zoom);
            int objectY = (int)((int)objY * getCellSize() * zoom) + (int)(0.5 * getCellSize() * zoom);
            displayed.drawImage(image, offX + objectX - diagonal / 2, offY + objectY - diagonal / 2);

            setImage(image);
        }

        setImage(displayed);
    }
}
