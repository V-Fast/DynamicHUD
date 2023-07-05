package com.tanishisherewith.dynamichud.widget.text;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.util.contextmenu.ContextMenuOptionsProvider;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import com.tanishisherewith.dynamichud.interfaces.TextGenerator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

/**
 * This class represents a text widget that displays a specified text on the screen.
 */
public class TextWidget extends Widget implements ContextMenuOptionsProvider {
    protected static float rainbowSpeed = 15f; // The speed of the rainbow effect
    protected String text;
    protected TextGenerator dataText;
    protected boolean shadow; // Whether to draw a shadow behind the text
    protected boolean rainbow; // Whether to apply a rainbow effect to the text
    protected boolean verticalRainbow; // Whether to apply a vertical rainbow effect to the text
    protected int Textcolor; // The color of the text
    protected int Datacolor; // The color of the Data
    protected boolean TextcolorOptionEnabled = false;
    protected boolean DatacolorOptionEnabled = false;


    /**
     * Constructs a TextWidget object.
     *
     * @param client   The Minecraft client instance
     * @param text     The text to display
     * @param xPercent The x position of the widget as a percentage of the screen width
     * @param yPercent The y position of the widget as a percentage of the screen height
     */
    public TextWidget(MinecraftClient client, String text, TextGenerator dataText, float xPercent, float yPercent, boolean Shadow, boolean Rainbow, boolean VerticalRainbow, int Textcolor, int Datacolor, boolean enabled) {
        super(client,text);
        this.text = text;
        this.dataText = dataText;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.shadow = Shadow;
        this.rainbow = Rainbow;
        this.verticalRainbow = VerticalRainbow;
        this.Textcolor = Textcolor;
        this.Datacolor = Datacolor;
        this.enabled = enabled;
    }

    /**
     * Toggles whether the Data color option is enabled.
     */
    public void toggleTextColorOption() {
        TextcolorOptionEnabled = !TextcolorOptionEnabled;
    }

    /**
     * Toggles whether the Text color option is enabled.
     */
    public void toggleDataColorOption() {
        DatacolorOptionEnabled = !DatacolorOptionEnabled;
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
        return text;
    }

    /**
     * Returns the text displayed by this widget.
     *
     * @return The text displayed by this widget
     */
    public String getDataText() {
        return dataText.generateText();
    }


    public void setDataTextGenerator(TextGenerator textGenerator) {
        this.dataText = textGenerator;
    }

    /**
     * Returns the color of the text.
     *
     * @return The color of the text
     */
    public int getTextcolor() {
        return Textcolor;
    }

    /**
     * Returns the color of the Data.
     *
     * @return The color of the Data
     */
    public int getDatacolor() {
        return Datacolor;
    }

    @Override
    public void setTextGeneratorFromLabel() {
        TextGenerator textGenerator = textGenerators.get(getText());
        if (textGenerator != null) {
            setDataTextGenerator(textGenerator);
        }
    }

    /**
     * Sets the color of the text.
     *
     * @param color The new color of the text
     */
    public void setTextColor(int color) {
        this.Textcolor = color;
    }

    /**
     * Sets the color of the text.
     *
     * @param color The new color of the text
     */
    public void setDataColor(int color) {
        this.Datacolor = color;
    }


    /**
     * Returns whether color options are enabled for this widget.
     *
     * @return true if color options are enabled for this widget, false otherwise.
     */
    public boolean isTextcolorOptionEnabled() {
        return TextcolorOptionEnabled;
    }

    /**
     * Returns whether color options are enabled for this widget.
     *
     * @return true if color options are enabled for this widget, false otherwise.
     */
    public boolean isDatacolorOptionEnabled() {
        return DatacolorOptionEnabled;
    }

    @Override
    public WidgetBox getWidgetBox() {
        TextRenderer textRenderer = client.textRenderer;
        int x1 = getX(); //- client.textRenderer.getWidth(textWidget.getText());
        int x2 = getX() + textRenderer.getWidth(getDataText()) + textRenderer.getWidth(getText()) + textRenderer.getWidth(" ");
        int y1 = getY() - textRenderer.fontHeight / 2;
        int y2 = getY() + textRenderer.fontHeight / 2;
        return new WidgetBox(x1, y1, x2, y2);
    }


    /**
     * Renders this widget on screen.
     *
     */
    @Override
    public void render(DrawContext drawContext) {
        int x = getX();
        int y = getY();
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0,0,300);
        String CombinedText = getText() + getDataText();
        if (rainbow) {
            float hue = (System.currentTimeMillis() % 10000) / (rainbowSpeed * 400f);
            for (int i = 0; i < CombinedText.length(); i++) {
                int color = ColorHelper.getColorFromHue(hue);
                String character = String.valueOf(CombinedText.charAt(i));
                int characterWidth = client.textRenderer.getWidth(character);
                drawText(drawContext, character, x + 2, y - 4, color);
                x += characterWidth;
                hue += verticalRainbow ? 0.05f : 0.1f;
                if (hue >= 1) hue -= 1;
            }
        } else {
            int Textcolour = verticalRainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % 10000) / (rainbowSpeed * 400f)) : this.Textcolor;
            int Datacolour = verticalRainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % 10000) / (rainbowSpeed * 400f)) : this.Datacolor;
            drawText(drawContext, getText(), getX() + 2, getY() - 4, Textcolour);
            drawText(drawContext, getDataText(), getX() + client.textRenderer.getWidth(getText()) + 2, getY() - 4, Datacolour);
        }
        drawContext.getMatrices().pop();
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putBoolean("Rainbow", hasRainbow());
        tag.putBoolean("Shadow", hasShadow());
        tag.putBoolean("VerticalRainbow", hasVerticalRainbow());
        tag.putInt("TextColor", Textcolor);
        tag.putInt("DataColor", Datacolor);
        tag.putString("Text", text);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        shadow = tag.getBoolean("shadow");
        rainbow = tag.getBoolean("rainbow");
        verticalRainbow = tag.getBoolean("verticalRainbow");
        Textcolor = tag.getInt("TextColor");
        Datacolor = tag.getInt("DataColor");
        text = tag.getString("Text");
    }

    private void drawText(DrawContext drawContext, String text, int x, int y, int color) {
            DrawHelper.drawText(drawContext,client.textRenderer, text, x, y, color,shadow);
    }

    @Override
    public boolean isOptionEnabled(String label) {
        return switch (label) {
            case "Shadow" -> hasShadow();
            case "Rainbow" -> hasRainbow();
            case "Vertical Rainbow" -> hasVerticalRainbow();
            case "TextColor" -> isTextcolorOptionEnabled();
            case "DataColor" -> isDatacolorOptionEnabled();
            default -> false;
        };
    }
}
