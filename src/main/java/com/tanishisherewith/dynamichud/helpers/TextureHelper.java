package com.tanishisherewith.dynamichud.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is entirely untested so some issues may occur which in case should be reported immediately.
 */
public class TextureHelper {
    static MinecraftClient mc = MinecraftClient.getInstance();

    public static NativeImage loadTexture(Identifier textureId) {
        if (mc.getResourceManager().getResource(textureId).isPresent()) {
            try (InputStream inputStream = mc.getResourceManager().getResource(textureId).get().getInputStream()) {
                return NativeImage.read(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load texture " + textureId, e);
            }
        }
        return null;
    }

    public static NativeImage resizeTexture(NativeImage image, int newWidth, int newHeight) {
        NativeImage result = new NativeImage(newWidth, newHeight, false);

        int oldWidth = image.getWidth();
        int oldHeight = image.getHeight();

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int srcX = x * oldWidth / newWidth;
                int srcY = y * oldHeight / newHeight;

                result.setColor(x, y, image.getColor(srcX, srcY));
            }
        }

        return result;
    }

    public static NativeImage resizeTextureUsingBilinearInterpolation(NativeImage image, int newWidth, int newHeight) {
        NativeImage result = new NativeImage(newWidth, newHeight, false);

        float x_ratio = ((float) (image.getWidth() - 1)) / newWidth;
        float y_ratio = ((float) (image.getHeight() - 1)) / newHeight;
        float x_diff, y_diff, blue, red, green;
        int offset, a, b, c, d, index;

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int x = (int) (x_ratio * j);
                int y = (int) (y_ratio * i);
                x_diff = (x_ratio * j) - x;
                y_diff = (y_ratio * i) - y;

                // Indexes of the 4 surrounding pixels
                a = image.getColor(x, y);
                b = image.getColor(x + 1, y);
                c = image.getColor(x, y + 1);
                d = image.getColor(x + 1, y + 1);

                // Blue element
                blue = (a & 0xff) * (1 - x_diff) * (1 - y_diff) + (b & 0xff) * (x_diff) * (1 - y_diff) +
                        (c & 0xff) * (y_diff) * (1 - x_diff) + (d & 0xff) * (x_diff * y_diff);

                // Green element
                green = ((a >> 8) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 8) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 8) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 8) & 0xff) * (x_diff * y_diff);

                // Red element
                red = ((a >> 16) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 16) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 16) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 16) & 0xff) * (x_diff * y_diff);

                result.setColor(j, i,
                        ((((int) red) << 16) & 0xff0000) |
                                ((((int) green) << 8) & 0xff00) |
                                ((int) blue) & 0xff);
            }
        }

        return result;
    }

    public static NativeImage invertTexture(NativeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getColor(x, y);

                int alpha = (argb >> 24) & 0xFF;
                int red = 255 - ((argb >> 16) & 0xFF);
                int green = 255 - ((argb >> 8) & 0xFF);
                int blue = 255 - (argb & 0xFF);

                int newArgb = (alpha << 24) | (red << 16) | (green << 8) | blue;

                result.setColor(x, y, newArgb);
            }
        }

        return result;
    }

    public static NativeImage rotateTexture(NativeImage image, int degrees) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage result = new NativeImage(height, width, false);

        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double angle = Math.toRadians(degrees);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int newX = (int) ((x - centerX) * Math.cos(angle) - (y - centerY) * Math.sin(angle) + centerX);
                int newY = (int) ((x - centerX) * Math.sin(angle) + (y - centerY) * Math.cos(angle) + centerY);

                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    result.setColor(newY, newX, image.getColor(x, y));
                }
            }
        }

        return result;
    }

    private static NativeImage flipTextureHorizontally(NativeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setColor(width - x - 1, y, image.getColor(x, y));
            }
        }

        return result;
    }

    private static NativeImage flipTextureVertically(NativeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setColor(x, height - y - 1, image.getColor(x, y));
            }
        }

        return result;
    }

    public static NativeImage flipTexture(NativeImage image, boolean flipVertically) {
        if (flipVertically) {
            return flipTextureVertically(image);
        } else {
            return flipTextureHorizontally(image);
        }
    }

    public static NativeImage applyGrayScaleFilter(NativeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getColor(x, y);

                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                int gray = (red + green + blue) / 3;
                int newArgb = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

                result.setColor(x, y, newArgb);
            }
        }

        return result;
    }

    public static NativeImage cropTexture(NativeImage image, int x, int y, int width, int height) {
        NativeImage result = new NativeImage(width, height, false);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.setColor(j, i, image.getColor(x + j, y + i));
            }
        }

        return result;
    }

    public static NativeImage tintTexture(NativeImage image, int color) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getColor(x, y);

                int alpha = (argb >> 24) & 0xFF;
                int red = ((argb >> 16) & 0xFF) * ((color >> 16) & 0xFF) / 255;
                int green = ((argb >> 8) & 0xFF) * ((color >> 8) & 0xFF) / 255;
                int blue = (argb & 0xFF) * (color & 0xFF) / 255;

                int newArgb = (alpha << 24) | (red << 16) | (green << 8) | blue;

                result.setColor(x, y, newArgb);
            }
        }

        return result;
    }

    public static NativeImage overlayTexture(NativeImage image, NativeImage overlay) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb1 = image.getColor(x, y);
                int argb2 = overlay.getColor(x, y);

                int alpha = Math.max((argb1 >> 24) & 0xFF, (argb2 >> 24) & 0xFF);
                int red = Math.min(255, ((argb1 >> 16) & 0xFF) + ((argb2 >> 16) & 0xFF));
                int green = Math.min(255, ((argb1 >> 8) & 0xFF) + ((argb2 >> 8) & 0xFF));
                int blue = Math.min(255, (argb1 & 0xFF) + (argb2 & 0xFF));

                int newArgb = (alpha << 24) | (red << 16) | (green << 8) | blue;

                result.setColor(x, y, newArgb);
            }
        }

        return result;
    }

    public static int getAverageColor(NativeImage image) {
        long redTotal = 0;
        long greenTotal = 0;
        long blueTotal = 0;
        int pixelCount = image.getWidth() * image.getHeight();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getColor(x, y);

                redTotal += (argb >> 16) & 0xFF;
                greenTotal += (argb >> 8) & 0xFF;
                blueTotal += argb & 0xFF;
            }
        }

        int redAverage = (int) (redTotal / pixelCount);
        int greenAverage = (int) (greenTotal / pixelCount);
        int blueAverage = (int) (blueTotal / pixelCount);

        return (redAverage << 16) | (greenAverage << 8) | blueAverage;
    }


}
