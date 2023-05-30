package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class SliderWidget {
    private final MinecraftClient client;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String label;
    private float value;
    private final float minValue;
    private final float maxValue;

    public SliderWidget(MinecraftClient client, int x, int y, int width, int height, String label, float value, float minValue, float maxValue) {
        this.client = client;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void render(MatrixStack matrices) {
        // Draw the background
        DrawableHelper.fill(matrices, x, y, x + width, y + height, 0xFF000000);

        // Draw the label
        TextRenderer textRenderer = client.textRenderer;
        textRenderer.draw(matrices, label + ": " + String.format("%.1f", value), x + 5, y + (height - textRenderer.fontHeight) / 2, 0xFFFFFFFF);

        // Draw the slider
        int sliderWidth = width - 10;
        int sliderHeight = 2;
        int sliderX = x + 5;
        int sliderY = y + height - sliderHeight - 5;
        DrawableHelper.fill(matrices, sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 0xFFFFFFFF);

        // Draw the handle
        float handleWidth = 4;
        float handleHeight = 10;
        float handleX = sliderX + (value - minValue) / (maxValue - minValue) * (sliderWidth - handleWidth);
        float handleY = sliderY + (sliderHeight - handleHeight) / 2;
        DrawableHelper.fill(matrices, (int)handleX, (int)handleY, (int)(handleX + handleWidth), (int)(handleY + handleHeight), 0xFFFFFFFF);
    }

    public boolean mouseClicked(double mouseX,double mouseY,int button){
        // Check if the mouse is over the slider
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            // Update the value based on the mouse position
            setValue(minValue + (float)(mouseX - x) / width * (maxValue - minValue)*1.0001f);
            return true;
        }
        return false;
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        // Check if the mouse is over the slider
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            // Update the value based on the mouse position
            setValue(minValue + (float)(mouseX - x) / width * (maxValue - minValue));
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.min(Math.max(value, minValue), maxValue);
    }
}


