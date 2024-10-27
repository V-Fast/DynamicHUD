package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class ColorGradient {
    final MinecraftClient client = MinecraftClient.getInstance();
    private final Consumer<Color> onColorSelected; // The callback to call when a color is selected
    private final HueSlider gradientSlider;
    private final SaturationHueBox gradientBox;
    private final ColorPickerButton colorPickerButton;
    private final AlphaSlider alphaSlider;
    private final int boxSize;
    private int x, y;
    private boolean display = false;

    public ColorGradient(int x, int y, Color initialColor, Consumer<Color> onColorSelected, int boxSize, int colors) {
        this.x = x;
        this.y = y;
        this.onColorSelected = onColorSelected;
        this.gradientSlider = new HueSlider(x, y, colors, 10);
        this.gradientBox = new SaturationHueBox(x, y + 20, boxSize);
        this.alphaSlider = new AlphaSlider(x, y, 10, boxSize, initialColor);

        float[] hsv = new float[3];
        Color.RGBtoHSB(initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue(), hsv);

        this.boxSize = boxSize;
        this.gradientSlider.setHue(hsv[0]);
        this.gradientBox.setHue(hsv[0]);
        this.gradientBox.setSaturation(hsv[1]);
        this.gradientBox.setValue(hsv[2]);

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

    public void render(DrawContext drawContext, int x1, int y1, int mouseX, int mouseY) {
        setPos(x1, y1);
        if (!display) {
            return;
        }
        gradientSlider.render(drawContext, x, y + client.textRenderer.fontHeight + 4);
        gradientBox.render(drawContext, x, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 10);
        colorPickerButton.render(drawContext, x + 24 + boxSize, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 8);
        alphaSlider.render(drawContext, x + 10 + boxSize, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 10);

        if (colorPickerButton.isPicking() && GlobalConfig.get().showColorPickerPreview()) {
            // Draw the preview box near cursor
            Framebuffer framebuffer = client.getFramebuffer();
            if (framebuffer != null) {
                //Translate cursor screen position to minecraft's scaled windo
                int x = (int) (mouseX * framebuffer.textureWidth / client.getWindow().getScaledWidth());
                int y = (int) ((client.getWindow().getScaledHeight() - mouseY) * framebuffer.textureHeight / client.getWindow().getScaledHeight());

                try {
                    int bufferSize = framebuffer.textureWidth * framebuffer.textureHeight * 4;

                    ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.getColorAttachment());
                    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);

                    int index = (x + y * framebuffer.textureWidth) * 4;
                    if (index >= 0 && index + 3 < bufferSize) {
                        int blue = buffer.get(index) & 0xFF;
                        int green = buffer.get(index + 1) & 0xFF;
                        int red = buffer.get(index + 2) & 0xFF;

                        drawContext.getMatrices().push();
                        drawContext.getMatrices().translate(0, 0, 500);
                        drawContext.fill((int) mouseX + 10, (int) mouseY, (int) mouseX + 26, (int) mouseY + 16, -1);
                        drawContext.fill((int) mouseX + 11, (int) mouseY + 1, (int) mouseX + 25, (int) mouseY + 15, (red << 16) | (green << 8) | blue | 0xFF000000);
                        drawContext.getMatrices().pop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Framebuffer is null");
            }
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
            if (framebuffer != null) {
                int x = (int) (mouseX * framebuffer.textureWidth / client.getWindow().getScaledWidth());
                int y = (int) ((client.getWindow().getScaledHeight() - mouseY) * framebuffer.textureHeight / client.getWindow().getScaledHeight());

                try {
                    // Calculate the size of the buffer needed to store the texture data
                    int bufferSize = framebuffer.textureWidth * framebuffer.textureHeight * 4;

                    ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
                    // Bind the texture from the framebuffer
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.getColorAttachment());
                    // Read the texture data into the buffer
                    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);

                    // Calculate the index of the pixel in the buffer
                    int index = (x + y * framebuffer.textureWidth) * 4;

                    // Check if the index is within the bounds of the buffer
                    if (index >= 0 && index + 3 < bufferSize) {
                        int blue = buffer.get(index) & 0xFF;
                        int green = buffer.get(index + 1) & 0xFF;
                        int red = buffer.get(index + 2) & 0xFF;

                        float[] hsv = Color.RGBtoHSB(red, green, blue, null);
                        gradientSlider.setHue(hsv[0]);
                        gradientBox.setHue(hsv[0]);
                        gradientBox.setSaturation(hsv[1]);
                        gradientBox.setValue(hsv[2]);

                        colorPickerButton.setPicking(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Framebuffer is null");
            }
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

    public int getBoxSize() {
        return boxSize;
    }

    public boolean isDisplay() {
        return display;
    }

    public ColorPickerButton getColorPickerButton() {
        return colorPickerButton;
    }

    public AlphaSlider getAlphaSlider() {
        return alphaSlider;
    }

    public HueSlider getGradientSlider() {
        return gradientSlider;
    }

    public SaturationHueBox getGradientBox() {
        return gradientBox;
    }
}
