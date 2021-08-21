package com.github.rccookie.greenfoot.core;

/**
 * Represents the type of the current session.
 */
public enum Session {

    /**
     * The scenario runs offline on the Greenfoot application on native Java.
     */
    OFFLINE,

    /**
     * The scenario runs online translated to JavaScript and has no access to things
     * like reflection or java.awt.
     */
    ONLINE,

    /**
     * The scenario runs offline in standalone mode, probably from a proper IDE, which
     * disallows the {@link greenfoot.UserInfo UserInfo} class to store any data.
     */
    STANDALONE;

    @Override
    public String toString() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase() + " session";
    }

    public boolean isJava() {
        return this != ONLINE;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean supportsUserInfo() {
        return this != STANDALONE;
    }

    public boolean supportsColoredOutput() {
        return this == STANDALONE;
    }
}
