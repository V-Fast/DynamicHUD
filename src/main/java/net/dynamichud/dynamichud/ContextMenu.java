package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    private final MinecraftClient client;
    private int width = 0;
    private final int x;
    private final int y;
    private final List<ContextMenuOption> options = new ArrayList<>();
    private int color=0xFFFFFFFF;

    public ContextMenu(MinecraftClient client, int x, int y) {
        this.client = client;
        this.x = x;
        this.y = y;
    }

    public void addOption(String label, Runnable action) {
        options.add(new ContextMenuOption(label, action));
    }

    public void render(MatrixStack matrices) {
        TextRenderer textRenderer = client.textRenderer;
        // Calculate the size of the context menu
        width = 0;
        int height = 0;
        for (ContextMenuOption option : options) {
            width = Math.max(width, textRenderer.getWidth(option.label) + 10);
            height += textRenderer.fontHeight + 2;
        }

        // Draw the background
        DrawableHelper.fill(matrices, x, y, x + width, y + height, 0xFF000000);
        int buttonSize = 10;
        int buttonX = x + width - buttonSize;
        int buttonY = y;
        DrawableHelper.fill(matrices, buttonX, buttonY, buttonX + buttonSize, buttonY + buttonSize, 0xFF000000);
        textRenderer.draw(matrices, "X", buttonX + 2, buttonY + 2, 0xFFFFFFFF);

        int optionY = y + 2;
        for (ContextMenuOption option : options) {
            int color = option.enabled ? 0xFF00FF00 : 0xFFFF0000;
            textRenderer.draw(matrices, option.label, x + 5, optionY, color);
            optionY += textRenderer.fontHeight + 2;
        }

    }

    public boolean mouseClicked(double mouseX,double mouseY,int button){
        // Check if the mouse is over any of the options
        int buttonSize = 5;
        int buttonX = x + width - buttonSize;
        int buttonY = y;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonSize && mouseY >= buttonY && mouseY <= buttonY + buttonSize) {
            // Close the context menu
            return true;
        }
        TextRenderer textRenderer = client.textRenderer;
        int optionY = y + 2;
        for (ContextMenuOption option : options) {
            if (mouseX >= x && mouseX <= x + textRenderer.getWidth(option.label) + 10 && mouseY >= optionY && mouseY <= optionY + textRenderer.fontHeight + 2) {
                // Run the action of the selected option
                option.enabled=!option.enabled;
                color=Color.GREEN.getGreen();
                option.action.run();
                return true;
            }
            color=0xFFFFFFFF;
            optionY += textRenderer.fontHeight + 2;
        }
        return false;
    }


    private static class ContextMenuOption {
        private final String label;
        private final Runnable action;
        private boolean enabled=false;

        public ContextMenuOption(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }
}
