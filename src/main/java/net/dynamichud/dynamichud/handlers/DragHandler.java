package net.dynamichud.dynamichud.handlers;

import net.dynamichud.dynamichud.Widget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.minecraft.client.MinecraftClient;

public interface DragHandler {
    boolean startDragging(Widget widget, double mouseX, double mouseY);
    void updateDragging(Widget widget, double mouseX, double mouseY);
    void stopDragging(Widget widget);
}

