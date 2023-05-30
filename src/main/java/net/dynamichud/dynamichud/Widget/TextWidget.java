package net.dynamichud.dynamichud.Widget;

import net.dynamichud.dynamichud.helpers.ColorHelper;
import net.dynamichud.dynamichud.helpers.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

/**
 * This class represents a text widget that displays a specified text on the screen.
 */
public class TextWidget extends Widget {
    private String text; // The text to display
    private boolean shadow; // Whether to draw a shadow behind the text
    private boolean rainbow; // Whether to apply a rainbow effect to the text
    private boolean verticalRainbow; // Whether to apply a vertical rainbow effect to the text
    private int backgroundColor;
    private int color = Color.WHITE.getRGB(); // The color of the text
    private boolean colorOptionEnabled = false;
    protected static float rainbowSpeed = 15f; // The speed of the rainbow effect

    /**
     * Constructs a TextWidget object.
     *
     * @param client   The Minecraft client instance
     * @param text     The text to display
     * @param xPercent The x position of the widget as a percentage of the screen width
     * @param yPercent The y position of the widget as a percentage of the screen height
     */
    public TextWidget(MinecraftClient client, String text, float xPercent, float yPercent) {
        super(client);
        this.text = text;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
    }

    /**
     * Toggles whether the color option is enabled.
     */
    public void toggleColorOption() {
        colorOptionEnabled = !colorOptionEnabled;
    }

    /**
     * Sets whether the rainbow effect is enabled.
     *
     * @param rainbow True if the rainbow effect should be enabled, false otherwise
     */
    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    /**
     * Sets whether the shadow is enabled.
     *
     * @param shadow True if the shadow should be enabled, false otherwise
     */
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    /**
     * Returns the speed of the rainbow effect.
     *
     * @return The speed of the rainbow effect
     */
    public static float getRainbowSpeed() {
        return rainbowSpeed;
    }

    /**
     * Sets the speed of the rainbow effect.
     *
     * @param rainbowSpeed The new speed of the rainbow effect
     */
    public static void setRainbowSpeed(float rainbowSpeed) {
        TextWidget.rainbowSpeed = rainbowSpeed;
    }

    /**
     * Sets whether the vertical rainbow effect is enabled.
     *
     * @param verticalRainbow True if the vertical rainbow effect should be enabled, false otherwise
     */
    public void setVerticalRainbow(boolean verticalRainbow) {
        this.verticalRainbow = verticalRainbow;
    }

    /**
     * Returns whether the rainbow effect is enabled.
     *
     * @return True if the rainbow effect is enabled, false otherwise
     */
    public boolean hasRainbow() {
        return rainbow;
    }

    /**
     * Returns whether the shadow is enabled.
     *
     * @return True if the shadow is enabled, false otherwise
     */
    public boolean hasShadow() {
        return shadow;
    }

    /**
     * Returns whether the vertical rainbow effect is enabled.
     *
     * @return True if the vertical rainbow effect is enabled, false otherwise
     */
    public boolean hasVerticalRainbow() {
        return verticalRainbow;
    }

    /**
     * Returns the text displayed by this widget.
     *
     * @return The text displayed by this widget
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the color of the text.
     *
     * @param color The new color of the text
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Returns the color of the text.
     *
     * @return The color of the text
     */
    public int getColor() {
        return color;
    }

    /**
     * Returns whether or not color options are enabled for this widget.
     *
     *@return true if color options are enabled for this widget, false otherwise.
     */
    public boolean isColorOptionEnabled() {
        return colorOptionEnabled;
    }

    /**
     * Renders this widget on screen.
     *@param matrices - MatrixStack used for rendering.
     */
    @Override
    public void render(MatrixStack matrices) {
        int textWidth = client.textRenderer.getWidth(text);
        if (rainbow) {
            int x = getX();
            int y = getY();
            float hue = (System.currentTimeMillis() % 2000) / (rainbowSpeed*100f);
            for (int i = 0; i < text.length(); i++) {
                int color = ColorHelper.getColorFromHue(hue);
                String character = String.valueOf(text.charAt(i));
                int characterWidth = client.textRenderer.getWidth(character);
                if (shadow)
                    DrawHelper.drawTextWithShadow(matrices, client.textRenderer, character, x-textWidth/2, y - 4, color);
                else
                    DrawHelper.drawText(matrices, client.textRenderer, character, x-textWidth/2, y - 4, color);
                x += characterWidth;
                hue += verticalRainbow ? 0.05f : 0.1f;
                if (hue > 1) hue -= 1;
            }
        } else if (verticalRainbow){
            int color = ColorHelper.getColorFromHue((System.currentTimeMillis() % 2000) / (rainbowSpeed*100f));
            if (shadow)
                DrawHelper.drawTextWithShadow(matrices, client.textRenderer, text, getX() - textWidth / 2, getY() - 4, color);
            else
                DrawHelper.drawText(matrices, client.textRenderer, text, getX() - textWidth / 2, getY() - 4, color);
        }
        else if (shadow)
            DrawHelper.drawTextWithShadow(matrices, client.textRenderer, text, getX() - textWidth / 2, getY() - 4, color);
        else
            DrawHelper.drawText(matrices, client.textRenderer, text, getX() - textWidth / 2, getY() - 4, color);

    }
}
