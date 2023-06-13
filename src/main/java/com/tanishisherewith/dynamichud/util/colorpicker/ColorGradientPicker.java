package com.tanishisherewith.dynamichud.util.colorpicker;

import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class ColorGradientPicker {
    private final MinecraftClient client; // The Minecraft client instance
    private final Consumer<Integer> onColorSelected; // The callback to call when a color is selected
    private final GradientSlider gradientSlider;
    private final GradientBox gradientBox;
    private final ColorPickerButton colorPickerButton;


    public ColorGradientPicker(MinecraftClient client, int x, int y, int initialColor, Consumer<Integer> onColorSelected, int BoxSize, int Colors, Widget selectedWidget) {
        this.client = client;
        this.onColorSelected = onColorSelected;
        float[] hsv = new float[3];
        Color.RGBtoHSB((initialColor >> 16) & 0xFF, (initialColor >> 8) & 0xFF, initialColor & 0xFF, hsv);

        // The initial color has an alpha component
        hsv[0] = 0.0f; // Set hue to default value
        hsv[1] = 1.0f; // Set saturation to default value
        hsv[2] = 1.0f; // Set value to default value
        this.gradientSlider = new GradientSlider(x, y, Colors, 10, selectedWidget);
        this.gradientSlider.setHue(hsv[0]);

        this.gradientBox = new GradientBox(x, y + 20, BoxSize, selectedWidget);
        this.gradientBox.setHue(hsv[0]);
        this.gradientBox.setSaturation(hsv[1]);
        this.gradientBox.setValue(hsv[2]);
        this.colorPickerButton = new ColorPickerButton(x + BoxSize + 8, y + 20, 35, 20);

    }

    public void tick() {
        gradientSlider.tick();
        gradientBox.tick();
    }

    public void render(MatrixStack matrices) {
        tick();
        gradientSlider.render(matrices);
        gradientBox.render(matrices);
        colorPickerButton.render(matrices);
        if (colorPickerButton.isPicking()) {
            // Draw the cursor
            double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

            Framebuffer framebuffer = client.getFramebuffer();
            int x = (int) (mouseX * framebuffer.textureWidth / client.getWindow().getScaledWidth());
            int y = (int) ((client.getWindow().getScaledHeight() - mouseY) * framebuffer.textureHeight / client.getWindow().getScaledHeight());

            ByteBuffer buffer = GlAllocationUtils.allocateByteBuffer(4);
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            int red = buffer.get(0) & 0xFF;
            int green = buffer.get(1) & 0xFF;
            int blue = buffer.get(2) & 0xFF;

            DrawableHelper.fill(matrices, (int) mouseX + 10, (int) mouseY, (int) mouseX + 26, (int) mouseY + 16, -1);
            DrawableHelper.fill(matrices, (int) mouseX + 11, (int) mouseY + 1, (int) mouseX + 25, (int) mouseY + 15, (red << 16) | (green << 8) | blue | 0xFF000000);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (colorPickerButton.onClick(mouseX, mouseY, button)) {
            return true;
        } else if (gradientSlider.isMouseOver(mouseX, mouseY)) {
            gradientSlider.onClick(mouseX, mouseY, button);
            gradientBox.setHue(gradientSlider.getHue());
        } else if (gradientBox.isMouseOver(mouseX, mouseY)) {
            gradientBox.onClick(mouseX, mouseY, button);
        } else if (colorPickerButton.isPicking()) {
            Framebuffer framebuffer = client.getFramebuffer();
            int x = (int) (mouseX * framebuffer.textureWidth / client.getWindow().getScaledWidth());
            int y = (int) ((client.getWindow().getScaledHeight() - mouseY) * framebuffer.textureHeight / client.getWindow().getScaledHeight());

            ByteBuffer buffer = GlAllocationUtils.allocateByteBuffer(4);
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            int red = buffer.get(0) & 0xFF;
            int green = buffer.get(1) & 0xFF;
            int blue = buffer.get(2) & 0xFF;

            float[] hsv = Color.RGBtoHSB(red, green, blue, null);
            gradientSlider.setHue(hsv[0]);
            gradientBox.setHue(hsv[0]);
            gradientBox.setSaturation(hsv[1]);
            gradientBox.setValue(hsv[2]);

            colorPickerButton.setPicking(false);
        }
        onColorSelected.accept(gradientBox.getColor());
        return true;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        gradientSlider.onRelease(mouseX, mouseY, button);
        gradientBox.onRelease(mouseX, mouseY, button);
    }

    public void mouseDragged(double mouseX, double mouseY, int button) {
        gradientSlider.onDrag(mouseX, mouseY, button);
        gradientBox.setHue(gradientSlider.getHue());
        gradientBox.onDrag(mouseX, mouseY, button);
        onColorSelected.accept(gradientBox.getColor());
    }

}
