package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption.ColorGradientPicker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorOption extends Option<Color> {
    public String name = "Empty";
    public boolean isVisible = false;
    public ContextMenu parentMenu = null;
    private ColorGradientPicker colorPicker = null;

    public ColorOption(String name, ContextMenu parentMenu, Supplier<Color> getter, Consumer<Color> setter) {
        super(getter, setter);
        this.name = name;
        this.parentMenu = parentMenu;
        colorPicker = new ColorGradientPicker(x + this.parentMenu.finalWidth, y - 10, get(), this::set, 50, 100);
    }

    @Override
    public void render(DrawContext drawContext, int x, int y) {
        super.render(drawContext, x, y);

        int color = isVisible ? Color.GREEN.getRGB() : Color.RED.getRGB();
        this.height = mc.textRenderer.fontHeight;
        this.width = mc.textRenderer.getWidth(name) + 8;
        drawContext.drawText(mc.textRenderer, Text.of(name), x, y, color, false);

        int shadowOpacity = Math.min(value.getAlpha(),90);
        DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                x + width - 4,
                y - 1,
                8,
                8,
                2,
                value.getRGB(),
                shadowOpacity,
                1,
                1);

        colorPicker.render(drawContext, this.x + parentMenu.finalWidth + 7, y - 10);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            isVisible = !isVisible;
            if (isVisible) {
                colorPicker.setPos(this.x + parentMenu.finalWidth + 7, y - 10);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        colorPicker.mouseDragged(mouseX, mouseY, button);
        return super.mouseDragged(mouseX, mouseY, button);
    }
}
