package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
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

        float[] hsv = Color.RGBtoHSB(initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue(), null);

        this.boxSize = boxSize;
        this.gradientBox.setHue(hsv[0]);
        this.gradientBox.setSaturation(hsv[1]);
        this.gradientBox.setValue(hsv[2]);
        this.gradientSlider.setHue(hsv[0]);

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
        // colorPickerButton.render(drawContext, x + 24 + boxSize, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 8);
        alphaSlider.render(drawContext, x + 10 + boxSize, y + client.textRenderer.fontHeight + gradientSlider.getHeight() + 10);

        if (colorPickerButton.isPicking() && GlobalConfig.get().showColorPickerPreview()) {
            int[] colors = ColorHelper.getMousePixelColor(mouseX, mouseY);
            if (colors != null) {
                int red = colors[0];
                int green = colors[1];
                int blue = colors[2];

                //Draw the preview box near the mouse pointer
                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(0, 0, 2500);
                drawContext.fill(mouseX + 10, mouseY, mouseX + 26, mouseY + 16, -1);
                drawContext.fill(mouseX + 11, mouseY + 1, mouseX + 25, mouseY + 15, (red << 16) | (green << 8) | blue | 0xFF000000);
                drawContext.getMatrices().pop();
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!display) {
            return false;
        }
        /*if (colorPickerButton.onClick(mouseX, mouseY, button)) {
            return true;
        } else*/
        if (gradientSlider.isMouseOver(mouseX, mouseY)) {
            gradientSlider.onClick(mouseX, mouseY, button);
            gradientBox.setHue(gradientSlider.getHue());
        } else if (gradientBox.isMouseOver(mouseX, mouseY)) {
            gradientBox.onClick(mouseX, mouseY, button);
        } /* else if (colorPickerButton.isPicking()) {
            int[] colors = ColorHelper.getMousePixelColor(mouseX,mouseY);
            if(colors != null) {
                float[] hsv = Color.RGBtoHSB(colors[0], colors[1], colors[2], null);
                gradientSlider.setHue(hsv[0]);
                gradientBox.setHue(hsv[0]);
                gradientBox.setSaturation(hsv[1]);
                gradientBox.setValue(hsv[2]);

                colorPickerButton.setPicking(false);
            } else {
                DynamicHUD.logger.error("Invalid RGB pixel color at mouse pointer");
            }
        }
        */
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

    public boolean shouldDisplay() {
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
