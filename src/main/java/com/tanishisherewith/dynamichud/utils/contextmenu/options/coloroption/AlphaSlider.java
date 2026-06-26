package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.awt.*;

public class AlphaSlider {
    private final int width;
    private final int height;
    private int x;
    private int y;
    private boolean isDragging = false;
    private Color color;
    private int alphaHandleY = 0;
    private float alpha;

    public AlphaSlider(int x, int y, int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.x = x;
        this.y = y;
        updateAlphaFromColor(color);
    }

    public void render(GuiGraphics graphics, int x, int y) {
        this.x = x;
        this.y = y;

        DrawHelper.drawOutlinedBox(graphics, x - 2, y - 2, x + width + 2, y + height + 2, Color.WHITE.getRGB());
        DrawHelper.drawGradient(graphics, x, y, width, height, color.getRGB(), ColorHelper.changeAlpha(color, 0).getRGB(), DrawHelper.Direction.TOP_BOTTOM);
        graphics.fill(x - 2, y + alphaHandleY - 1, x + width + 2, y + alphaHandleY + 1, Color.WHITE.getRGB());
    }

    public Color getColor() {
        return ColorHelper.changeAlpha(color, (int) (alpha * 255f));
    }

    public void setColor(Color color) {
        this.color = color;
        updateAlphaFromColor(color);
    }

    private void updateAlphaFromColor(Color color) {
        this.alpha = color.getAlpha() / 255f;
        // Maps the 0.0 - 1.0 float range back into screen pixel offset
        this.alphaHandleY = Math.round((1.0f - this.alpha) * this.height);
    }

    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            this.isDragging = true;
            handleMouseMovement(mouseY);
        }
    }

    public void onDrag(double mouseX, double mouseY, int button) {
        if (this.isDragging) {
            handleMouseMovement(mouseY);
        }
    }

    public void onRelease(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isDragging = false;
        }
    }

    private void handleMouseMovement(double mouseY) {
        // Clamp mouse delta pos so the handle dooesnt jump out of the bar bounds
        this.alphaHandleY = Mth.clamp((int) mouseY - this.y, 0, this.height);
        this.alpha = 1.0f - (this.alphaHandleY / (float) this.height);
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}