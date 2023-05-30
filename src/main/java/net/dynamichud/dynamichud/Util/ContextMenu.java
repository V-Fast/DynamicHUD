package net.dynamichud.dynamichud.Util;

import net.dynamichud.dynamichud.Widget.TextWidget;
import net.dynamichud.dynamichud.helpers.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    private final MinecraftClient client; // The Minecraft client instance
    private int width = 0; // The width of the context menu
    private int x; // The x position of the context menu
    private int y; // The y position of the context menu
    private final List<ContextMenuOption> options = new ArrayList<>(); // The list of options in the context menu
    private TextWidget selectedWidget = null; // The widget that this context menu is associated with

    private int color=0xFFFFFFFF; // The color of the context menu

    /**
     * Constructs a ContextMenu object.
     *
     * @param client         The Minecraft client instance
     * @param x              The x position of the context menu
     * @param y              The y position of the context menu
     * @param selectedWidget The widget that this context menu is associated with
     */
    public ContextMenu(MinecraftClient client, int x, int y, TextWidget selectedWidget) {
        this.client = client;
        this.x = x;
        this.y = y;
        this.selectedWidget = selectedWidget;
    }

    /**
     * Adds an option to the context menu.
     *
     * @param label  The label of the option
     * @param action The action to perform when the option is clicked
     */
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

    /**
     * Renders this context menu on screen.
     *@param matrices - MatrixStack used for rendering.
     */
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
        DrawHelper.fillRoundedRect(matrices.peek().getPositionMatrix(), x, y - padding, x + width + padding-1, y + height + padding, cornerRadius, backgroundColor);
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

    /**
     * Sets position of this context menu.
     *@param x - X position to set.
     *@param y - Y position to set.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Handles mouse clicks on this context menu.
     *@param mouseX - X position of mouse cursor.
     *@param mouseY - Y position of mouse cursor.
     *@param button - Mouse button that was clicked.
     *@return true if mouse click was handled by this context menu.
     */
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
        private final String label; // The label of the option
        private final Runnable action; // The action to perform when the option is clicked
        private boolean enabled=false; // Whether the option is enabled

        /**
         * Constructs a ContextMenuOption object.
         *@param label - Label of this option.
         *@param action - Action to perform when this option is clicked.
         */
        public ContextMenuOption(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }
}