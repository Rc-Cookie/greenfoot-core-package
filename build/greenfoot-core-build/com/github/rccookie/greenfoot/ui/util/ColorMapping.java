package com.github.rccookie.greenfoot.ui.util;

/**
 * A color mapping containing the mapped index and weather it is a text or normal color.
 */
public class ColorMapping {
    public final int index;
    public final boolean isText;

    public ColorMapping(int index, boolean isText) {
        this.index = index;
        this.isText = isText;
    }

    @Override
    public int hashCode() {
        return index * (isText ? -1 : 1);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof ColorMapping)) return false;
        return ((ColorMapping) obj).index == index && ((ColorMapping) obj).isText == isText;
    }
}
