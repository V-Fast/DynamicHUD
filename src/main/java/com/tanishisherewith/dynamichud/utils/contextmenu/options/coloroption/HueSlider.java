package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class HueSlider {
    private final int width;
    private final int height;
    private int x;
    private int y;
    private float hue = 0.0f;
    private boolean isDragging = false;

    public HueSlider(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public void render(DrawContext drawContext, int x, int y) {
        setPosition(x, y);
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0, 0, 401);
        DrawHelper.drawOutlinedBox(drawContext, x - 2, y - 2, x + width + 2, y + height + 2, -1);

        // Draw the gradient
        for (int i = 0; i < width; i++) {
            float hue = (float) i / width;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            color = (color & 0x00FFFFFF) | (255 << 24);
            drawContext.fill(x + i, y, x + i + 1, y + height, color);
        }
        drawContext.draw();


        // Draw the handle
        float handleWidth = 3;
        float handleHeight = height + 4;
        float handleX = x + hue * width - handleWidth / 2.0f;
        float handleY = y - (handleHeight - height) / 2.0f;

        DrawHelper.drawRectangle(drawContext, handleX, handleY, handleWidth, handleHeight, -1);
        drawContext.getMatrices().pop();
    }

    public int getHeight() {
        return height;
    }

    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            float handleWidth = 3;
            float handleHeight = height + 4;
            float handleX = x + hue * width - handleWidth / 2.0f;
            float handleY = y - (handleHeight - height) / 2.0f;

            if (mouseX >= handleX && mouseX <= handleX + handleWidth && mouseY >= handleY && mouseY <= handleY + handleHeight) {
                this.isDragging = true;
            } else if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                this.hue = (float) (mouseX - x) / this.width;
                this.isDragging = true;
            }
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void onRelease(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
        }
    }

    public void onDrag(double mouseX, double mouseY, int button) {
        if (isDragging) {
            this.hue = (float) (mouseX - x) / this.width;
            this.hue = Math.clamp(this.hue, 0.0f, 1.0f);
        }
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }
}

