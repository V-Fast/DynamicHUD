package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListOption<T> extends Option<T> {
    private final List<T> values;
    public String name = "Empty";
    private int currentIndex = 0;

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
        this.renderer.init(this);
    }

    @Override
    public void render(DrawContext drawContext, int x, int y,int mouseX, int mouseY) {
        value = get();
        super.render(drawContext, x, y,mouseX,mouseY);

        // properties.getSkin().getRenderer(ListOption.class).render(drawContext,this,x,y,mouseX,mouseY);
/*
        this.height = mc.textRenderer.fontHeight + 1;
        this.width = mc.textRenderer.getWidth(name + ": " + value.toString()) + 1;

        drawContext.drawText(mc.textRenderer, Text.of(name + ": "), x, y, Color.WHITE.getRGB(), false);
        drawContext.drawText(mc.textRenderer, Text.of(value.toString()), x + mc.textRenderer.getWidth(name + ": ") + 1, y, Color.CYAN.getRGB(), false);
*/
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (button == 0) {
                currentIndex = (currentIndex + 1) % values.size();
                if (currentIndex > values.size() - 1) {
                    currentIndex = 0;
                }
                value = values.get(currentIndex);
            } else if (button == 1) {
                currentIndex = (currentIndex - 1) % values.size();
                if (currentIndex < 0) {
                    currentIndex = values.size() - 1;
                }
                value = values.get(currentIndex);
            }
            set(value);
        }
        return true;
    }

    public List<T> getValues() {
        return values;
    }
}
