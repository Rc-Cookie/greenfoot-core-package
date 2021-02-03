package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Page extends Structure {
    
    private final String pageId;

    final List<Runnable> onLoad = new ArrayList<>();

    public Page(String pageId, Widget... children) {
        super(children);
        Objects.requireNonNull(pageId);
        this.pageId = pageId;
    }

    public String getPageId() {
        return pageId;
    }

    @Override
    public String toString() {
        return super.toString() + "(\"" + getPageId() + "\")";
    }

    public Page addOnLoad(Runnable method) {
        if(method == null) return this;
        onLoad.add(method);
        return this;
    }
}
