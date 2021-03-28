package com.github.rccookie.greenfoot.core;

import greenfoot.GreenfootSound;

/**
 * An implementation of {@link GreenfootSound}.
 * 
 * @author RcCookie
 * @version 0.1
 */
public class Sound extends GreenfootSound {

    static {
        Core.initialize();
    }



    /**
     * Creates a new sound by loading the sound file from the given file.
     * 
     * @param filename The path and name of the file
     */
    public Sound(String filename) {
        super(filename);
    }



    /**
     * Returns the filename of this sound.
     * 
     * @return The sound's filename
     */
    public String getFilename() {
        return getFilenameOf(this);
    }



    /**
     * Creates a new Sound from the given {@link GreenfootSound}.
     * 
     * @param gSound The greenfoot sound to copy
     * @return The new sound object
     */
    public static final Sound of(GreenfootSound gSound) {
        return new Sound(getFilenameOf(gSound));
    }

    private static final String getFilenameOf(GreenfootSound gSound) {
        String s = gSound.toString();
        s = s.substring(s.indexOf("file: ") + 6);
        return s.substring(0, s.indexOf(" . "));
    }

    /**
     * Plays the sound loaded from the specified file.
     * 
     * @param filename The path and name of the file to play the sound from
     * @return The sound that is now being played
     */
    public static final Sound play(String filename) {
        Sound sound = new Sound(filename);
        sound.play();
        return sound;
    }
}
