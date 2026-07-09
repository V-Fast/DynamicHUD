package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * A wrapper node that applies custom top, bottom, left, and right padding to its child node.
 */
public class PaddingNode implements LayoutNode {
    private final LayoutNode child;
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;
    private int x;
    private int y;
    private int width;
    private int height;

    public PaddingNode(LayoutNode child, int left, int right, int top, int bottom) {
        this.child = child;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public PaddingNode(LayoutNode child, int padding) {
        this(child, padding, padding, padding, padding);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (child != null) {
            child.setPosition(x + left, y + top);
        }
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void layout(int maxWidth) {
        if (!shouldRender()) return;
        child.layout(maxWidth - left - right);
        setSize(maxWidth, child.getHeight() + top + bottom);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (!shouldRender()) return;
        child.render(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!shouldRender()) return false;
        return child.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!shouldRender()) return false;
        return child.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!shouldRender()) return false;
        return child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!shouldRender()) return;
        child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (!shouldRender()) return;
        child.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
        if (!shouldRender()) return;
        child.keyReleased(key, scanCode, modifiers);
    }

    @Override
    public void charTyped(char c, int modifiers) {
        if (!shouldRender()) return;
        child.charTyped(c, modifiers);
    }

    @Override
    public boolean shouldRender() {
        return child != null && child.shouldRender();
    }
}
