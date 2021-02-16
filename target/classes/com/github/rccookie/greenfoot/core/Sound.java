package com.github.rccookie.greenfoot.core;

import greenfoot.GreenfootSound;

public class Sound extends GreenfootSound {

    public Sound(GreenfootSound copy) {
        this(getFilenameOf(copy));
    }

    public Sound(String filename) {
        super(filename);
    }



    public String getFilename() {
        return getFilenameOf(this);
    }



    public static final String getFilenameOf(GreenfootSound sound) {
        String s = sound.toString();
        s = s.substring(s.indexOf("file: ") + 6);
        return s.substring(0, s.indexOf(" . "));
    }

    public static final Sound play(String filename) {
        Sound sound = new Sound(filename);
        sound.play();
        return sound;
    }
}
