package com.tanishisherewith.dynamichud.handlers;

import com.tanishisherewith.dynamichud.widget.Widget;

public interface DragHandler {
    boolean startDragging(Widget widget, double mouseX, double mouseY);

    void updateDragging(Widget widget, double mouseX, double mouseY);

    void stopDragging(Widget widget);
}

