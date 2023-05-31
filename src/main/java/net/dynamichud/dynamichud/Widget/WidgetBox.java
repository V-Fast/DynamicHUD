package net.dynamichud.dynamichud.Widget;

public class WidgetBox {
    private final int width;
    private final int height;

    public WidgetBox(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean contains(Widget widget, double x, double y) {
        int x1 = widget.getX() - width / 2;
        int y1 = widget.getY() - height / 2;
        int x2 = widget.getX() + width / 2;
        int y2 = widget.getY() + height / 2;
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
