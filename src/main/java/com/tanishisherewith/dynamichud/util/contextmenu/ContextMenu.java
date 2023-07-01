package com.tanishisherewith.dynamichud.util.contextmenu;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ContextMenu {
    private static int optionY;
    private final MinecraftClient client; // The Minecraft client instance
    private final List<ContextMenuOption> options = new ArrayList<>(); // The list of options in the context menu
    private int width = 0; // The width of the context menu
    private int x; // The x position of the context menu
    private int y; // The y position of the context menu
    private final Widget selectedWidget; // The widget that this context menu is associated with
    private int backgroundColor = 0x90C0C0C0;// Semi-transparent light grey color
    private int padding = 5; // The amount of padding around the rectangle
    private int HeightFromWidget = 5; // The amount of padding around the rectangle
    private float scale = 0.0f;
    private int height = 0;


    /**
     * Constructs a ContextMenu object.
     *
     * @param client         The Minecraft client instance
     * @param x              The x position of the context menu
     * @param y              The y position of the context menu
     * @param selectedWidget The widget that this context menu is associated with
     */
    public ContextMenu(MinecraftClient client, int x, int y, Widget selectedWidget) {
        this.client = client;
        this.x = x;
        this.y = y;
        this.selectedWidget = selectedWidget;
    }

    public static int getOptionY() {
        return optionY;
    }

    /**
     * Sets the options to enable or disable based on values
     *
     * @param label  The label of the option
     * @param option Context Menu options
     */
    public void setOptions(String label, ContextMenuOption option) {
        if (selectedWidget instanceof ContextMenuOptionsProvider optionsProvider) {
            option.enabled = optionsProvider.isOptionEnabled(label);
        }
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
            setOptions(label, option);
        }
        options.add(option);
    }
    public void addDataTextOption(String label, Consumer<String > action) {
        DataInputOption option = new DataInputOption(label, action,x+client.textRenderer.getWidth(label)+25,15);
        if (selectedWidget != null) {
            setOptions(label, option);
        }
        options.add(option);
    }
    public void addDoubleTextOption(String label, Consumer<Double > action) {
        DoubleInputOption option = new DoubleInputOption(label, action,x+client.textRenderer.getWidth(label)+25,15);
        if (selectedWidget != null) {
            setOptions(label, option);
        }
        options.add(option);
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Returns whether the given point is within the bounds of this context menu.
     *
     * @param x - X position of the point.
     * @param y - Y position of the point.
     * @return true if the point is within the bounds of this context menu, false otherwise.
     */
    public boolean contains(double x, double y) {
        return x >= this.x - 3 && x <= this.x + width + 13 && y >= this.y + HeightFromWidget - 3 && y <= this.y + height + HeightFromWidget + 3;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Adds an option to the context menu that cycles through the values of an enum.
     * <p>
     * Usage Example:
     * Position currentPosition = Position.ABOVE;
     * <p>
     * ContextMenu contextMenu = new ContextMenu(client, x, y);
     * <p>
     * contextMenu.addEnumCycleOption("Position", Position.values(), () -> currentPosition, newPosition -> {
     * currentPosition = newPosition;
     * });
     *
     * @param labelPrefix The label to display for this option in the context menu
     * @param values      An array of enum values that specifies the possible values that this option can cycle through
     * @param getter      A Supplier that returns the current value of the enum
     * @param setter      A Consumer that sets the new value of the enum
     * @param <T>         The type of the enum
     */
    public <T extends Enum<T>> void addEnumCycleOption(String labelPrefix, T[] values, Supplier<T> getter, Consumer<T> setter) {
        ContextMenuOption option = new EnumCycleContextMenuOption<>(labelPrefix, values, getter, () -> {
            // Get the current value of the enum
            T currentValue = getter.get();

            // Find the index of the current value in the values array
            int index = -1;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == currentValue) {
                    index = i;
                    break;
                }
            }

            // Increment the index and wrap around if necessary
            index = (index + 1) % values.length;

            // Set the new value of the enum
            setter.accept(values[index]);
        });
        options.add(option);
    }


    public void tick() {
        // Update the scale
        float scaleSpeed = 0.1f;
        scale += scaleSpeed;
        if (scale > 1.0f) {
            scale = 1.0f;
        }
    }

    /**
     * Updates the position of this context menu to avoid overlapping with other widgets.
     */
    public void updatePosition() {
        // Check if the context menu is outside the bounds of the screen
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        if (x + width + 12 > screenWidth) {
            x = screenWidth - width - 12;
        }
        if (y + HeightFromWidget - 2 < 0) {
            y = HeightFromWidget + 2;
        }
        if (y + height + HeightFromWidget + 2 > screenHeight) {
            y = screenHeight - height - HeightFromWidget - 2;
        }
    }

    /**
     * Renders this context menu on screen.
     *
     * @param drawContext - MatrixStack used for rendering.
     */
    public void render(DrawContext drawContext) {
        tick();
        TextRenderer textRenderer = client.textRenderer;
        calculateSize(textRenderer);
        // Apply the scale
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(x + width / 2.0f + 5, y + height / 2.0f + HeightFromWidget, 300);
        drawContext.getMatrices().scale(scale, scale, 1.0f);
        drawContext.getMatrices().translate(-(x + width / 2.0f + 5), -(y + height / 2.0f + HeightFromWidget), 300);
        int x1 = x - 1;
        int y1 = y + HeightFromWidget - 2;
        int x2 = x + width + 8;
        int y2 = y + height + HeightFromWidget + 2;
        // Draw the background
        DrawHelper.drawCutRectangle(drawContext, x1, y1, x2, y2, 0, backgroundColor, 1);
        optionY = y + HeightFromWidget + 2;
        drawOptions(drawContext, textRenderer);
        if (selectedWidget != null)
            setPosition(selectedWidget.getX(), selectedWidget.getY() + textRenderer.fontHeight + 4);
        drawContext.getMatrices().pop();
        updatePosition();
    }

    private void calculateSize(TextRenderer textRenderer) {
        // Calculate the size of the context menu
        width = 0;
        height = 0;
        for (ContextMenuOption option : options) {
            width = Math.max(width, textRenderer.getWidth(option.label) + padding);
            height += textRenderer.fontHeight + 2;
        }
    }

    private void drawOptions(DrawContext drawContext, TextRenderer textRenderer) {
        int labelTextcolor;
        for (ContextMenuOption option : options) {
            if (option instanceof EnumCycleContextMenuOption enumOption) {
                enumOption.updateLabel();
                labelTextcolor=ColorHelper.ColorToInt(Color.WHITE);
            } else{
                labelTextcolor = option.enabled ? 0xFF00FF00 : 0xFFFF0000;
            }
            drawContext.drawText(textRenderer, option.label, x + 5, optionY, labelTextcolor, false);
            optionY += textRenderer.fontHeight + 2;
        }
    }


    /**
     * Sets position of this context menu.
     *
     * @param x - X position to set.
     * @param y - Y position to set.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setHeightFromWidget(int HeightFromWidget) {
        this.HeightFromWidget = HeightFromWidget;
    }

    public List<ContextMenuOption> getOptions() {
        return options;
    }

    /**
     * Handles mouse clicks on this context menu.
     *
     * @param mouseX - X position of mouse cursor.
     * @param mouseY - Y position of mouse cursor.
     * @param button - Mouse button that was clicked.
     * @return true if mouse click was handled by this context menu.
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        TextRenderer textRenderer = client.textRenderer;
        int optionY = y + HeightFromWidget + 2;
        for (ContextMenuOption option : options) {
            if (mouseX >= x && mouseX <= x + textRenderer.getWidth(option.label) + 10 && mouseY >= optionY && mouseY <= optionY + textRenderer.fontHeight + 2) {
                // Run the action of the selected option
                option.action.run();
                option.enabled = !option.enabled;
                return true;
            }
            optionY += textRenderer.fontHeight + 2;
        }
        return false;
    }


    private static class ContextMenuOption {
        final Runnable action; // The action to perform when the option is clicked
        String label; // The label of the option
        private boolean enabled = false; // Whether the option is enabled

        /**
         * Constructs a ContextMenuOption object.
         *
         * @param label  - Label of this option.
         * @param action - Action to perform when this option is clicked.
         */
        public ContextMenuOption(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }

    private static class EnumCycleContextMenuOption<T extends Enum<T>> extends ContextMenuOption {
        private final String labelPrefix;
        private final T[] values;
        private final Supplier<T> getter;

        public EnumCycleContextMenuOption(String labelPrefix, T[] values, Supplier<T> getter, Runnable action) {
            super(labelPrefix + getter.get(), action);
            this.labelPrefix = labelPrefix;
            this.values = values;
            this.getter = getter;
        }

        public void updateLabel() {
            label = labelPrefix + getter.get();
        }
    }
    public class DataInputOption extends ContextMenuOption {
        public DataInputOption(String label, Consumer<String> consumer,int x,int y) {
            super(label, () -> {
                // Open a new screen to allow the player to input data
                MinecraftClient.getInstance().setScreen(new DataInputScreen(consumer,x,y));
            });
        }
    }
    public class DoubleInputOption extends ContextMenuOption {
        public DoubleInputOption(String label, Consumer<Double> consumer,int x, int y) {
            super(label, () -> {
                // Open a new screen to allow the player to input data
                MinecraftClient.getInstance().setScreen(new DoubleInputScreen(consumer,x,y));
            });
        }
    }

}