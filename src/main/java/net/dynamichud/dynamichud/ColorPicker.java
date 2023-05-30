package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPicker {

    private final MinecraftClient client;
    int colorPickerWidth = 100;
    int colorPickerHeight = 10;
    private final int x;
    private final int y;
    private final Consumer<Integer> onColorSelected;

    public ColorPicker(MinecraftClient client, int x, int y, int initialColor, Consumer<Integer> onColorSelected) {
        this.client = client;
        this.x = x;
        this.y = y;
        this.onColorSelected = onColorSelected;
    }

    public void render(MatrixStack matrices) {
        // Draw the color picker
        int colorPickerX = x;
        int colorPickerY = y;

        // Draw the background
        DrawableHelper.fill(matrices, colorPickerX, colorPickerY, colorPickerX + colorPickerWidth, colorPickerY + colorPickerHeight, 0xFF000000);

        // Draw the colors
        int numColors = 100;
        int colorWidth = colorPickerWidth / numColors;
        int colorHeight = colorPickerHeight;
        for (int i = 0; i < numColors; i++) {
            float hue = (float) i / numColors;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            DrawableHelper.fill(matrices, colorPickerX + i * colorWidth, colorPickerY, colorPickerX + (i + 1) * colorWidth, colorPickerY + colorHeight, color);
        }
    }

    public boolean mouseClicked(double mouseX,double mouseY,int button){
        // Check if the mouse is over any of the colors
        int numColors = 100;
        int colorWidth = colorPickerWidth / numColors;
        for (int i = 0; i < numColors; i++) {
            if (mouseX >= x + i * colorWidth && mouseX <= x + (i + 1) * colorWidth && mouseY >= y && mouseY <= y + colorPickerHeight) {
                // Call the onColorSelected callback with the selected color
                float hue = (float) i / numColors;
                int selectedColor = Color.HSBtoRGB(hue, 1.0f, 1.0f);
                onColorSelected.accept(selectedColor);
                return true;
            }
        }

        return false;
    }


}
