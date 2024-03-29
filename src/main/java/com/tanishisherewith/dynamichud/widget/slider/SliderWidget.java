package com.tanishisherewith.dynamichud.widget.slider;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

/**
 * This class represents a slider widget that allows the user to select a value within a specified range.
 */
public class SliderWidget {
    private final MinecraftClient client;
    private final int width; // The width of the widget
    private final String label; // The label displayed above the slider
    private final float minValue; // The minimum value of the slider
    private final float maxValue; // The maximum value of the slider
    private final int height; // The height of the widget
    private int x; // The x position of the widget
    private int y; // The y position of the widget
    private float value; // The current value of the slider
    private Widget selectedWidget = null;
    private Consumer<Float> getValue;
    private float progress = 0.0f;
    private float progressSpeed = 0.1f;
    private float textProgress = 0.0f;
    private float textProgressSpeed = 0.05f;
    private boolean MouseClicked = false;

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
    public SliderWidget(MinecraftClient client, int x, int y, int width, int height, String label, float value, float minValue, float maxValue, Consumer<Float> getValue, Widget selectedWidget) {
        this.client = client;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.getValue=getValue;
        this.selectedWidget = selectedWidget;
    }

    public void tick() {
        // Update the progress
        progress += progressSpeed;
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        // Update the text progress
        textProgress += textProgressSpeed;
        if (textProgress > 1.0f) {
            textProgress = 1.0f;
        }
    }

    /**
     * Updates the position of this Slider to avoid getting out of the screen.
     */
    public void updatePosition() {
        // Check if the Slider is outside the bounds of the screen
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        if (x + width > screenWidth) {
            x = screenWidth - width + 1;
        }
        if (y + 2 < 0) {
            y += 2;
        }
        if (y + height + 2 > screenHeight) {
            y = screenHeight - height - 2;
        }
    }

    /**
     * Renders the widget on the screen.
     *
     * @param drawContext The matrix stack used for rendering
     */
    public void render(DrawContext drawContext) {
        tick();
        // Draw the label
        TextRenderer textRenderer = client.textRenderer;
        String labelText = label + ": " + String.format("%.1f", value);
        int labelWidth = textRenderer.getWidth(labelText);
        int labelX = (int) (x + (width - labelWidth) / 2.0f * textProgress) - 1;
        int labelY = y + height - textRenderer.fontHeight - 6;
        drawContext.drawTextWithShadow(textRenderer, labelText, labelX, labelY, 0xFFFFFFFF);

        // Draw the slider
        int sliderWidth = width - 8;
        int sliderHeight = 2;
        int sliderX = x;
        int sliderY = y + height - sliderHeight;

        drawSlider(drawContext, sliderX, sliderY, sliderWidth, sliderHeight);

        // Draw the handle
        float handleWidth = 4;
        float handleHeight = 10;
        float handleX = sliderX + (value - minValue) / (maxValue - minValue) * (sliderWidth - handleWidth);
        float handleY = sliderY + ((sliderHeight - handleHeight) / 2);

        if (progress >= 1.0f) {
            DrawHelper.fillRoundedRect(drawContext, (int) handleX, (int) handleY, (int) (handleX + handleWidth), (int) (handleY + handleHeight), 0xFFFFFFFF);
        }

        if (selectedWidget != null)
            setPosition(selectedWidget.getX(), selectedWidget.getY() + textRenderer.fontHeight + 67);
        updatePosition();
    }

    private void drawSlider(DrawContext drawContext, int sliderX, int sliderY, int sliderWidth, int sliderHeight) {
        int visibleSliderWidth = (int) (sliderWidth * progress);
        DrawHelper.fill(drawContext, sliderX, sliderY, sliderX + visibleSliderWidth, sliderY + sliderHeight, 0xFFFFFFFF);
    }

    /**
     * Returns whether the given point is within the bounds of this widget.
     *
     * @param x - X position of the point.
     * @param y - Y position of the point.
     * @return true if the point is within the bounds of this context menu, false otherwise.
     */
    public boolean contains(double x, double y) {
        return x >= this.x + 2 && x <= this.x - 2 + width && y >= this.y + 2 && y <= this.y - 2 + height;
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
            MouseClicked = !MouseClicked;
            setValue(minValue + (float) (mouseX - x) / width * (maxValue - minValue) - 0.001f);
            getValue.accept(value);
            return true;
        }
        MouseClicked = false;
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
        if (mouseX >= x && mouseX <= x + width && MouseClicked) {
            // Update the value based on the mouse position
            setValue(minValue + (float) (mouseX - x) / width * (maxValue - minValue));
            getValue.accept(value);
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
