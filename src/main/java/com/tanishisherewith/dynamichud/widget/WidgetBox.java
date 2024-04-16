package com.tanishisherewith.dynamichud.widget;

public class WidgetBox {
    public float x = 0, y = 0;
    private float width;
    private float height;

    public WidgetBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean intersects(WidgetBox other) {
        // Check if this box is to the right of the other box
        if (this.x > other.x + other.width) {
            return false;
        }

        // Check if this box is to the left of the other box
        if (this.x + this.width < other.x) {
            return false;
        }

        // Check if this box is below the other box
        if (this.y > other.y + other.height) {
            return false;
        }

        // Check if this box is above the other box
        // If none of the above conditions are met, the boxes must intersect
        return !(this.y + this.height < other.y);
    }

    public void setSizeAndPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public void setSizeAndPosition(float x, float y, float width, float height, boolean shouldScale, float scale) {
        this.x = x;
        this.y = y;
        this.height = height * (shouldScale ? scale : 1.0f);
        this.width = width * (shouldScale ? scale : 1.0f);
    }

    public void setSize(double width, double height, boolean shouldScale, float scale) {
        if (width >= 0)
            this.width = (int) Math.ceil(width * (shouldScale ? scale : 1.0f));
        if (height >= 0)
            this.height = (int) Math.ceil(height * (shouldScale ? scale : 1.0f));
    }
}
