package com.tanishisherewith.dynamichud.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * This class provides helper methods for working with colors.
 */
public class ColorHelper {

    //Aliases for chat formatting from heliosclient
    public static String colorChar = "\247";
    public static String black = "\2470";
    public static String darkBlue = "\2471";
    public static String darkGreen = "\2472";
    public static String darkAqua = "\2473";
    public static String darkRed = "\2474";
    public static String darkMagenta = "\2475";
    public static String gold = "\2476";
    public static String gray = "\2477";
    public static String darkGray = "\2478";
    public static String blue = "\2479";
    public static String green = "\247a";
    public static String aqua = "\247b";
    public static String red = "\247c";
    public static String magenta = "\247d";
    public static String yellow = "\247e";
    public static String white = "\247f";

    public static String underline = "\247n";
    public static String bold = "\247l";
    public static String italic = "\247o";
    public static String strikethrough = "\247m";
    public static String obfuscated = "\247k";
    public static String reset = "\247r";

    public static int r, g, b, a;

    public ColorHelper(int r, int g, int b, int a) {
        ColorHelper.r = r;
        ColorHelper.g = g;
        ColorHelper.b = b;
        ColorHelper.a = a;
        validate();
    }

    public ColorHelper() {
    }

    /**
     * Returns a color as an integer value given its red, green and blue components.
     *
     * @param red   The red component of the color
     * @param green The green component of the color
     * @param blue  The blue component of the color
     * @return The color as an integer value
     */
    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    /**
     * Returns a color as an integer value given its red, green, blue and alpha components.
     *
     * @param red   The red component of the color
     * @param green The green component of the color
     * @param blue  The blue component of the color
     * @param alpha The alpha component of the color
     * @return The color as an integer value
     */
    public static int getColor(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static Color getColorFromInt(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        return new Color(red, green, blue);
    }

    /**
     * Returns a color as an integer value given its hue.
     *
     * @param hue The hue of the color
     * @return The color as an integer value
     */
    public static int getColorFromHue(float hue) {
        return Color.HSBtoRGB(hue, 1.0f, 1.0f);
    }

    /**
     * Converts a color to an integer.
     *
     * @param color The color to convert
     * @return The integer representation of the JWT color
     */
    public static int ColorToInt(Color color) {
        return color.getRGB();
    }

    public static float[] getRainbowColor() {
        float x = System.currentTimeMillis() % 2000 / 1000F;
        float pi = (float) Math.PI;

        float[] rainbow = new float[3];
        rainbow[0] = 0.5F + 0.5F * MathHelper.sin(x * pi);
        rainbow[1] = 0.5F + 0.5F * MathHelper.sin((x + 4F / 3F) * pi);
        rainbow[2] = 0.5F + 0.5F * MathHelper.sin((x + 8F / 3F) * pi);
        return rainbow;
    }

    /**
     * @param color Target color.
     * @return Alpha of the color.
     */
    public static float getAlpha(int color) {
        return (float) (color >> 24 & 255) / 255.0F;
    }

    /**
     * @param color Target color.
     * @return Red value of the color.
     */
    public static float getRed(int color) {
        return (float) (color >> 16 & 255) / 255.0F;
    }

    /**
     * @param color Target color.
     * @return Green value of the color.
     */
    public static float getGreen(int color) {
        return (float) (color >> 8 & 255) / 255.0F;
    }

    /**
     * @param color Target color.
     * @return Blue value of the color.
     */
    public static float getBlue(int color) {
        return (float) (color & 255) / 255.0F;
    }

    /**
     * Rainbow color with custom speed.
     *
     * @param speed
     * @return Current rainbow color.
     */
    public static Color getRainbowColor(int speed) {
        float hue = (System.currentTimeMillis() % (speed * 100)) / (speed * 100.0f);
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    /**
     * Changes alpha on color.
     *
     * @param color Target color.
     * @param alpha Target alpha.
     * @return Color with changed alpha.
     */
    public static Color changeAlpha(Color color, int alpha) {
        if (color != null)
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        else
            return new Color(0);
    }

    public static int[] getMousePixelColor(double mouseX, double mouseY){
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        if (framebuffer != null) {
            int x = (int) (mouseX * framebuffer.textureWidth / MinecraftClient.getInstance().getWindow().getScaledWidth());
            int y = (int) ((MinecraftClient.getInstance().getWindow().getScaledHeight() - mouseY) * framebuffer.textureHeight / MinecraftClient.getInstance().getWindow().getScaledHeight());

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

                    return new int[]{red,green,blue};
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Framebuffer is null");
        }
        return null;
    }

    public static int fromRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    public static int toRGBAR(int color) {
        return (color >> 16) & 0x000000FF;
    }

    public static int toRGBAG(int color) {
        return (color >> 8) & 0x000000FF;
    }

    public static int toRGBAB(int color) {
        return (color) & 0x000000FF;
    }

    public static int toRGBAA(int color) {
        return (color >> 24) & 0x000000FF;
    }

    public void validate() {
        if (r < 0) r = 0;
        else if (r > 255) r = 255;

        if (g < 0) g = 0;
        else if (g > 255) g = 255;

        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        if (a < 0) a = 0;
        else if (a > 255) a = 255;
    }

    public int toInt() {
        return new Color(r, b, g, a).getRGB();
    }
}
