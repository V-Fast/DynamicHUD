package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options.coloroption;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.GlAllocationUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class ColorGradientPicker {
    private final Consumer<Color> onColorSelected; // The callback to call when a color is selected
    private final GradientSlider gradientSlider;
    private final GradientBox gradientBox;
    private final ColorPickerButton colorPickerButton;
    private final AlphaSlider alphaSlider;
    private final int boxSize;
    final MinecraftClient client = MinecraftClient.getInstance();
    private int x, y;
    private boolean display = false;

    public ColorGradientPicker(int x, int y, Color initialColor, Consumer<Color> onColorSelected, int boxSize, int colors) {
        this.x = x;
        this.y = y;
        this.onColorSelected = onColorSelected;
        float[] hsv = Color.RGBtoHSB(initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue(), null);
        this.boxSize = boxSize;
        this.gradientSlider = new GradientSlider(x, y, colors, 10);
        this.gradientSlider.setHue(hsv[0]);

        this.gradientBox = new GradientBox(x, y + 20, boxSize);
        this.gradientBox.setHue(hsv[0]);
        this.gradientBox.setSaturation(hsv[1]);
        this.gradientBox.setValue(hsv[2]);

        this.alphaSlider = new AlphaSlider(x, y, 10, boxSize, initialColor);
        this.colorPickerButton = new ColorPickerButton(x + boxSize + 8, y + 20, 30, 18);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void display() {
        display = true;
    }

    public void close() {
        display = false;
    }

    public void render(DrawContext drawContext, int x1, int y1) {
        setPos(x1, y1);
        if (!display) {
            return;
        }
        gradientSlider.render(drawContext, x + 30, y + client.textRenderer.fontHeight + 4);
        gradientBox.render(drawContext, x + 30, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 10);
        colorPickerButton.render(drawContext, x + 54 + boxSize, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 8);
        alphaSlider.render(drawContext, x + 40 + boxSize, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 10);

        if (colorPickerButton.isPicking()) {
            // Draw the preview box near cursor

            //Translate cursor screen position to minecraft's scaled window
            double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

            Framebuffer framebuffer = client.getFramebuffer();
            int x = (int) (mouseX * framebuffer.textureWidth / client.getWindow().getScaledWidth());
            int y = (int) ((client.getWindow().getScaledHeight() - mouseY) * framebuffer.textureHeight / client.getWindow().getScaledHeight());

            //Read the pixel color at x,y pos to buffer
            ByteBuffer buffer = GlAllocationUtils.allocateByteBuffer(4);
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            int red = buffer.get(0) & 0xFF;
            int green = buffer.get(1) & 0xFF;
            int blue = buffer.get(2) & 0xFF;

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0,0,500);
            drawContext.fill((int) mouseX + 10, (int) mouseY, (int) mouseX + 26, (int) mouseY + 16, -1);
            drawContext.fill((int) mouseX + 11, (int) mouseY + 1, (int) mouseX + 25, (int) mouseY + 15, (red << 16) | (green << 8) | blue | 0xFF000000);
            drawContext.getMatrices().pop();
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!display) {
            return false;
        }
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
        alphaSlider.setColor(new Color(gradientBox.getColor()));
        alphaSlider.onClick(mouseX, mouseY, button);
        onColorSelected.accept(alphaSlider.getColor());
        return true;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        gradientSlider.onRelease(mouseX, mouseY, button);
        gradientBox.onRelease(mouseX, mouseY, button);
        alphaSlider.onRelease(mouseX, mouseY, button);
    }

    public void mouseDragged(double mouseX, double mouseY, int button) {
        if (!display) {
            return;
        }
        gradientSlider.onDrag(mouseX, mouseY, button);
        gradientBox.setHue(gradientSlider.getHue());
        gradientBox.onDrag(mouseX, mouseY, button);
        alphaSlider.setColor(new Color(gradientBox.getColor()));
        alphaSlider.onDrag(mouseX, mouseY, button);
        onColorSelected.accept(alphaSlider.getColor());
    }

}
