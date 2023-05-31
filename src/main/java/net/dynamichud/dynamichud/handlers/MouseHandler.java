package net.dynamichud.dynamichud.handlers;

public interface MouseHandler {
    boolean mouseClicked(double mouseX, double mouseY, int button);
    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);
        boolean contextMenuClicked(double mouseX, double mouseY, int button);
        boolean colorPickerClicked(double mouseX, double mouseY, int button);
        boolean sliderClicked(double mouseX, double mouseY, int button);



}
