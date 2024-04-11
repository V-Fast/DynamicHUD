package com.tanishisherewith.dynamichud.widget;

public class WidgetBox {
    private float width;
    private float height;
    public float x1 = 0, x2 = 0, y1 = 0, y2 = 0;

    public WidgetBox(float x1, float y1, float x2, float y2, float scale) {
        this.width = (x2 - x1) * scale;
        this.height = (y2 - y1) * scale;
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

    public boolean intersects(float otherX1, float otherY1, float otherX2, float otherY2) {
        return !(otherX1 > x2 || otherX2 < x1 || otherY1 > y2 || otherY2 < y1);
    }
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x1 && mouseX <= x1 + width && mouseY >= y1 && mouseY <= y2 + height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setSizeAndPosition(float x, float y, float width, float height){
        this.x1 = x;
        this.y1 = y;
        this.height = height;
        this.width = width;
    }
    public void setSizeAndPosition(float x, float y, float width, float height,boolean shouldScale, float scale){
        this.x1 = x;
        this.y1 = y;
        this.height = height * (shouldScale? scale : 1.0f);
        this.width = width  * (shouldScale? scale : 1.0f);
    }
    public void setSize(double width, double height) {
        if (width >= 0)
            this.width = (int) Math.ceil(width);
        if (height >= 0)
            this.height = (int) Math.ceil(height);
    }
    public void setPosition(float x,float y, float x2, float y2){
        this.x1 = x;
        this.x2 = x2;
        this.y1 = y;
        this.y2 = y2;
    }
}
