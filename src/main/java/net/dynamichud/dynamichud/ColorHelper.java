package net.dynamichud.dynamichud;

import java.awt.*;

public class ColorHelper {
    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    public static int getColorFromHue(float hue) {
        return Color.HSBtoRGB(hue, 1.0f, 1.0f);
    }

}
