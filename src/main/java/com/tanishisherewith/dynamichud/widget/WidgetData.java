package com.tanishisherewith.dynamichud.widget;

import java.util.function.Supplier;

public record WidgetData<T extends Widget>(String name, String description, Supplier<T> widgetFactory) {
    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    public Widget createWidget() {
        return widgetFactory.get();
    }

}
