import com.github.rccookie.greenfoot.core.CoreWorld;
import com.github.rccookie.greenfoot.core.FontStyle;
import com.github.rccookie.greenfoot.core.Image;

import greenfoot.Color;

/**
 * This world is being displayed online and offline.
 */
public class TestWorld extends CoreWorld {

    String p = "This scenario adds options to use different fonts than the default one\nused by Greenfoot. While the default font may look decent offline,\nit's a different one online that looks aweful. You have always been\nable to use different fonts, however there is one major downside\nto using other fonts: When using the default font you can create an\nimage with some text on it and it will automatically have the fitting\nsize. However to use a custom font you have to print on existing\nimage which requires you to know the dimensions that that text will\nhave. To work around that problem I have started to - its great fun -\nmeasure each character from a font and create some classes that then \nuse that measured size. So far I have got this font and a monospace\nfont which was simple.";

    // (Consolas), Candera, Calibri, Century Gothic, (Segoe UI)
    
    public TestWorld() {
        super(600, 400);
        addRelative(Image.text(">> System.out.println(\"Hello World!\")\n\nThis is a monospace font, 'Consolas', used for coding.\nIt's the default editor font in Visual Studio Code.", Color.RED, FontStyle.monospace(16)).asActor(), 0.5, 0.1);
        addRelative(Image.text("This is a modern font, 'Segoe UI', nice for paragraphs.\n\n" + p, Color.DARK_GRAY, FontStyle.modern(16)).asActor(), 0.5, 0.6);
        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font("Candera", false, false, 16)).asActor(), 0.5, 0.5);
        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font("Calibri", false, false, 16)).asActor(), 0.5, 0.7);
        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font("Century Gothic", false, false, 16)).asActor(), 0.5, 0.9);

        //addRelative(Image.text(">> Hello\n   World!\nThis is a proper text, longer than just two words.\n", Color.RED, new Font(20)).asActor(), 0.5, 0.75);
    }
}
