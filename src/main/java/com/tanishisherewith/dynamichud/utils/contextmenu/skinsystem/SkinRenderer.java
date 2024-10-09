package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import net.minecraft.client.gui.DrawContext;

public interface SkinRenderer<T extends Option<?>>  {
    void render(DrawContext drawContext, T option, int x, int y, int mouseX, int mouseY);

    default boolean mouseClicked(T option, double mouseX, double mouseY, int button) {
        return option.isMouseOver(mouseX, mouseY);
    }

    default boolean mouseReleased(T option, double mouseX, double mouseY, int button) {
        return option.isMouseOver(mouseX, mouseY);
    }

    default boolean mouseDragged(T option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return option.isMouseOver(mouseX, mouseY);
    }

    default void keyPressed(T option, int key, int scanCode, int modifiers) {
    }

    default void keyReleased(T option, int key, int scanCode, int modifiers) {
    }

    default void mouseScrolled(T option, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    }

    default void init(T option) {
    }
}
