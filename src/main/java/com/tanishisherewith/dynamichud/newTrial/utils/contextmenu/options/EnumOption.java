package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.Option;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumOption<E extends Enum<E>> extends Option<E> {
    private final E[] values;
    private int currentIndex = 0;
    public String name = "Empty";

    public EnumOption(String name, Supplier<E> getter, Consumer<E> setter, E[] values) {
        super(getter, setter);
        this.name = name;
        this.values = values;
        this.value = get();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                currentIndex = i;
                break;
            }
        }
    }

    @Override
    public void render(DrawContext drawContext, int x, int y) {
        super.render(drawContext, x, y);

        value = get();
        this.height = mc.textRenderer.fontHeight + 1;
        this.width = mc.textRenderer.getWidth(name + ": " + value.name()) + 1;

        int color = Color.WHITE.getRGB();
        drawContext.drawText(mc.textRenderer, Text.of(name + ": " + value.name()), x, y, color, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (isMouseOver(mouseX, mouseY)) {
            if(button == 0) {
                currentIndex = (currentIndex + 1) % values.length;
                if(currentIndex > values.length - 1){
                    currentIndex = 0;
                }
                value = values[currentIndex];
            }else if(button == 1){
                currentIndex = (currentIndex - 1) % values.length;
                if(currentIndex < 0){
                    currentIndex = values.length - 1;
                }
                value = values[currentIndex];
            }
            set(value);
        }
        return true;
    }
}