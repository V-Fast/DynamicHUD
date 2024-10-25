package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces;

import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import net.minecraft.client.gui.DrawContext;

public interface SkinRenderer<T extends Option<?>>  {
    void render(DrawContext drawContext, T option, int x, int y, int mouseX, int mouseY);

    default boolean mouseClicked(T option, double mouseX, double mouseY, int button) {
        return option.mouseClicked(mouseX, mouseY,button);
    }

    default boolean mouseReleased(T option, double mouseX, double mouseY, int button) {
        return option.mouseReleased(mouseX, mouseY,button);
    }

    default boolean mouseDragged(T option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return option.mouseDragged(mouseX, mouseY,button,deltaX,deltaY);
    }

    default void keyPressed(T option, int key, int scanCode, int modifiers) {
        option.keyPressed(key, scanCode,modifiers);
    }

    default void keyReleased(T option, int key, int scanCode, int modifiers) {
        option.keyReleased(key, scanCode,modifiers);
    }

    default void mouseScrolled(T option, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        option.mouseScrolled(mouseX, mouseY,horizontalAmount,verticalAmount);
    }

    default void init(T option) {
    }
}
