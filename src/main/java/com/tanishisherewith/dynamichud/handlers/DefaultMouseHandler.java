package com.tanishisherewith.dynamichud.handlers;

import com.tanishisherewith.dynamichud.util.colorpicker.ColorGradientPicker;
import com.tanishisherewith.dynamichud.util.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.widget.slider.SliderWidget;
import com.tanishisherewith.dynamichud.widget.text.TextWidget;

import java.util.List;

public class DefaultMouseHandler implements MouseHandler {
    private final ColorGradientPicker colorPicker;
    private final List<ContextMenu> contextMenu;
    private final List<SliderWidget> sliderWidget;

    public DefaultMouseHandler(ColorGradientPicker colorPicker, List<ContextMenu> contextMenu, List<SliderWidget> sliderWidget) {
        this.colorPicker = colorPicker;
        this.contextMenu = contextMenu;
        this.sliderWidget = sliderWidget;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (contextMenu != null && sliderWidget != null) {
            for (ContextMenu contextMenu : contextMenu) {
                if (contextMenuClicked(mouseX, mouseY, button, contextMenu)) {
                    return true;
                }
            }
            for (SliderWidget sliderWidget : sliderWidget) {
                if (sliderClicked(mouseX, mouseY, button, sliderWidget)) {
                    return true;
                }
            }
        }
        return colorPickerClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (sliderWidget != null) {
            for (SliderWidget sliderWidget : sliderWidget) {
                if (sliderWidget != null && sliderWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                    return true;
                }
            }
        }
        if (this.colorPicker != null) {
            colorPicker.mouseDragged(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.colorPicker != null) {
            colorPicker.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return false;
    }


    @Override
    public boolean contextMenuClicked(double mouseX, double mouseY, int button, ContextMenu contextMenu) {
        return contextMenu != null && contextMenu.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean colorPickerClicked(double mouseX, double mouseY, int button) {
        return colorPicker != null && colorPicker.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean sliderClicked(double mouseX, double mouseY, int button, SliderWidget sliderWidget) {
        return sliderWidget != null && sliderWidget.mouseClicked(mouseX, mouseY, button);
    }
}
