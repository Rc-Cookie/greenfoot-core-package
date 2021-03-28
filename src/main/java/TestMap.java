import com.github.rccookie.common.geometry.Vector;
import com.github.rccookie.common.util.Console;
import com.github.rccookie.greenfoot.core.*;

import greenfoot.GreenfootImage;

public class TestMap extends Map {

    public TestMap() {
        super(1200, 400);
        add(new TestGameObject(), Vector.of(100, 100));
    }

    public static final void test() {
        Core.getMap().find(TestGameObject.class).ifPresent(o -> {
            GreenfootImage gImage = Image.asGImage(o.getImage());
            Console.map("GImage dim", gImage.getWidth() + "x" + gImage.getHeight());
            Console.map("Image dim", o.getImage().getWidth() + "x" + o.getImage().getHeight());
        });
    }

    @Override
    public void render() {
        super.render();
    }
}
