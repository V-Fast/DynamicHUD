package com.tanishisherewith.dynamichud.utils.contextmenu.options.coloroption;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.MouseColorQuery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorGradient {
    final Minecraft client = Minecraft.getInstance();
    private final Consumer<Color> onColorSelected; // The callback to call when a color is selected
    private final HueSlider gradientSlider;
    private final SaturationHueBox gradientBox;
    private final ColorPickerButton colorPickerButton;
    private final AlphaSlider alphaSlider;
    private final int boxSize;
    private int x, y;
    private boolean display = false;

    private Color hoveredColorPreview = null;

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
        this.hoveredColorPreview = null;
    }

    public void render(GuiGraphics graphics, int x1, int y1, int mouseX, int mouseY) {
        setPos(x1, y1);
        if (!display) {
            return;
        }
        gradientSlider.render(graphics, x, y + client.font.lineHeight + 4);
        gradientBox.render(graphics, x, y + client.font.lineHeight + gradientSlider.getHeight() + 10);
        colorPickerButton.render(graphics, x + 24 + boxSize, y + client.font.lineHeight + gradientSlider.getHeight() + 8);
        alphaSlider.render(graphics, x + 10 + boxSize, y + client.font.lineHeight + gradientSlider.getHeight() + 10);

        if (colorPickerButton.isPicking()) {
            if (GlobalConfig.get().showColorPickerPreview()) {
                // Request the pixel color under the exact cursor position
                MouseColorQuery.request(colors -> {
                    if (colors != null) {
                        this.hoveredColorPreview = new Color(colors[0], colors[1], colors[2]);
                    }
                });

                MouseColorQuery.processIfPending(); // process immediately

                renderPickerPreview(graphics, mouseX, mouseY);
            }
        } else {
            this.hoveredColorPreview = null;
        }
    }

    public void renderPickerPreview(GuiGraphics graphics, int mouseX, int mouseY) {
        if (hoveredColorPreview != null && colorPickerButton.isPicking() && GlobalConfig.get().showColorPickerPreview()) {
            // Temporarily pop all active clipping zones off the scissor stack
            // This will allow the preview to render over all screen scissors
            List<ScreenRectangle> poppedScissors = new ArrayList<>();
            while (graphics.scissorStack.peek() != null) {
                poppedScissors.add(graphics.scissorStack.peek());
                graphics.scissorStack.pop();
            }

            graphics.fill(mouseX + 6, mouseY + 6, mouseX + 22, mouseY + 22, 0xFFFFFFFF);
            graphics.fill(mouseX + 7, mouseY + 7, mouseX + 21, mouseY + 21, hoveredColorPreview.getRGB() | 0xFF000000);

            // Restore all clipping zones back to the stack in reverse order
            if(!poppedScissors.isEmpty()) {
                for (int i = poppedScissors.size() - 1; i >= 0; i--) {
                    graphics.scissorStack.push(poppedScissors.get(i));
                }
            }
        }
    }


    /**
     * Updates the internal states of the HSV sliders, alpha bar, and alerts the parent option on selection.
     */
    private void updateSelectedColor(Color color) {
        float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        gradientSlider.setHue(hsv[0]);
        gradientBox.setHue(hsv[0]);
        gradientBox.setSaturation(hsv[1]);
        gradientBox.setValue(hsv[2]);

        int currentAlpha = alphaSlider.getColor().getAlpha();
        alphaSlider.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), currentAlpha));
        onColorSelected.accept(alphaSlider.getColor());
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!display) {
            return false;
        }
        if (colorPickerButton.isPicking()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (hoveredColorPreview != null) {
                    updateSelectedColor(hoveredColorPreview);
                } else {
                    MouseColorQuery.processIfPending();
                    MouseColorQuery.request(colors -> {
                        if (colors != null) {
                            updateSelectedColor(new Color(colors[0], colors[1], colors[2]));
                        }
                    });
                    MouseColorQuery.processIfPending();
                }

                colorPickerButton.setPicking(false);
                return true;
            }
        }

        if (colorPickerButton.onClick(mouseX, mouseY, button)) {
            return true;
        }

        if (gradientSlider.isMouseOver(mouseX, mouseY)) {
            gradientSlider.onClick(mouseX, mouseY, button);
            gradientBox.setHue(gradientSlider.getHue());
        } else if (gradientBox.isMouseOver(mouseX, mouseY)) {
            gradientBox.onClick(mouseX, mouseY, button);
        }
        alphaSlider.setColor(new Color(gradientBox.getColor(), true));
        alphaSlider.onClick(mouseX, mouseY, button);
        onColorSelected.accept(alphaSlider.getColor());

        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        gradientSlider.onRelease(mouseX, mouseY, button);
        gradientBox.onRelease(mouseX, mouseY, button);
        alphaSlider.onRelease(mouseX, mouseY, button);
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!display || colorPickerButton.isPicking()) {
            return false;
        }
        gradientSlider.onDrag(mouseX, mouseY, button);
        gradientBox.setHue(gradientSlider.getHue());
        gradientBox.onDrag(mouseX, mouseY, button);
        alphaSlider.setColor(new Color(gradientBox.getColor(), true));
        alphaSlider.onDrag(mouseX, mouseY, button);
        onColorSelected.accept(alphaSlider.getColor());
        return true;
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
