package com.github.rccookie.greenfoot.ui.advanced;

import greenfoot.Greenfoot;
import com.github.rccookie.greenfoot.ui.basic.Text;

public class NumberField extends TextField {

    public static final double DEFAULT_RETURN = 0;

    public boolean useDoubles = true;


    public NumberField() {
        this(new Text("Enter a number"));
    }

    public NumberField(Text defaultText) {
        this(defaultText, true);
    }

    public NumberField(Text defaultText, boolean useDoubles) {
        this(defaultText, 150, useDoubles);
    }
    
    public NumberField(Text defaultText, int width, boolean useDoubles) {
        super(defaultText, width);
        this.useDoubles = useDoubles;
    }

    @Override
    protected void setupAction(String question) {
        addClickAction(() -> setContent(question));
    }

    private void setContent(String question) {
        String input = Greenfoot.ask(question);
        try{
            if(useDoubles) setTitle(Double.parseDouble(input) + "");
            else setTitle((int)Double.parseDouble(input) + "");
        }catch(Exception e) {}
    }


    public double getDouble() {
        try{
            return Double.parseDouble(getTitle());
        }
        catch(Exception e) {
            return DEFAULT_RETURN;
        }
    }

    public int getInt() {
        return (int)getDouble();
    }
}
