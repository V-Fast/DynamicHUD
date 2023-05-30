package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

import java.awt.*;

public class TextWidget extends Widget {
    private String text;
    private boolean shadow;
    private boolean rainbow;
    private boolean verticalRainbow;
    // In TextWidget class
    private int backgroundColor;
    private int color = Color.WHITE.getRGB();
    protected static float rainbowSpeed = 10f;


    public TextWidget(MinecraftClient client, String text, float xPercent, float yPercent) {
        super(client);
        this.text = text;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public static float getRainbowSpeed() {
        return rainbowSpeed;
    }

    public static void setRainbowSpeed(float rainbowSpeed) {
        TextWidget.rainbowSpeed = rainbowSpeed;
    }

    public void setVerticalRainbow(boolean verticalRainbow) {
        this.verticalRainbow = verticalRainbow;
    }

    public boolean hasRainbow() {
        return rainbow;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public boolean hasVerticalRainbow() {
        return verticalRainbow;
    }

    public String getText() {
        return text;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
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