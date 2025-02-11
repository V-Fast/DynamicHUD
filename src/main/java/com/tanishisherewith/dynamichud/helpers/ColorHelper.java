package com.tanishisherewith.dynamichud.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

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

    public static int[] getMousePixelColor(double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        Framebuffer framebuffer = client.getFramebuffer();
        Window window = client.getWindow();

        // Get the window and framebuffer dimensions
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        int framebufferWidth = framebuffer.textureWidth;
        int framebufferHeight = framebuffer.textureHeight;

        // Calculate scaling factors
        double scaleX = (double) framebufferWidth / windowWidth;
        double scaleY = (double) framebufferHeight / windowHeight;

        // Convert mouse coordinates to framebuffer coordinates
        int x = (int) (mouseX * scaleX);
        int y = (int) ((windowHeight - mouseY) * scaleY);

        // Ensure the coordinates are within the framebuffer bounds
        if (x < 0 || x >= framebufferWidth || y < 0 || y >= framebufferHeight) {
            System.err.println("Mouse coordinates are out of bounds");
            return null;
        }

        // Allocate a buffer to store the pixel data
        ByteBuffer buffer = ByteBuffer.allocateDirect(4); // 4 bytes for RGBA

        // Bind the framebuffer for reading
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer.fbo);

        // Read the pixel at the mouse position
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Extract the color components from the buffer
        int red = buffer.get(0) & 0xFF;
        int green = buffer.get(1) & 0xFF;
        int blue = buffer.get(2) & 0xFF;
        int alpha = buffer.get(3) & 0xFF;

        return new int[]{red, green, blue, alpha};
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
