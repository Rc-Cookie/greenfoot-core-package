package com.github.rccookie.common.consoleapp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.rccookie.common.geometry.Size;
import com.github.rccookie.common.geometry.Size2D;

public class ConsoleApp {

    private OutputStream output;

    private Size2D size;



    public ConsoleApp(Size size) {
        this(System.out, size);
    }

    public ConsoleApp(OutputStream output, Size size) {
        setOutput(output);
        setSize(size);
    }



    public void start() {
        new Thread(() -> run()).start();
    }

    public void run() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> update(), 0, 100, TimeUnit.MILLISECONDS);
    }

    private void update() {
        System.out.println();
        printImage();
    }



    private void printImage() {
        BufferedImage image = renderImage();
        StringBuilder string = new StringBuilder((image.getWidth() + 1) * image.getHeight());
        for(int i=0; i<image.getHeight(); i++) {
            for(int j=0; j<image.getWidth(); j++) {
                int argb = image.getRGB(j, i);
                int alpha = (argb >> 24) & 0xff;
                string.append(getCharForColor(((i+j)*20)%255));
            }
            if(i+1 < image.getHeight()) string.append('\n');
        }
        System.out.print(string);
    }



    private BufferedImage renderImage() {
        BufferedImage image = new BufferedImage((int)size.width(), (int)size.height(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        return image;
    }



    private static char getCharForColor(int color) {
        if(color <= 31) return        ' ';
        if(color <= 95) return  '\u2591';
        if(color <= 159) return '\u2592';
        if(color <= 223) return '\u2593';
        return                  '\u2588';
    }



    public OutputStream getOutput() {
        return output;
    }

    public Size2D getSize() {
        return size;
    }



    public void setOutput(OutputStream output) {
        this.output = Objects.requireNonNull(output);
    }

    public void setSize(Size size) {
        this.size = new Size2D(size);
    }



    public static void main(String[] args) {
        new ConsoleApp(new Size(20, 10)).start();
    }
}
