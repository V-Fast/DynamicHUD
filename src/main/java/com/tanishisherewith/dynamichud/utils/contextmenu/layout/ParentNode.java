package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a node that can contain child layout nodes.
 * Automatically delegates rendering, bounds checking, and input events to its children.
 */
public abstract class ParentNode implements LayoutNode {
    protected final List<LayoutNode> children = new ArrayList<>();
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public void addChild(LayoutNode child) {
        children.add(child);
    }

    public List<LayoutNode> getChildren() {
        return children;
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
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (!shouldRender()) return;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.render(graphics, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!shouldRender()) return false;
        for (LayoutNode child : children) {
            if (child.shouldRender() && child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!shouldRender()) return false;
        for (LayoutNode child : children) {
            if (child.shouldRender() && child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!shouldRender()) return false;
        for (LayoutNode child : children) {
            if (child.shouldRender() && child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!shouldRender()) return;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        }
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (!shouldRender()) return;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.keyPressed(key, scanCode, modifiers);
            }
        }
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
        if (!shouldRender()) return;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.keyReleased(key, scanCode, modifiers);
            }
        }
    }

    @Override
    public void charTyped(char c, int modifiers) {
        if (!shouldRender()) return;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.charTyped(c, modifiers);
            }
        }
    }

    @Override
    public boolean shouldRender() {
        for (LayoutNode child : children) {
            if (child.shouldRender()) return true;
        }
        return false;
    }
}
