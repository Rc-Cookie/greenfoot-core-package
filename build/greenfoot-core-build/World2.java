import greenfoot.*;

import java.util.Optional;
import java.util.stream.*;

import com.github.rccookie.common.util.Console;

public class World2 extends World {
    public World2() {
        super(600, 400, 1);
        Stream.of("a", "b", "z").forEach(s -> System.out.println(s));
        Optional<String> o = Optional.of("Hello World!");
        Console.info("Starting");
        Greenfoot.start();
    }
    
    public void act() {
        Greenfoot.stop();
        Greenfoot.setWorld(new TestWorld());
    }
}
