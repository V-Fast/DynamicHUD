package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption.ColorGradient;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorOption extends Option<Color> {
    public boolean isVisible = false;
    private ContextMenu<?> parentMenu = null;
    private ColorGradient colorGradient = null;

    public ColorOption(Text name, Supplier<Color> getter, Consumer<Color> setter,ContextMenu<?> parentMenu) {
        super(name,getter, setter);
        this.parentMenu = parentMenu;
        this.colorGradient = new ColorGradient(x + this.parentMenu.getWidth(), y - 10, get(), this::set, 50, 100);
        this.renderer.init(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            isVisible = !isVisible;
            if (isVisible) {
                colorGradient.setPos(this.x + parentMenu.getWidth() + 7, y - 10);
                colorGradient.display();
            } else {
                colorGradient.close();
            }
        }
        colorGradient.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        colorGradient.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        colorGradient.mouseDragged(mouseX, mouseY, button);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public ColorGradient getColorGradient() {
        return colorGradient;
    }

    @Override
    public void onClose() {
        isVisible = false;
        colorGradient.close();
    }

    public ContextMenu<?> getParentMenu() {
        return parentMenu;
    }
}
