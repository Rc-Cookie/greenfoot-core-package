package com.github.rccookie.greenfoot.widgets;

public final class Size {
    public final int width, height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Size)) return false;
        Size c = (Size)obj;
        return c.width == width && c.height == height;
    }

    @Override
    public String toString() {
        return "[" + width + " x " + height + "]";
    }

    public SizeModifier modify() {
        return new SizeModifier(this);
    }

    static class SizeModifier {
        private int width, height;
    
        private SizeModifier(Size unmodified) {
            width = unmodified.width;
            height = unmodified.height;
        }

        public SizeModifier setWidth(int maxWidth) {
            this.width = maxWidth;
            return this;
        }

        public SizeModifier setHeight(int maxHeight) {
            this.height = maxHeight;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Size build() {
            return new Size(width, height);
        }
    }
}
