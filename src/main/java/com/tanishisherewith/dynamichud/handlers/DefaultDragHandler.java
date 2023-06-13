package com.tanishisherewith.dynamichud.handlers;

import com.tanishisherewith.dynamichud.widget.Widget;

public class DefaultDragHandler implements DragHandler {
    private int dragStartX = 0;
    private int dragStartY = 0;

    @Override
    public boolean startDragging(Widget widget, double mouseX, double mouseY) {
        if (widget.getWidgetBox().contains(widget, mouseX, mouseY)) {
            dragStartX = (int) (mouseX - widget.getX());
            dragStartY = (int) (mouseY - widget.getY());
            return true;
        }
        return false;
    }

    @Override
    public void updateDragging(Widget widget, double mouseX, double mouseY) {
        int newX = (int) (mouseX - dragStartX);
        int newY = (int) (mouseY - dragStartY);
        widget.setX(newX);
        widget.setY(newY);
    }

    @Override
    public void stopDragging(Widget widget) {
        // Nothing to do here in the default implementation
    }
}

