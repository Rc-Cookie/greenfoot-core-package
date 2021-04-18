/**
 * This package introduces a complete rework of the user-visible
 * greenfoot-framework. It features wrapper classes for all common
 * greenfoot classes like {@link greenfoot.Actor Actor} with
 * {@link com.github.rccookie.greenfoot.core.GameObject GameObject}
 * or {@link greenfoot.World World} with
 * {@link com.github.rccookie.greenfoot.core.Map Map}. It is intended
 * for use as an enclosed system with other packages from this package.
 * Most classes therefore support conversion of a greenfoot package
 * class to the representing class from the core package because normal
 * methods of the classes don't support greenfoot package classes.
 * 
 * <p>Overview of how greenfoot classes are represented in the core
 * package:
 * 
 * <table>
 * <tr><th>Greenfoot package<th>Core package
 * <tr><td>Actor<td>GameObject
 * <tr><td>World<td>Map (subclass)
 * <tr><td>GreenfootImage<td>Image
 * <tr><td>greenfoot.Color<td>Color
 * <tr><td>Font<td>FontStyle (subclass)
 * <tr><td>UserInfo<td>User
 * <tr><td>MouseInfo<td>MouseState
 * <tr><td>Greenfoot<td>Core + KeyState
 * <tr><td>GreenfootSound<td>Sound (subclass)
 * </table>
 * 
 * <p>The core package can be seen as the base package for the other
 * greenfoot packages. All other greenfoot packages are using these
 * classes as base.
 */
package com.github.rccookie.greenfoot.core;
