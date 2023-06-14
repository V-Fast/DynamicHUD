package com.tanishisherewith.dynamichud.util.colorpicker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ColorPickerButton {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private boolean isPicking = false;

    public ColorPickerButton(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(DrawContext drawContext) {
        // Draw the button
        drawContext.fill( x, y, x + width, y + height, 0xFFAAAAAA);
        drawContext.drawCenteredTextWithShadow( MinecraftClient.getInstance().textRenderer, "Pick", x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
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