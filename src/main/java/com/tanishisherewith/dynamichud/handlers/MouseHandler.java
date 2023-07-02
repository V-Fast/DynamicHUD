package com.tanishisherewith.dynamichud.handlers;

import com.tanishisherewith.dynamichud.util.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.widget.slider.SliderWidget;

public interface MouseHandler {
    boolean mouseClicked(double mouseX, double mouseY, int button);

    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);
    boolean mouseReleased(double mouseX, double mouseY, int button);

    boolean contextMenuClicked(double mouseX, double mouseY, int button, ContextMenu contextMenu);

    boolean colorPickerClicked(double mouseX, double mouseY, int button);

    boolean sliderClicked(double mouseX, double mouseY, int button, SliderWidget sliderWidget);


}

