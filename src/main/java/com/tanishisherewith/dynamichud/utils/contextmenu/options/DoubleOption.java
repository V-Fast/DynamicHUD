package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.Validate;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DoubleOption extends Option<Double> {
    public double minValue = 0.0;
    public double maxValue = 0.0;
    public float step = 0.1f;
    ContextMenu parentMenu;
    private boolean isDragging = false;

    public DoubleOption(String name, double minValue, double maxValue, float step, Supplier<Double> getter, Consumer<Double> setter, ContextMenu parentMenu) {
        super(name,getter, setter);
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

    private void step(double mouseX) {
       this.step(mouseX,x);
    }

    public void step(double mouseX,double x) {
        double newValue = minValue + (float) (mouseX - x) / width * (maxValue - minValue);
        // Round the new value to the nearest step
        newValue = Math.round(newValue / step) * step;
        newValue = Math.clamp(newValue,minValue,maxValue);
        set(newValue);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isMouseOver(mouseX, mouseY) && isDragging) {
            step(mouseX);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public boolean isDragging() {
        return isDragging;
    }
}
