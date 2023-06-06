package net.dynamichud.dynamichud.handlers;

import net.dynamichud.dynamichud.Util.ColorPicker;
import net.dynamichud.dynamichud.Util.ContextMenu;
import net.dynamichud.dynamichud.Widget.SliderWidget.SliderWidget;
import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;

public class DefaultMouseHandler implements MouseHandler {
    private final ColorPicker colorPicker;
    private final ContextMenu contextMenu;
    private final SliderWidget sliderWidget;

    public DefaultMouseHandler(ColorPicker colorPicker, ContextMenu contextMenu, SliderWidget sliderWidget) {
        this.colorPicker = colorPicker;
        this.contextMenu = contextMenu;
        this.sliderWidget = sliderWidget;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (contextMenuClicked(mouseX,mouseY,button)) {
            return true;
        }
        if (colorPickerClicked(mouseX,mouseY,button)) {
            return true;
        }
        if (sliderClicked(mouseX,mouseY,button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX,double mouseY,int button,double deltaX,double deltaY) {
        if (sliderWidget != null && sliderWidget.mouseDragged(mouseX,mouseY,button,deltaX,deltaY)) {
            TextWidget.setRainbowSpeed(sliderWidget.getValue());
            return true;
        }
        return false;
    }

    @Override
    public boolean contextMenuClicked(double mouseX,double mouseY,int button) {
        return contextMenu != null && contextMenu.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public boolean colorPickerClicked(double mouseX,double mouseY,int button) {
        return colorPicker != null && colorPicker.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public boolean sliderClicked(double mouseX,double mouseY,int button) {
        return sliderWidget != null && sliderWidget.mouseClicked(mouseX,mouseY,button);
    }
}
