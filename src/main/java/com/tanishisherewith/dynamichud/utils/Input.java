package com.tanishisherewith.dynamichud.utils;


public interface Input {
    boolean mouseClicked(double mouseX, double mouseY, int button);

    boolean mouseReleased(double mouseX, double mouseY, int button);

    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    void keyPressed(int key, int scanCode, int modifiers);

    void keyReleased(int key, int scanCode, int modifiers);

    void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);

    default boolean isMouseOver(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    void charTyped(char c, int modifiers);
}
