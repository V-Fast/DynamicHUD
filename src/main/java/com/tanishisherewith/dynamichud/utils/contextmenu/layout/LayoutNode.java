package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Interface representing a node in the context menu layout tree.
 * Nodes manage their position, layout constraints, render logic, and event propagation.
 */
public interface LayoutNode {
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    void setPosition(int x, int y);
    void setSize(int width, int height);
    void layout(int maxWidth);
    void render(GuiGraphics graphics, int mouseX, int mouseY);
    boolean mouseClicked(double mouseX, double mouseY, int button);
    boolean mouseReleased(double mouseX, double mouseY, int button);
    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);
    void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    void keyPressed(int key, int scanCode, int modifiers);
    void keyReleased(int key, int scanCode, int modifiers);
    void charTyped(char c, int modifiers);
    boolean shouldRender();
}
