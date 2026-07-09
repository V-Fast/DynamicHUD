package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * A layout node that occupies a fixed amount of horizontal and vertical space without rendering anything.
 */
public class SpacerNode implements LayoutNode {
    private int x;
    private int y;
    private final int spacerWidth;
    private final int spacerHeight;

    public SpacerNode(int width, int height) {
        this.spacerWidth = width;
        this.spacerHeight = height;
    }

    public SpacerNode(int height) {
        this(0, height);
    }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    @Override
    public int getWidth() { return spacerWidth; }

    @Override
    public int getHeight() { return spacerHeight; }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setSize(int width, int height) {}

    @Override
    public void layout(int maxWidth) {}

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) { return false; }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) { return false; }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {}

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {}

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {}

    @Override
    public void charTyped(char c, int modifiers) {}

    @Override
    public boolean shouldRender() { return true; }
}
