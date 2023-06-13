package net.dynamichud.dynamichud.handlers;

import net.dynamichud.dynamichud.widget.Widget;

public interface DragHandler {
    boolean startDragging(Widget widget, double mouseX, double mouseY);

    void updateDragging(Widget widget, double mouseX, double mouseY);

    void stopDragging(Widget widget);
}

