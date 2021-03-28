package com.github.rccookie.greenfoot.ui.util;

import java.io.Serializable;
import java.util.Objects;

import com.github.rccookie.greenfoot.ui.UIWorld;

public class Design implements Cloneable, Serializable {


    public static final Design DARK_MODE = new Design(Theme.DARK_MODE, Theme.DARK_MODE_TEXT);
    public static final Design LIGHT_MODE = new Design(Theme.LIGHT_MODE, Theme.LIGHT_MODE_TEXT);
    public static final Design DEBUG = new Design(Theme.DEBUG, Theme.DEBUG_TEXT);
    public static final Design ERROR = new Design(Theme.ERROR, Theme.ERROR_TEXT);

    public static final Design DEFAULT = new DefaultDesign();

    private static final long serialVersionUID = -566036656977062509L;



    private final Theme theme;
    private final Theme textTheme;

    public Design(Theme theme, Theme textTheme) {
        this.theme = theme;
        this.textTheme = textTheme;
    }

    public Design(Design copy) {
        this(copy.theme().clone(), copy.textTheme().clone());
    }

    @Override
    public Design clone() {
        return new Design(this);
    }

    public Theme theme() {
        return theme;
    }

    public Theme textTheme() {
        return textTheme;
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Design)) return false;
        return Objects.equals(theme(), ((Design)obj).theme()) && Objects.equals(textTheme(), ((Design)obj).textTheme());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theme(), textTheme());
    }



    public static final void debug() { setDebug(true); }
    public static final void setDebug(boolean flag) { ((DefaultDesign)DEFAULT).setDebugState(flag); }

    public static final void useDarkMode() { ((DefaultDesign)DEFAULT).setDesign(DARK_MODE); }
    public static final void useLightMode() { ((DefaultDesign)DEFAULT).setDesign(LIGHT_MODE); }
    public static final void setDefault(Design design) { ((DefaultDesign)DEFAULT).setDesign(design); }



    private static class DefaultDesign extends Design {

        private static final long serialVersionUID = -1131237985703337255L;

        private static final Design DEFAULT_DESIGN = LIGHT_MODE;

        private Design currentDefaultDesign = DEFAULT_DESIGN;
        private boolean debug = false;

        private DefaultDesign() {
            super(DEFAULT_DESIGN);
        }

        @Override
        public Theme theme() {
            return debug ? Theme.DEBUG : currentDefaultDesign.theme();
        }

        @Override
        public Theme textTheme() {
            return debug ? Theme.DEBUG_TEXT : currentDefaultDesign.textTheme();
        }

        private void setDebugState(boolean flag) {
            if(debug == flag) return;
            debug = flag;
            informAboutChange();
        }

        private void setDesign(Design newDefaultDesign) {
            if(Objects.equals(newDefaultDesign, currentDefaultDesign)) return;
            currentDefaultDesign = newDefaultDesign;
            informAboutChange();
        }

        private void informAboutChange() {
            try{
                for(UIElement element : greenfoot.core.WorldHandler.getInstance().getWorld().getObjects(UIElement.class)) {
                    element.imageChanged();
                }
                ((UIWorld)greenfoot.core.WorldHandler.getInstance().getWorld()).backgroundChanged();
            } catch(Exception e) { }
        }
    }
}
