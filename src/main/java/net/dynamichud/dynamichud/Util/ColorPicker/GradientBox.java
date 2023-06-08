package net.dynamichud.dynamichud.Util.ColorPicker;

import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.helpers.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class GradientBox {
    private int x;
    private int y;
    private final int size;
    private float hue = 0.0f;
    private float saturation = 1.0f;
    private float value = 1.0f;
    private boolean isDragging = false;
    private float alpha = 0.0f;
    private final float alphaSpeed = 0.05f;
    private final Widget selectedWidget;


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

    public void render(MatrixStack matrices) {
        DrawHelper.drawOutlinedBox(matrices, x - 2, y - 2, x + size + 2, y + size + 2, -1);

        // Draw the gradient
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                float saturation = (float) i / size;
                float value = 1.0f - (float) j / size;
                int color = Color.HSBtoRGB(hue, saturation, value);
                color = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
                DrawableHelper.fill(matrices, x + i, y + j, x + i + 1, y + j + 1, color);
            }
        }

        // Draw the handle
        float handleSize = 5;
        float handleX = x + saturation * size - handleSize / 2.0f;
        float handleY = y + (1.0f - value) * size - handleSize / 2.0f;

        DrawHelper.fillRoundedRect(matrices, (int) handleX, (int) handleY, (int) (handleX + handleSize), (int) (handleY + handleSize), -1);
        if (this.selectedWidget != null)
            setPosition(selectedWidget.getX() + 30, selectedWidget.getY() + MinecraftClient.getInstance().textRenderer.fontHeight + 4);
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
