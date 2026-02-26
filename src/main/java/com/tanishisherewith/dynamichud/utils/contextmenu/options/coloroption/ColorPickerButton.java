package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

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

    public void render(GuiGraphics graphics, int x, int y) {
        this.x = x;
        this.y = y;
        // Draw the button
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, isPicking() ? Color.GREEN.getRGB() : 0xFFAAAAAA);
        graphics.drawCenteredString(Minecraft.getInstance().font, "Pick", x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
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