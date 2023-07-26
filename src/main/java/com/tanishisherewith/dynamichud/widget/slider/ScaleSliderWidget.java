package com.tanishisherewith.dynamichud.widget.slider;

import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import java.text.DecimalFormat;

public class ScaleSliderWidget extends SliderWidget {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private final float minValue;
    private final float maxValue;

    public ScaleSliderWidget(int x, int y, int width, int height, Text message, double value, float minValue, float maxValue) {
        super(x, y, width, height, message, value);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public float getValue(){
        return (float) (minValue + (maxValue - minValue) * value);
    }

    @Override
    protected void updateMessage() {
        String formattedValue = DECIMAL_FORMAT.format(getValue());
        setMessage(Text.of("Widgets Scale: " + formattedValue));
    }

    @Override
    protected void applyValue() {
        Widget.setScale(getValue());
    }

}
