package com.tanishisherewith.dynamichud.util.colorpicker;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class GradientBox {
    private final int size;
    private final float alphaSpeed = 0.05f;
    private final Widget selectedWidget;
    private int x;
    private int y;
    private float hue = 0.0f;
    private float saturation = 1.0f;
    private float value = 1.0f;
    private boolean isDragging = false;
    private float alpha = 0.0f;


    public GradientBox(int x, int y, int size, Widget selectedWidget) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.selectedWidget = selectedWidget;
    }

    public void tick() {
        // Update the alpha
        alpha += alphaSpeed;
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }
    }

    public void render(DrawContext drawContext) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0,0,401);
        DrawHelper.drawOutlinedBox(drawContext, x - 2, y - 2, x + size + 2, y + size + 2, -1);

        // Draw the gradient
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                float saturation = (float) i / size;
                float value = 1.0f - (float) j / size;
                int color = Color.HSBtoRGB(hue, saturation, value);
                color = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
                drawContext.fill(x + i, y + j, x + i + 1, y + j + 1, color);
            }
        }

        // Draw the handle
        float handleSize = 3;
        float handleX = x + saturation * size - handleSize / 2.0f;
        float handleY = y + (1.0f - value) * size - handleSize / 2.0f;

        DrawHelper.fillRoundedRect(drawContext, (int) handleX, (int) handleY, (int) (handleX + handleSize), (int) (handleY + handleSize), -1);
        if (this.selectedWidget != null)
            setPosition(selectedWidget.getX() + 30, selectedWidget.getY() + MinecraftClient.getInstance().textRenderer.fontHeight + 4);
        drawContext.getMatrices().pop();
    }

    /**
     * Sets position.
     *
     * @param x - X position to set.
     * @param y - Y position to set.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            float handleSize = 5;
            float handleX = x + saturation * size - handleSize / 2.0f;
            float handleY = y + (1.0f - value) * size - handleSize / 2.0f;

            if (mouseX >= handleX && mouseX <= handleX + handleSize && mouseY >= handleY && mouseY <= handleY + handleSize) {
                this.isDragging = true;
            } else if (mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
                saturation = (float) (mouseX - x) / size;
                value = 1.0f - (float) (mouseY - y) / size;
                this.isDragging = true;
            }
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size;
    }

    public void onRelease(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
        }
    }

    public void onDrag(double mouseX, double mouseY, int button) {
        if (isDragging) {
            saturation = (float) (mouseX - x) / size;
            saturation = Math.max(0, saturation);
            saturation = Math.min(1, saturation);

            value = 1.0f - (float) (mouseY - y) / size;
            value = Math.max(0, value);
            value = Math.min(1, value);
        }
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getColor() {
        return Color.HSBtoRGB(hue, saturation, value);
    }
}
