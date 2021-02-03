package com.github.rccookie.greenfoot.game.util;

import java.util.Set;

import greenfoot.Actor;
import com.github.rccookie.common.util.TagTable;

public class ActorTag {
    
    private static final TagTable<Actor, String> TAG_TABLE = new TagTable<>();


    public static final boolean tag(Actor actor, String tag) {
        return TAG_TABLE.tag(actor, tag);
    }

    public static final boolean untag(Actor actor, String tag) {
        return TAG_TABLE.untag(actor, tag);
    }

    public static final Set<Actor> getTagged(String tag) {
        return TAG_TABLE.getTagged(tag);
    }

    public static final Set<String> getTags(Actor actor) {
        return TAG_TABLE.getTags(actor);
    }


    private ActorTag() {}
}
