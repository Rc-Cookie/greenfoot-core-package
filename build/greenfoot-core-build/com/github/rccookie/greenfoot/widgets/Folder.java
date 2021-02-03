package com.github.rccookie.greenfoot.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Folder extends Structure {

    private Page active = null;
    
    public Folder(Page... pages) {
        addChildren(pages);
        if(pages != null && pages.length != 0) {
            (active = pages[0]).setVisible(true);
            for(int i=1; i<pages.length; i++) pages[i].setVisible(false);
        }
    }

    public Folder setPage(String pageId) {
        if(active != null) {
            if(Objects.equals(active.getPageId(), pageId)) return this;
            active.setVisible(false);
        }
        if(pageId == null) return this;
        active = null;
        List<Widget> pages = new ArrayList<>(getChildren());
        for(Widget page : pages) {
            if(page instanceof Page && Objects.equals(page.as(Page.class).getPageId(), pageId)) {
                setPage(page.as(Page.class));
                return this;
            }
        }
        return this;
    }

    private void setPage(Page page) {
        (active = page).setVisible(true);
        for(Runnable method : page.onLoad) method.run();
    }
}
