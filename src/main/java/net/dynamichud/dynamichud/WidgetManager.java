package net.dynamichud.dynamichud;

import java.util.ArrayList;
import java.util.List;

public class WidgetManager {
    private final List<Widget> widgets = new ArrayList<>();

    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    public void removeWidget(Widget widget) {
        widgets.remove(widget);
    }

    public List<Widget> getWidgets() {
        return widgets;
    }
}
