package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.Validate;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DoubleOption extends Option<Double> {
    public String name = "Empty";
    float step = 0.1f;
    private boolean isDragging = false;
    public double minValue = 0.0;
    public double maxValue = 0.0;
    ContextMenu parentMenu;

    public DoubleOption(String name, double minValue, double maxValue, float step, Supplier<Double> getter, Consumer<Double> setter, ContextMenu parentMenu) {
        super(getter, setter);
        this.name = name;
        this.value = get();
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.width = 30;
        this.height = 16;
        this.step = step;
        this.parentMenu = parentMenu;
        Validate.isTrue(this.step > 0.0f, "Step cannot be less than or equal to 0 (zero)");
        this.renderer.init(this);
    }

    @Override
    public void render(DrawContext drawContext, int x, int y,int mouseX, int mouseY) {
        value = get();
        super.render(drawContext, x, y,mouseX,mouseY);

        //properties.getSkin().getRenderer(DoubleOption.class).render(drawContext,this,x,y,mouseX,mouseY);

        /*
        this.width = 35;
        this.height = 16;

        // Draw the label
        TextRenderer textRenderer = mc.textRenderer;
        DrawHelper.scaleAndPosition(drawContext.getMatrices(), x, y, 0.7f);
        String labelText = name + ": " + String.format("%.1f", value);
        int labelWidth = textRenderer.getWidth(labelText);
        this.width = Math.max(this.width, labelWidth);
        drawContext.drawTextWithShadow(textRenderer, labelText, x, y + 1, 0xFFFFFFFF);
        DrawHelper.stopScaling(drawContext.getMatrices());

        float handleWidth = 3;
        float handleHeight = 8;
        double handleX = x + (value - minValue) / (maxValue - minValue) * (width - handleWidth);
        double handleY = y + textRenderer.fontHeight + 1 + ((2 - handleHeight) / 2);

        // Draw the slider
        drawSlider(drawContext, x, y + textRenderer.fontHeight + 1, width, handleX);

        // Draw the handle

        DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                (float) handleX,
                (float) handleY,
                handleWidth,
                handleHeight,
                1,
                0xFFFFFFFF,
                90,
                0.6f,
                0.6f);

         */
    }

    public void drawSlider(DrawContext drawContext, int sliderX, int sliderY, int sliderWidth, double handleX) {
        DrawHelper.drawRectangle(drawContext.getMatrices().peek().getPositionMatrix(), sliderX, sliderY, sliderWidth, 2, 0xFFFFFFFF);
        if (handleX - sliderX > 0) {
            DrawHelper.drawRectangle(drawContext.getMatrices().peek().getPositionMatrix(), (float) sliderX, (float) sliderY, (float) ((value - minValue) / (maxValue - minValue) * (width - 3)), 2, Color.ORANGE.getRGB());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
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

    public void step(double mouseX) {
        double newValue = minValue + (float) (mouseX - x) / width * (maxValue - minValue);
        // Round the new value to the nearest step
        newValue = Math.round(newValue / step) * step;
        set(newValue);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button,double deltaX, double deltaY) {
        if (isMouseOver(mouseX, mouseY) && isDragging) {
            step(mouseX);
        }
        return super.mouseDragged(mouseX, mouseY, button,deltaX,deltaY);
    }
}
