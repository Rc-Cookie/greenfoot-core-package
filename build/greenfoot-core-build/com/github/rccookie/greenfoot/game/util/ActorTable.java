package com.github.rccookie.greenfoot.game.util;

import java.util.function.Function;

import greenfoot.Actor;
import greenfoot.World;
import com.github.rccookie.common.util.Table;


/**
 * An actor table is a table which holds only actors. It contains additional methods to interact with
 * its elements to for example add them to a world.
 * @author RcCookie
 * @version 1.0
 */
public class ActorTable<T extends Actor> extends Table<T> {

    /**
     * Creates a new empty actor table with the specified dimensions.
     * 
     * @param rows The number of rows
     * @param columns the number of columns
     */
    public ActorTable(int rows, int columns) {
        super(rows, columns);
    }

    /**
     * Creates a new actor table and fills it using the given initializer.
     * 
     * @param rows The number of rows
     * @param columns The number of columns
     * @param initializer A function that returns a object for each location
     */
    public ActorTable(int rows, int columns, Function<GridElement<T>, T> initializer) {
        super(rows, columns, initializer);
    }

    /**
     * Adds all actors to the given world in a table shape with the given start coordinates and the specified spacing.
     * 
     * @param world The world to add the objects to
     * @param xStart The x coordinate of the top left object in the table
     * @param yStart The y coordinate of the top left object in the table
     * @param spacing The distance between two objects
     */
    public void addTo(World world, int xStart, int yStart, int spacing) {
        if(world == null) return;
        forEachLoc(current -> {
            world.addObject(current.value, xStart + current.row() * spacing, yStart + current.column() * spacing);
        });
    }

    /**
     * Adds all actors to the given world in a table shape with the given start coordinates.
     * 
     * @param world The world to add the objects to
     * @param xStart The x coordinate of the top left object in the table
     * @param yStart The y coordinate of the top left object in the table
     */
    public void addTo(World world, int xStart, int yStart) {
        addTo(world, xStart, yStart, 1);
    }

    /**
     * Adds all actors to the given world in a table shape with the given center coordinates and the specified spacing.
     * 
     * @param world The world to add the objects to
     * @param xStart The x coordinate of the center of the table
     * @param yStart The y coordinate of the center of the table
     * @param spacing The distance between two objects
     */
    public void addCenteredTo(World world, int centerX, int centerY, int spacing) {
        addTo(
            world,
            centerX - columnCount() * spacing / 2 + spacing / 2,
            centerY - rowCount() * spacing / 2 + spacing / 2,
            spacing
        );
    }

    /**
     * Adds all actors to the center of given world in a table shape with the specified spacing.
     * 
     * @param world The world to add the objects to
     * @param spacing The distance between two objects
     */
    public void addTo(World world, int spacing) {
        addCenteredTo(
            world,
            world.getWidth() / 2,
            world.getHeight() / 2,
            spacing
        );
    }

    /**
     * Adds all actors to the center of given world in a table shape.
     * 
     * @param world The world to add the objects to
     */
    public void addTo(World world) {
        addTo(world, 1);
    }


    /**
     * Removes any actors from the world that are contained in the table.
     * 
     * @param world The world to remove any contained actors from
     */
    public void removeFrom(World world) {
        forEach(current -> world.removeObject(current));
    }
}
