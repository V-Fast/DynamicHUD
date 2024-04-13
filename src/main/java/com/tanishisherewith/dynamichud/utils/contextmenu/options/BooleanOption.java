package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanOption extends Option<Boolean> {
    public String name = "Empty";

    public BooleanOption(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(getter, setter);
        this.name = name;
    }

    @Override
    public void render(DrawContext drawContext, int x, int y) {
        super.render(drawContext, x, y);

        value = get();
        int color = value ? Color.GREEN.getRGB() : Color.RED.getRGB();
        drawContext.drawText(mc.textRenderer, Text.of(name), x, y, color, false);
        this.height = mc.textRenderer.fontHeight;
        this.width = mc.textRenderer.getWidth(name) + 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (isMouseOver(mouseX, mouseY)) {
            value = !value;
            set(value);
        }
        return true;
    }
}
