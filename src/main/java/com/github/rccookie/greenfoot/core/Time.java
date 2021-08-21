package com.github.rccookie.greenfoot.core;

import com.github.rccookie.util.Arguments;

public final class Time {

    private Time() {
        throw new UnsupportedOperationException();
    }


    private static final com.github.rccookie.event.Time TIME_TIME = new com.github.rccookie.event.Time();


    static {
        Core.registerOnEarlyGlobalUpdate(TIME_TIME::update);
    }


    public static double deltaTime() {
        return TIME_TIME.deltaTime();
    }

    public static double fps() {
        return TIME_TIME.fps();
    }

    public static double stableFps() {
        return TIME_TIME.stableFps();
    }

    public static long frameIndex() {
        return TIME_TIME.frameIndex();
    }


    public static double seconds() {
        return TIME_TIME.time();
    }

    public static long millis() {
        return (long) (seconds() * 1000L);
    }

    public static long nanos() {
        return (long) (seconds() * 1000000000L);
    }

    public static double minutes() {
        return seconds() / 60;
    }

    public static double hours() {
        return seconds() / 3600;
    }

    public static double days() {
        return seconds() / 86400;
    }

    public static double years() {
        return seconds() / 31556736;
    }


    public static double getMaxDeltaTime() {
        return TIME_TIME.maxDeltaTime;
    }

    public static void setMaxDeltaTime(double maxDeltaTime) {
        Arguments.checkRange(maxDeltaTime, 0.01, Double.POSITIVE_INFINITY);
        TIME_TIME.maxDeltaTime = maxDeltaTime;
    }

    public static double getTimeScale() {
        return TIME_TIME.timeScale;
    }

    public static void setTimeScale(double timeScale) {
        TIME_TIME.timeScale = timeScale;
    }


    public boolean isStaticFrameLength() {
        return TIME_TIME.useStaticFrameLength;
    }

    public void useStaticFrameLength(boolean flag) {
        TIME_TIME.useStaticFrameLength = flag;
    }

    public double getStaticFrameLength() {
        return TIME_TIME.staticFrameLength;
    }

    public void setStaticFrameLength(double staticFrameLength) {
        TIME_TIME.staticFrameLength = staticFrameLength;
    }



    static void init() { }
}
