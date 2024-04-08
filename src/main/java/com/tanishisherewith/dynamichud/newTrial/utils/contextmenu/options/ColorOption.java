package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.newTrial.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.Option;
import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options.coloroption.ColorGradientPicker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorOption extends Option<Color> {
    public String name  = "Empty";
    public boolean isVisible =  false;
    private ColorGradientPicker colorPicker = null;
    public ColorOption(String name, Supplier<Color> getter, Consumer<Color> setter) {
        super(getter, setter);
        this.name = name;
        colorPicker = new ColorGradientPicker(x + this.width + 50,y,value,color-> set(new Color(color)),50,100 );
    }

    @Override
    public void render(DrawContext drawContext, int x, int y) {
        super.render(drawContext, x, y);
        value = get();

        int color = isVisible ? Color.GREEN.getRGB() : Color.RED.getRGB();
        this.height = mc.textRenderer.fontHeight;
        this.width = mc.textRenderer.getWidth(name) + 12;
        drawContext.drawText(mc.textRenderer, Text.of(name),x,y, color,false);
        DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                x + width - 8,
                y,
                8,
                8,
                2,
                value.getRGB(),
                90,
                1,
                1 );

        colorPicker.render(drawContext,x + this.width + 50,y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            isVisible = !isVisible;
            if(isVisible)
            {
                colorPicker.setPos(x + this.width + 50,y);
                colorPicker.display();
            }else{
                colorPicker.close();
            }
        }
        colorPicker.mouseClicked(mouseX,mouseY,button);
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
