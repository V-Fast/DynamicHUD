package com.tanishisherewith.dynamichud.handlers;

import com.tanishisherewith.dynamichud.widget.slider.SliderWidget;
import com.tanishisherewith.dynamichud.util.colorpicker.ColorGradientPicker;
import com.tanishisherewith.dynamichud.util.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.widget.text.TextWidget;

public class DefaultMouseHandler implements MouseHandler {
    private final ColorGradientPicker colorPicker;
    private final ContextMenu contextMenu;
    private final SliderWidget sliderWidget;

    public DefaultMouseHandler(ColorGradientPicker colorPicker, ContextMenu contextMenu, SliderWidget sliderWidget) {
        this.colorPicker = colorPicker;
        this.contextMenu = contextMenu;
        this.sliderWidget = sliderWidget;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (contextMenuClicked(mouseX, mouseY, button)) {
            return true;
        }
            if (colorPickerClicked(mouseX, mouseY, button)) {
                return true;
            }
        return sliderClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (sliderWidget != null && sliderWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            TextWidget.setRainbowSpeed(sliderWidget.getValue());
            return true;
        }
            if (this.colorPicker!=null) {
                colorPicker.mouseDragged(mouseX, mouseY, button);
                return true;
            }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.colorPicker!=null)
        {
            colorPicker.mouseReleased(mouseX,mouseY,button);
            return true;
        }
        return false;
    }


    @Override
    public boolean contextMenuClicked(double mouseX, double mouseY, int button) {
        return contextMenu != null && contextMenu.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean colorPickerClicked(double mouseX, double mouseY, int button) {
        return colorPicker != null && colorPicker.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean sliderClicked(double mouseX, double mouseY, int button) {
        return sliderWidget != null && sliderWidget.mouseClicked(mouseX, mouseY, button);
    }
}
