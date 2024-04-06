package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.Option;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.Validate;

import javax.xml.validation.Validator;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DoubleOption extends Option<Double> {
    public String name = "Empty";
    private boolean isDragging = false;
    private double minValue = 0.0,maxValue = 0.0;
    float step = 0.1f;
    public DoubleOption(String name,double minValue,double maxValue,float step,Supplier<Double> getter, Consumer<Double> setter) {
        super(getter, setter);
        this.name = name;
        this.value = get();
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.width = 30;
        this.height = 16;
        this.step = step;
        Validate.isTrue(this.step>0.0f,"Step cannot be less than or equal to 0 (zero)");
    }
    @Override
    public void render(DrawContext drawContext, int x, int y) {
        super.render(drawContext, x, y);

        value = get();

        this.width = 30;
        this.height = 16;
        // Draw the label
        TextRenderer textRenderer = mc.textRenderer;
        DrawHelper.scaleAndPosition(drawContext.getMatrices(),x,y,0.7f);
        String labelText = name + ": " + String.format("%.1f", value);
        int labelWidth = textRenderer.getWidth(labelText);
        this.width = Math.max(this.width,labelWidth);
        drawContext.drawTextWithShadow(textRenderer, labelText, x, y + 1, 0xFFFFFFFF);
        DrawHelper.stopScaling(drawContext.getMatrices());

        // Draw the slider
        drawSlider(drawContext, x, y + textRenderer.fontHeight + 1, width);

        // Draw the handle
        float handleWidth = 3;
        float handleHeight = 8;
        double handleX = x + (value - minValue) / (maxValue - minValue) * (width - handleWidth);
        double handleY = y + textRenderer.fontHeight + 1 + ((2 - handleHeight) / 2);

        DrawHelper.fillRoundedRect(drawContext, (int) handleX, (int) handleY, (int) (handleX + handleWidth), (int) (handleY + handleHeight), 0xFFFFFFFF);
    }

    private void drawSlider(DrawContext drawContext, int sliderX, int sliderY, int sliderWidth) {
        DrawHelper.fill(drawContext, sliderX, sliderY, sliderX + sliderWidth, sliderY + 2, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if(isMouseOver(mouseX, mouseY)) {
            step(mouseX);
            isDragging = true;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    private void step(double mouseX){
        double newValue = minValue + (float) (mouseX - x) / width * (maxValue - minValue);
        // Round the new value to the nearest step
        newValue = Math.round(newValue / step) * step;
        set(newValue);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY) && isDragging) {
            step(mouseX);
        }
        return super.mouseDragged(mouseX, mouseY, button);
    }
}
