import com.github.rccookie.common.util.Console;
import com.github.rccookie.greenfoot.core.*;

public class TestGameObject extends GameObject {

    public TestGameObject() {
        Image image = Image.block(30, 30, Color.RED);
        setImage(image);
        addOnClick(() -> Console.log("Click"));
        time.addSecondListener(d -> Console.map("FPS", time.fps()));
    }

    @Override
    public void update() {
        if(KeyState.of("space").pressed) velocity().setX(100);
        else velocity().setZero();
    }
}
