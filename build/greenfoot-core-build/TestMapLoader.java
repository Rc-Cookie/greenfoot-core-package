import com.github.rccookie.greenfoot.core.Map.MapLoader;

public class TestMapLoader extends MapLoader {

    public TestMapLoader() {
        super(600, 400, () -> new TestMap());
    }
}
