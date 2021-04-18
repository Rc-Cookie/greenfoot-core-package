import com.github.rccookie.greenfoot.core.GameObject;
import com.github.rccookie.greenfoot.core.KeyState;

public class TestObject extends GameObject {

    @Override
    public void update() {
        if(KeyState.of("space").down) velocity().setX(100);
        else velocity().setZero();
    }
}
