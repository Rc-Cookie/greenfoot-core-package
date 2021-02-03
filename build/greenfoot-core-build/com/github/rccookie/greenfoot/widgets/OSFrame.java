package com.github.rccookie.greenfoot.widgets;

import greenfoot.GreenfootImage;

public class OSFrame extends Structure {

    static final String DESKTOP = "os_desktop";
    static final String WINDOWS = "os_windows";
    static final String TASK_BAR = "os_task_bar";
    
    public OSFrame(Window... windows) {
        addChildren(
            Align.top(
                new Dimension(
                    0,
                    -TaskBar.HEIGHT,
                    new Desktop().setId(DESKTOP),
                    new Structure().addChildren(windows).setId(WINDOWS)
                )
            ),
            Align.bottom(
                new TaskBar().setId(TASK_BAR)
            )
        );
    }



    public OSFrame addWindow(Window window) {
        if(window == null) return this;
        find(WINDOWS).as(Structure.class).addChild(window);
        return this;
    }







    public class Desktop extends Structure {
    
        static final String WALLPAPER_BASE = "desktop_wallpaper_base";
    
        private Desktop() {
            super(
                new Structure(
                    new Color(Color.LIGHT_GRAY.brighter().brighter())
                ).setId(WALLPAPER_BASE)
            );
        }

        public Visual getWallpaper() {
            return find(WALLPAPER_BASE).as(Structure.class).find(Visual.class);
        }

        public Desktop setWallpaper(Visual wallpaper) {
            Structure base = find(WALLPAPER_BASE).as(Structure.class);
            base.removeChild(Visual.class);
            base.addChild(wallpaper);
            return this;
        }
    }

    public class TaskBar extends Dimension {

        static final int HEIGHT = 40;

        private TaskBar() {
            super(0, 40);
            addChildren(
                new Color(new greenfoot.Color(36, 32, 26)),
                Align.left(
                    new TaskBarButton(
                        Icon.loadIconImage("windows_normal"),
                        () -> System.out.println("Click")
                    ).setTouched(Icon.loadIconImage("windows_touched"))
                )
            );
        }

        public class TaskBarButton extends Dimension {

            private static final String BUTTON = "image_button";

            private TaskBarButton(GreenfootImage image, Runnable onClick) {
                super(
                    48,
                    40,
                    new ImageButton(
                        image,
                        onClick
                    ) {
                        protected GreenfootImage renderPressed(GreenfootImage base) {
                            GreenfootImage image = new GreenfootImage(getTouched() != null ? getTouched() : base);
                            image.setColor(CLICK_COLOR_CORRECTION);
                            image.fill();
                            return image;
                        };
                    }.setId(BUTTON)
                );
            }

            TaskBarButton setTouched(GreenfootImage touched) {
                find(BUTTON).as(ImageButton.class).setTouched(touched);
                return this;
            }
        }
    }
}
