package net.dynamichud.dynamichud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPicker {

    private final MinecraftClient client;
    static int colorPickerWidth = 10;
    static int colorPickerHeight = 100;
    private static int x = 0;
    private static int y = 0;
    private static int hoveredColor = 0;

    private final Consumer<Integer> onColorSelected;

    public ColorPicker(MinecraftClient client, int x, int y, int initialColor, Consumer<Integer> onColorSelected) {
        this.client = client;
        ColorPicker.x = x;
        ColorPicker.y = y;
        this.onColorSelected = onColorSelected;
    }

    public void render(MatrixStack matrices) {
        // Draw the background
        int backgroundColor = 0xFF000000; // Black color
        int padding = 1;
        DrawableHelper.fill(matrices, x - padding, y - padding, x + colorPickerWidth + padding, y + colorPickerHeight + padding, backgroundColor);

        // Draw the colors
        int numColors = 100;
        int colorWidth = colorPickerWidth;
        int colorHeight = colorPickerHeight / numColors;
        for (int i = 0; i < numColors; i++) {
            float hue = (float) i / numColors;
            int topColor = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            int bottomColor = Color.HSBtoRGB(hue, 1.0f, 0.8f);
            fillGradient(matrices.peek().getPositionMatrix(), x, y + i * colorHeight, x + colorWidth, y + (i + 1) * colorHeight, topColor, bottomColor);
        }

        // Draw a square box at the top right of the screen using the hovered color
        if (hoveredColor != 0) {
            int boxSize = 25;
            int boxX = client.getWindow().getScaledWidth() - boxSize-2;
            int boxY = 2;
            DrawableHelper.fill(matrices, boxX - padding, boxY - padding, boxX + boxSize + padding, boxY + boxSize + padding, backgroundColor);
            DrawableHelper.fill(matrices, boxX, boxY, boxX + boxSize, boxY + boxSize, hoveredColor);
        }
    }
    public static void mouseMoved(double mouseX, double mouseY) {
        // Check if the mouse is over any of the colors
        int numColors = 100;
        int colorWidth = colorPickerWidth;
        int colorHeight = colorPickerHeight / numColors;
        for (int i = 0; i < numColors; i++) {
            if (mouseX >= x && mouseX <= x + colorWidth && mouseY >= y + i * colorHeight && mouseY <= y + (i + 1) * colorHeight) {
                // Update the hovered color
                float hue = (float) i / numColors;
                hoveredColor = Color.HSBtoRGB(hue, 1.0f, 1.0f);
                return;
            }
        }
        // Reset the hovered color if the mouse is not over any of the colors
        hoveredColor = 0;
    }



    private void fillGradient(Matrix4f matrix4f, int x1, int y1, int x2, int y2, int topColor, int bottomColor) {
        float topAlpha = (float)(topColor >> 24 & 255) / 255.0F;
        float topRed = (float)(topColor >> 16 & 255) / 255.0F;
        float topGreen = (float)(topColor >> 8 & 255) / 255.0F;
        float topBlue = (float)(topColor & 255) / 255.0F;
        float bottomAlpha = (float)(bottomColor >> 24 & 255) / 255.0F;
        float bottomRed = (float)(bottomColor >> 16 & 255) / 255.0F;
        float bottomGreen = (float)(bottomColor >> 8 & 255) / 255.0F;
        float bottomBlue = (float)(bottomColor & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f,x1,y2 ,0).color(bottomRed ,bottomGreen ,bottomBlue ,bottomAlpha).next();
        bufferBuilder.vertex(matrix4f,x2,y2 ,0).color(bottomRed ,bottomGreen ,bottomBlue ,bottomAlpha).next();
        bufferBuilder.vertex(matrix4f,x2,y1 ,0).color(topRed ,topGreen ,topBlue,topAlpha ).next();
        bufferBuilder.vertex(matrix4f,x1,y1 ,0).color(topRed ,topGreen ,topBlue,topAlpha ).next();
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    public boolean mouseClicked(double mouseX,double mouseY,int button){
        // Check if the mouse is over any of the colors
        int numColors = 100;
        int colorWidth = colorPickerWidth;
        int colorHeight = colorPickerHeight / numColors;
        for (int i = 0; i < numColors; i++) {
            if (mouseX >= x && mouseX <= x + colorWidth && mouseY >= y + i * colorHeight && mouseY <= y + (i + 1) * colorHeight) {
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
