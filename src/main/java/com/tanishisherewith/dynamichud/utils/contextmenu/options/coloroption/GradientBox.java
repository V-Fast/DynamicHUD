package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class GradientBox {
    private final int size;
    private int x;
    private int y;
    private float hue = 0.0f;
    private float saturation = 1.0f;
    private float value = 1.0f;
    private boolean isDragging = false;


    public GradientBox(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void render(DrawContext drawContext, int x, int y) {
        setPosition(x, y);
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0, 0, 406);
        DrawHelper.drawOutlinedBox(drawContext, x - 2, y - 2, x + size + 2, y + size + 2, -1);

        // Draw the gradient
        com.tanishisherewith.dynamichud.helpers.DrawHelper.drawRoundedGradientRectangle(drawContext.getMatrices().peek().getPositionMatrix(), Color.BLACK, Color.BLACK, Color.getHSBColor(hue, 1.0f, 1.0f), Color.WHITE, x, y, size, size, 2);

        // Draw the handle
        float handleSize = 3;
        float handleX = x + 2 + saturation * size - handleSize / 2.0f;
        float handleY = y + 2 + (1.0f - value) * size - handleSize / 2.0f;

        DrawHelper.drawFilledCircle(drawContext.getMatrices().peek().getPositionMatrix(), handleX, handleY, 1, -1);
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
