package com.github.rccookie.greenfoot.ui.basic;

import com.github.rccookie.common.geometry.Vector2D;

class RelativeLocation {
    final Vector2D relative, offset;

    public RelativeLocation(Vector2D relative, Vector2D offset) {
        this.relative = relative;
        this.offset = offset;
    }
    
    public RelativeLocation(double relX, double relY, double offX, double offY) {
        this(new Vector2D(relX, relY), new Vector2D(offX, offY));
    }
}