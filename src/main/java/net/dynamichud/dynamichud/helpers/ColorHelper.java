package net.dynamichud.dynamichud.helpers;

import java.awt.*;

/**
 * This class provides helper methods for working with colors.
 */
public class ColorHelper {
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

}
