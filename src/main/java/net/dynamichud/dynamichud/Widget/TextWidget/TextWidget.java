package net.dynamichud.dynamichud.Widget.TextWidget;

import net.dynamichud.dynamichud.Util.ContextMenuOptionsProvider;
import net.dynamichud.dynamichud.Widget.TextGenerator;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.Widget.WidgetBox;
import net.dynamichud.dynamichud.helpers.ColorHelper;
import net.dynamichud.dynamichud.helpers.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;

import java.util.function.Supplier;

/**
 * This class represents a text widget that displays a specified text on the screen.
 */
public class TextWidget extends Widget implements ContextMenuOptionsProvider {
    protected static float rainbowSpeed = 15f; // The speed of the rainbow effect
    private Supplier<String> textSupplier; // The supplier that provides the text to display
    private TextGenerator textGenerator;
    private boolean shadow; // Whether to draw a shadow behind the text
    private boolean rainbow; // Whether to apply a rainbow effect to the text
    private boolean verticalRainbow; // Whether to apply a vertical rainbow effect to the text
    private int color; // The color of the text
    private boolean colorOptionEnabled = false;


    /**
     * Constructs a TextWidget object.
     *
     * @param client        The Minecraft client instance
     * @param textGenerator The text to display
     * @param xPercent      The x position of the widget as a percentage of the screen width
     * @param yPercent      The y position of the widget as a percentage of the screen height
     */
    public TextWidget(MinecraftClient client, TextGenerator textGenerator, float xPercent, float yPercent, boolean Shadow, boolean Rainbow, boolean VerticalRainbow, int color, boolean enabled) {
        super(client);
        this.textGenerator = textGenerator;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.shadow = Shadow;
        this.rainbow = Rainbow;
        this.verticalRainbow = VerticalRainbow;
        this.color = color;
        this.enabled = enabled;
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
    public float getRainbowSpeed() {
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
        return textGenerator.generateText();
    }

    public void setTextGenerator(TextGenerator textGenerator) {
        this.textGenerator = textGenerator;
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
     * Sets the color of the text.
     *
     * @param color The new color of the text
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Returns whether color options are enabled for this widget.
     *
     * @return true if color options are enabled for this widget, false otherwise.
     */
    public boolean isColorOptionEnabled() {
        return colorOptionEnabled;
    }

    @Override
    public WidgetBox getWidgetBox() {
        TextRenderer textRenderer = client.textRenderer;
        int width = textRenderer.getWidth(getText());
        int height = textRenderer.fontHeight;
        return new WidgetBox(width, height);
    }


    /**
     * Renders this widget on screen.
     *
     * @param matrices - MatrixStack used for rendering.
     */
    @Override
    public void render(MatrixStack matrices) {
        int textWidth = client.textRenderer.getWidth(getText());
        int x = getX();
        int y = getY();
        if (rainbow) {
            float hue = (System.currentTimeMillis() % 2000) / (rainbowSpeed * 100f);
            for (int i = 0; i < getText().length(); i++) {
                int color = ColorHelper.getColorFromHue(hue);
                String character = String.valueOf(getText().charAt(i));
                int characterWidth = client.textRenderer.getWidth(character);
                drawText(matrices, character, x - textWidth / 2, y - 4, color);
                x += characterWidth;
                hue += verticalRainbow ? 0.05f : 0.1f;
                if (hue > 1) hue -= 1;
            }
        } else {
            int color = verticalRainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % 2000) / (rainbowSpeed * 100f)) : this.color;
            drawText(matrices, getText(), getX() - textWidth / 2, getY() - 4, color);
        }
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putString("class", getClass().getName());
        tag.putString("text", getText());
        tag.putFloat("xPercent", xPercent);
        tag.putFloat("yPercent", yPercent);
        tag.putBoolean("Rainbow", hasRainbow());
        tag.putBoolean("Shadow", hasShadow());
        tag.putBoolean("VerticalRainbow", hasVerticalRainbow());
        tag.putBoolean("Enabled", this.enabled);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        xPercent = tag.getFloat("xPercent");
        yPercent = tag.getFloat("yPercent");
        shadow = tag.getBoolean("shadow");
        rainbow = tag.getBoolean("rainbow");
        verticalRainbow = tag.getBoolean("verticalRainbow");
        color = tag.getInt("color");
        enabled = tag.getBoolean("Enabled");
    }

    private void drawText(MatrixStack matrices, String text, int x, int y, int color) {
        if (shadow)
            DrawHelper.drawTextWithShadow(matrices, client.textRenderer, text, x, y, color);
        else
            DrawHelper.drawText(matrices, client.textRenderer, text, x, y, color);
    }

    @Override
    public boolean isOptionEnabled(String label) {
        return switch (label) {
            case "Shadow" -> hasShadow();
            case "Rainbow" -> hasRainbow();
            case "VerticalRainbow" -> hasVerticalRainbow();
            case "Color" -> isColorOptionEnabled();
            default -> false;
        };
    }
}
