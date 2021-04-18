import com.github.rccookie.common.geometry.Vector;
import com.github.rccookie.greenfoot.core.Map;

public class TestMap extends Map {

    public TestMap() {
        add(new TestObject(), Vector.of(100, 100));
    }
}
