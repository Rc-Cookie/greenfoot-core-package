import com.github.rccookie.greenfoot.core.CoreWorld;
import com.github.rccookie.greenfoot.core.Image;

import greenfoot.Color;
import greenfoot.Font;

public class TestWorld extends CoreWorld {
    
    public TestWorld() {
        super(600, 400);
        Font f = new Font("Consolas", false, false, 30);
        Image i = Image.text("AgggggggggggM\nNOPQRSTUVWXyZ", Color.RED, f);
        addRelative(i.asActor(), 0.5, 0.5);
    }
}
