package net.dynamichud.dynamichud.Widget.SliderWidget;

import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.helpers.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * This class represents a slider widget that allows the user to select a value within a specified range.
 */
public class SliderWidget {
    private final MinecraftClient client;
    private final int width; // The width of the widget
    private final int height; // The height of the widget
    private final String label; // The label displayed above the slider
    private final float minValue; // The minimum value of the slider
    private final float maxValue; // The maximum value of the slider
    private int x; // The x position of the widget
    private int y; // The y position of the widget
    private float value; // The current value of the slider
    private Widget selectedWidget = null;

    /**
     * Constructs a SliderWidget object.
     *
     * @param client         The Minecraft client instance
     * @param x              The x position of the widget
     * @param y              The y position of the widget
     * @param width          The width of the widget
     * @param height         The height of the widget
     * @param label          The label displayed above the slider
     * @param value          The initial value of the slider
     * @param minValue       The minimum value of the slider
     * @param maxValue       The maximum value of the slider
     * @param selectedWidget The widget which was selected to display this slider
     */
    public SliderWidget(MinecraftClient client, int x, int y, int width, int height, String label, float value, float minValue, float maxValue, Widget selectedWidget) {
        this.client = client;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.selectedWidget = selectedWidget;
    }

    /**
     * Renders the widget on the screen.
     *
     * @param matrices The matrix stack used for rendering
     */
    public void render(MatrixStack matrices) {
        // Draw the label
        TextRenderer textRenderer = client.textRenderer;
        textRenderer.draw(matrices, label + ": " + String.format("%.1f", value), x + 1, y - height + (textRenderer.fontHeight) / 3.5f, 0xFFFFFFFF);

        // Draw the slider
        int sliderWidth = width - 8;
        int sliderHeight = 2;
        int sliderX = x;
        int sliderY = y + height - sliderHeight - 8;

        DrawHelper.fillRoundedRect(matrices, sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 0xFFFFFFFF);

        // Draw the handle
        float handleWidth = 4;
        float handleHeight = 10;
        float handleX = sliderX + (value - minValue) / (maxValue - minValue) * (sliderWidth - handleWidth);
        float handleY = sliderY + (sliderHeight - handleHeight) / 2;

        DrawHelper.fillRoundedRect(matrices, (int) handleX, (int) handleY, (int) (handleX + handleWidth), (int) (handleY + handleHeight), 0xFFFFFFFF);

        if (selectedWidget != null)
            setPosition(selectedWidget.getX(), selectedWidget.getY() + textRenderer.fontHeight + 67);
    }

    /**
     * Sets the position of the widget.
     *
     * @param x The new x position of the widget
     * @param y The new y position of the widget
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Handles mouse clicks on the widget.
     *
     * @param mouseX The x position of the mouse cursor
     * @param mouseY The y position of the mouse cursor
     * @param button The mouse button that was clicked
     * @return True if the mouse click was handled by the widget, false otherwise
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if the mouse is over the slider
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            // Update the value based on the mouse position
            setValue(minValue + (float) (mouseX - x) / width * (maxValue - minValue) - 0.001f);
            return true;
        }
        return false;
    }

    /**
     * Handles mouse dragging on the widget.
     *
     * @param mouseX The current x position of the mouse cursor
     * @param mouseY The current y position of the mouse cursor
     * @param button The mouse button that is being dragged
     * @param deltaX The change in x position of the mouse cursor since the last call to this method
     * @param deltaY The change in y position of the mouse cursor since the last call to this method
     * @return True if the mouse dragging was handled by the widget, false otherwise
     */
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Check if the mouse is over the slider
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            // Update the value based on the mouse position
            setValue(minValue + (float) (mouseX - x) / width * (maxValue - minValue));
            return true;
        }
        return false;
    }

    /**
     * Returns the current value of the slider.
     *
     * @return The current value of the slider
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value of the slider.
     *
     * @param value The new value of the slider
     */
    public void setValue(float value) {
        this.value = Math.min(Math.max(value, minValue), maxValue);
    }
}
