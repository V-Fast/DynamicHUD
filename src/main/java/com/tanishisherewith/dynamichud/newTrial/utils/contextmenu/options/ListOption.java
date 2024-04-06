package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.Option;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListOption<T> extends Option<T> {
    private final List<T> values;
    private int currentIndex = 0;
    public String name = "Empty";

    public ListOption(String name, Supplier<T> getter, Consumer<T> setter, List<T> values) {
        super(getter, setter);
        this.name = name;
        this.values = values;
        this.value = getter.get();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).toString().equals(value)) {
                currentIndex = i;
                break;
            }
        }
    }

    @Override
    public void render(DrawContext drawContext, int x, int y) {
        super.render(drawContext, x, y);

        value = get();
        this.height = mc.textRenderer.fontHeight;
        this.width = mc.textRenderer.getWidth(name + ": " + value.toString()) + 1;

        int color = Color.WHITE.getRGB();
        drawContext.drawText(mc.textRenderer, Text.of(name + ": " + value.toString()), x, y, color, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (isMouseOver(mouseX, mouseY)) {
            if(button == 0) {
                currentIndex = (currentIndex + 1) % values.size();
                if(currentIndex > values.size() - 1){
                    currentIndex = 0;
                }
                value = values.get(currentIndex);
            }else if(button == 1){
                currentIndex = (currentIndex - 1) % values.size();
                if(currentIndex < 0){
                    currentIndex = values.size() - 1;
                }
                value = values.get(currentIndex);
            }
            set(value);
        }
        return true;
    }
}
