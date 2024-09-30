package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class ColorPickerButton {
    private final int width;
    private final int height;
    private int x;
    private int y;
    private boolean isPicking = false;

    public ColorPickerButton(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(DrawContext drawContext, int x, int y) {
        this.x = x;
        this.y = y;
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0, 0, 404);
        // Draw the button
        drawContext.fill(x + 2, y + 2, x + width - 2, y + height - 2, isPicking() ? Color.GREEN.getRGB() :  0xFFAAAAAA);
        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "Pick", x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
        drawContext.getMatrices().pop();
    }

    public int getHeight() {
        return height;
    }

    public boolean onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                isPicking = true;
                return true;
            }
        }
        return false;
    }

    public boolean isPicking() {
        return isPicking;
    }

    public void setPicking(boolean picking) {
        isPicking = picking;
    }
}