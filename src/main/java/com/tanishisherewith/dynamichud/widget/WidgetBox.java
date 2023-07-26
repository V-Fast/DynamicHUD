package com.tanishisherewith.dynamichud.widget;

public class WidgetBox {
    private final float width;
    private final float height;
    public float x1 = 0, x2 = 0, y1 = 0, y2 = 0;

    public WidgetBox(float x1, float y1, float x2, float y2, float scale) {
        this.width = (float) ((x2 - x1) * scale);
        this.height = (float) ((y2 - y1) * scale);
        this.x1 = x1;
        this.x2 = x1 + width;
        this.y1 = y1;
        this.y2 = y1 + height;
    }

    public WidgetBox(float x1, float y1, double width, double height, float scale) {
        this.width = (float) (width * scale);
        this.height = (float) (height * scale);
        this.x1 = x1;
        this.x2 = x1 + this.width;
        this.y1 = y1;
        this.y2 = y1 + this.height;
    }

    public boolean contains(Widget widget, double x, double y, float scale) {
        if (x1 == 0 || x2 == 0 || y1 == 0 || y2 == 0) {
            x1 = widget.getX() - width / 2;
            y1 = widget.getY() - height / 2;
            x2 = widget.getX() + width / 2;
            y2 = widget.getY() + height / 2;
        }
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    public boolean intersects(float otherX1, float otherY1, float otherX2, float otherY2, float scale) {
        return !(otherX1 > x2 || otherX2 < x1 || otherY1 > y2 || otherY2 < y1);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
