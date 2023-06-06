package net.dynamichud.dynamichud.Widget.SliderWidget;

import net.dynamichud.dynamichud.Widget.Widget;
import net.minecraft.client.MinecraftClient;

public class SliderWidgetBuilder {
    private final MinecraftClient client;
    private int x;
    private int y;
    private int width;
    private int height;
    private String label;
    private float value;
    private float minValue;
    private float maxValue;
    private Widget selectedWidget;

    public SliderWidgetBuilder(MinecraftClient client) {
        this.client = client;
    }

    public SliderWidgetBuilder setX(int x) {
        this.x = x;
        return this;
    }

    public SliderWidgetBuilder setY(int y) {
        this.y = y;
        return this;
    }

    public SliderWidgetBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public SliderWidgetBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public SliderWidgetBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public SliderWidgetBuilder setValue(float value) {
        this.value = value;
        return this;
    }

    public SliderWidgetBuilder setMinValue(float minValue) {
        this.minValue = minValue;
        return this;
    }

    public SliderWidgetBuilder setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public SliderWidgetBuilder setSelectedWidget(Widget selectedWidget) {
        this.selectedWidget = selectedWidget;
        return this;
    }

    public SliderWidget build() {
        return new SliderWidget(client, x, y, width, height, label, value, minValue, maxValue, selectedWidget);
    }
}
