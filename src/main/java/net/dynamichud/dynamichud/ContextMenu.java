package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    private final MinecraftClient client;
    private int width = 0;
    private int x;
    private int y;
    private final List<ContextMenuOption> options = new ArrayList<>();
    private TextWidget selectedWidget = null;

    private int color=0xFFFFFFFF;

    public ContextMenu(MinecraftClient client, int x, int y, TextWidget selectedWidget) {
        this.client = client;
        this.x = x;
        this.y = y;
        this.selectedWidget = selectedWidget;
    }

    public void addOption(String label, Runnable action) {
        ContextMenuOption option = new ContextMenuOption(label, action);
        if (selectedWidget != null) {
            switch (label) {
                case "Shadow" -> option.enabled = selectedWidget.hasShadow();
                case "Rainbow" -> option.enabled = selectedWidget.hasRainbow();
                case "Vertical Rainbow" -> option.enabled = selectedWidget.hasVerticalRainbow();
                case "Color" -> option.enabled = selectedWidget.isColorOptionEnabled();
            }
        }
        options.add(option);
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
        int backgroundColor = 0x40C0C0C0; // Semi-transparent light grey color
        int cornerRadius = 5; // The radius of the rounded corners
        int padding = 5; // The amount of padding around the rectangle
        DrawHelper.fillRoundedRect(matrices.peek().getPositionMatrix(), x, y - padding, x + width + padding-1, y + height + padding, cornerRadius, backgroundColor);        //DrawableHelper.fill(matrices, x, y, x + width, y + height, 0xFF000000);
        int buttonSize = 10;
        int buttonX = x + width - buttonSize;
        int buttonY = y;
        DrawableHelper.fill(matrices, buttonX, buttonY, buttonX + buttonSize, buttonY + buttonSize, backgroundColor);
        textRenderer.draw(matrices, "X", buttonX + 2, buttonY + 2, 0xFFFFFFFF);

        int optionY = y + 2;
        for (ContextMenuOption option : options) {
            int color = option.enabled ? 0xFF00FF00 : 0xFFFF0000;
            textRenderer.draw(matrices, option.label, x + 5, optionY, color);
            optionY += textRenderer.fontHeight + 2;
        }

    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public boolean mouseClicked(double mouseX,double mouseY,int button){
        // Check if the mouse is over any of the options
        int buttonSize = 10;
        int buttonX = x + width - buttonSize;
        int buttonY = y;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonSize && mouseY >= buttonY && mouseY <= buttonY + buttonSize) {
            // Close the context menu
            return false;
        }
        TextRenderer textRenderer = client.textRenderer;
        int optionY = y + 2;
        for (ContextMenuOption option : options) {
            if (mouseX >= x && mouseX <= x + textRenderer.getWidth(option.label) + 10 && mouseY >= optionY && mouseY <= optionY + textRenderer.fontHeight + 2) {
                // Run the action of the selected option
                option.action.run();

                option.enabled=!option.enabled;

                return true;
            }
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
