package net.dynamichud.dynamichud.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages a list of widgets that can be added, removed and retrieved.
 */
public class WidgetManager {
    private final List<Widget> widgets = new ArrayList<>(); // The list of widgets

    /**
     * Adds a widget to the list.
     *
     * @param widget The widget to add
     */
    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     * Removes a widget from the list.
     *
     * @param widget The widget to remove
     */
    public void removeWidget(Widget widget) {
        widgets.remove(widget);
    }

    /**
     * Returns list of all widgets.
     *@return list of all widgets.
     */
    public List<Widget> getWidgets() {
        return widgets;
    }
}