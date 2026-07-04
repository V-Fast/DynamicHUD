package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A leaf node wrapping a single option. Handles delegation of layout, position,
 * rendering, and inputs to the underlying option and its renderer.
 */
public class OptionNode implements LayoutNode {
    private final Option<?> option;
    private int x;
    private int y;
    private int width;
    private int height;

    public OptionNode(Option<?> option) {
        this.option = option;
    }

    public Option<?> getOption() {
        return option;
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
        option.setPosition(x, y);
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        option.setWidth(width);
        option.setHeight(height);
    }

    @Override
    public void layout(int maxWidth) {
        if (!shouldRender()) return;
        int optHeight = option.getHeight() > 0 ? option.getHeight() : DynamicHUD.MC.font.lineHeight;
        setSize(maxWidth, optHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!shouldRender()) return;
        option.render(graphics, x, y, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!shouldRender()) return false;
        return option.getRenderer().mouseClicked((Option) option, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!shouldRender()) return false;
        return option.getRenderer().mouseReleased((Option) option, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!shouldRender()) return false;
        return option.getRenderer().mouseDragged((Option) option, mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!shouldRender()) return;
        option.getRenderer().mouseScrolled((Option) option, mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (!shouldRender()) return;
        option.getRenderer().keyPressed((Option) option, key, scanCode, modifiers);
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
        if (!shouldRender()) return;
        option.getRenderer().keyReleased((Option) option, key, scanCode, modifiers);
    }

    @Override
    public void charTyped(char c, int modifiers) {
        if (!shouldRender()) return;
        option.charTyped(c, modifiers);
    }

    @Override
    public boolean shouldRender() {
        return option != null && option.shouldRender();
    }

    @Override
    public String toString() {
        return "OptionNode= " + option.toString();
    }
}
