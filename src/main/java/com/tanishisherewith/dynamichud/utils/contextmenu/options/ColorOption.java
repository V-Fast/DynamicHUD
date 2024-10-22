package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption.ColorGradientPicker;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorOption extends Option<Color> {
    public String name = "Empty";
    public boolean isVisible = false;
    private ContextMenu parentMenu = null;
    private ColorGradientPicker colorPicker = null;

    public ColorOption(String name, ContextMenu parentMenu, Supplier<Color> getter, Consumer<Color> setter) {
        super(getter, setter);
        this.name = name;
        this.parentMenu = parentMenu;
        colorPicker = new ColorGradientPicker(x + this.parentMenu.getFinalWidth(), y - 10, get(), this::set, 50, 100);
        this.renderer.init(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            isVisible = !isVisible;
            if (isVisible) {
                colorPicker.setPos(this.x + parentMenu.getFinalWidth() + 7, y - 10);
                colorPicker.display();
            } else {
                colorPicker.close();
            }
        }
        colorPicker.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        colorPicker.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        colorPicker.mouseDragged(mouseX, mouseY, button);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public ColorGradientPicker getColorPicker() {
        return colorPicker;
    }

    @Override
    public void onClose() {
        isVisible = false;
        colorPicker.close();
    }

    public ContextMenu getParentMenu() {
        return parentMenu;
    }
}
