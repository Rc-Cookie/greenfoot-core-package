import com.github.rccookie.greenfoot.core.CoreWorld;
import com.github.rccookie.greenfoot.core.FontStyle;
import com.github.rccookie.greenfoot.core.Image;

import greenfoot.Color;

public class TestWorld extends CoreWorld {

    // (Consolas), Candera, Calibri, Century Gothic, (Segoe UI)
    
    public TestWorld() {
        super(600, 400);
        addRelative(Image.text(">> Hello\n   World!\nThis is a monospace font, 'Consolas', used for coding.\nIt's the default editor font in Visual Studio Code.", Color.RED, FontStyle.monospace(16)).asActor(), 0.5, 0.1);
        addRelative(Image.text(">> Hello\n   World!\nThis is a modern font, 'Segoe UI', nice for paragraphs.", Color.RED, FontStyle.modern(16)).asActor(), 0.5, 0.3);
        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font("Candera", false, false, 16)).asActor(), 0.5, 0.5);
        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font("Calibri", false, false, 16)).asActor(), 0.5, 0.7);
        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font("Century Gothic", false, false, 16)).asActor(), 0.5, 0.9);

        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font(20)).asActor(), 0.5, 0.75);
    }
}
