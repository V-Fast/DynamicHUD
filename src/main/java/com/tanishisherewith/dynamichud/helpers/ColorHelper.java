package com.tanishisherewith.dynamichud.helpers;

import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 * This class provides helper methods for working with colors.
 */
public class ColorHelper {
    public static int r, g, b, a;
    public ColorHelper(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
        validate();
    }
    public ColorHelper() {
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
    public static float[] getRainbowColor()
    {
        float x = System.currentTimeMillis() % 2000 / 1000F;
        float pi = (float)Math.PI;

        float[] rainbow = new float[3];
        rainbow[0] = 0.5F + 0.5F * MathHelper.sin(x * pi);
        rainbow[1] = 0.5F + 0.5F * MathHelper.sin((x + 4F / 3F) * pi);
        rainbow[2] = 0.5F + 0.5F * MathHelper.sin((x + 8F / 3F) * pi);
        return rainbow;
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
    public int toInt()
    {
     return new Color(r,b,g,a).getRGB();
    }
}
