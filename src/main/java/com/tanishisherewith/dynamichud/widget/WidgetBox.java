package com.tanishisherewith.dynamichud.widget;

public class WidgetBox {
    private final int width;
    private final int height;
    public int x1 = 0, x2 = 0, y1 = 0, y2 = 0;

    public WidgetBox(int x1, int y1, int x2, int y2) {
        this.width = x2 - x1;
        this.height = y2 - y1;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public boolean contains(Widget widget, double x, double y) {
        if (x1 == 0 || x2 == 0 || y1 == 0 || y2 == 0) {
            x1 = widget.getX() - width / 2;
            y1 = widget.getY() - height / 2;
            x2 = widget.getX() + width / 2;
            y2 = widget.getY() + height / 2;
        }
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }
    public boolean intersects(int otherX1, int otherY1, int otherX2, int otherY2) {
        return !(otherX1 > x2 || otherX2 < x1 || otherY1 > y2 || otherY2 < y1);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
