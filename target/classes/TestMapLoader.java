import com.github.rccookie.common.util.Console;
import com.github.rccookie.greenfoot.core.Core;
import com.github.rccookie.greenfoot.core.Map.MapLoader;

import greenfoot.GreenfootImage;

public class TestMapLoader extends MapLoader {

    public TestMapLoader() {
        super(() -> new TestMap());
        test();
    }

    public static final void test() {
        Console.map("Current world", Core.getWorld().getClass().getName());
        Console.map("Current map", Core.getMap());
    }

    @Override
    public GreenfootImage getBackground() {
        Console.info("Returning background");
        return super.getBackground();
    }
}
