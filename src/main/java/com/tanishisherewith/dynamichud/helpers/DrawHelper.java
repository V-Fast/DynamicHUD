package com.tanishisherewith.dynamichud.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class DrawHelper extends DrawContext {
    public DrawHelper(MinecraftClient client, VertexConsumerProvider.Immediate vertexConsumers) {
        super(client, vertexConsumers);
    }

    /**
     * Fills a box on the screen with a specified color.
     *
     * @param drawContext The matrix stack used for rendering
     * @param x        The x position of the rectangle
     * @param y        The y position of the rectangle
     * @param width    The width of the rectangle
     * @param height   The height of the rectangle
     * @param color    The color to fill the rectangle with
     */
    public static void drawBox(DrawContext drawContext, int x, int y, int width, int height, int color) {
        int x1 = x - width / 2 - 2;
        int y1 = y - height / 2 - 2;
        int x2 = x + width / 2 + 2;
        int y2 = y + height / 2 + 2;
        drawContext.fill(x1, y1, x2, y2, color);
    }

    /**
     * Fills a rectangle on the screen with a specified color.
     *
     * @param x1       The x position of the top left corner of the rectangle
     * @param y1       The y position of the top left corner of the rectangle
     * @param x2       The x position of the bottom right corner of the rectangle
     * @param y2       The y position of the bottom right corner of the rectangle
     * @param color    The color to fill the rectangle with
     */
    public static void fill(DrawContext drawContext, int x1, int y1, int x2, int y2, int color) {
        drawContext.fill(x1, y1, x2, y2, color);
    }

    /**
     * Draws text on screen.
     *
     * @param textRenderer - TextRenderer instance used for rendering.
     * @param text         - Text to be drawn.
     * @param x            - X position to draw at.
     * @param y            - Y position to draw at.
     * @param color        - Color to draw with.
     */
    public static void drawText(DrawContext drawContext,
                                TextRenderer textRenderer,
                                String text,
                                int x,
                                int y,
                                int color,
                                boolean shadow) {
        drawContext.drawText(textRenderer,text, x, y, color, shadow);
    }

    /**
     * Fills a rounded rectangle on screen with specified color.
     * This causes a lot of problems and for some reason does not work for ArmorWidget when used for contextMenu
     *
     * @param matrix4f     - Matrix4f used for rendering.
     * @param x1           - X position of top left corner of rectangle.
     * @param y1           - Y position of top left corner of rectangle.
     * @param x2           - X position of bottom right corner of rectangle.
     * @param y2           - Y position of bottom right corner of rectangle.
     * @param cornerRadius - Radius of rounded corners.
     * @param color        - Color to fill rectangle with.
     */
    public static void fillRoundedRect(Matrix4f matrix4f, int x1, int y1, int x2, int y2, int cornerRadius, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Draw the center rectangle
        bufferBuilder.vertex(matrix4f, x1 + cornerRadius, y2 - cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x2 - cornerRadius, y2 - cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x2 - cornerRadius, y1 + cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x1 + cornerRadius, y1 + cornerRadius, 0).color(red, green, blue, alpha).next();

        // Draw the side rectangles
        bufferBuilder.vertex(matrix4f, x1, y1 + cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x1 + cornerRadius, y1 + cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x1 + cornerRadius, y2 - cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x1, y2 - cornerRadius, 0).color(red, green, blue, alpha).next();

        bufferBuilder.vertex(matrix4f, x2, y1 + cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x2 - cornerRadius, y1 + cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x2 - cornerRadius, y2 - cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x2, y2 - cornerRadius, 0).color(red, green, blue, alpha).next();


        // Draw the rounded corners
        for (int i = 0; i <= 90; i += 5) {
            double angle = Math.toRadians(i);
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            bufferBuilder.vertex(matrix4f, (float) (x1 + cornerRadius * (1 - cos)), (float) (y1 + cornerRadius * (1 - sin)), 0).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrix4f, (float) (x1 + cornerRadius * (1 - cos)), (float) (y2 - cornerRadius * (1 - sin)), 0).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrix4f, (float) (x2 - cornerRadius * (1 - cos)), (float) (y2 - cornerRadius * (1 - sin)), 0).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrix4f, (float) (x2 - cornerRadius * (1 - cos)), (float) (y1 + cornerRadius * (1 - sin)), 0).color(red, green, blue, alpha).next();
        }
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    public static void fillRoundedRect(DrawContext drawContext, int left, int top, int right, int bottom, int color) {
        drawContext.fill( left + 1, top, right - 1, top + 1, color);
        drawContext.fill( left + 1, bottom - 1, right - 1, bottom, color);
        drawContext.fill(left, top + 1, left + 1, bottom - 1, color);
        drawContext.fill(right - 1, top + 1, right, bottom - 1, color);
        drawContext.fill(left + 1, top + 1, right - 1, bottom - 1, color);
    }

    /**
     * Fills a rectangle on screen with a gradient.
     *
     * @param matrix4f    - Matrix4f used for rendering.
     * @param x1          - X position of top left corner of rectangle.
     * @param y1          - Y position of top left corner of rectangle.
     * @param x2          - X position of bottom right corner of rectangle.
     * @param y2          - Y position of bottom right corner of rectangle.
     * @param topColor    - Color at top of gradient.
     * @param bottomColor - Color at bottom of gradient.
     */
    public static void fillGradient(Matrix4f matrix4f, int x1, int y1, int x2, int y2, int topColor, int bottomColor) {
        float topAlpha = (float) (topColor >> 24 & 255) / 255.0F;
        float topRed = (float) (topColor >> 16 & 255) / 255.0F;
        float topGreen = (float) (topColor >> 8 & 255) / 255.0F;
        float topBlue = (float) (topColor & 255) / 255.0F;
        float bottomAlpha = (float) (bottomColor >> 24 & 255) / 255.0F;
        float bottomRed = (float) (bottomColor >> 16 & 255) / 255.0F;
        float bottomGreen = (float) (bottomColor >> 8 & 255) / 255.0F;
        float bottomBlue = (float) (bottomColor & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, x1, y2, 0).color(topRed, topGreen, topBlue, topAlpha).next();
        bufferBuilder.vertex(matrix4f, x2, y2, 0).color(topRed, topGreen, topBlue, topAlpha).next();
        bufferBuilder.vertex(matrix4f, x2, y1, 0).color(bottomRed, bottomGreen, bottomBlue, bottomAlpha).next();
        bufferBuilder.vertex(matrix4f, x1, y1, 0).color(bottomRed, bottomGreen, bottomBlue, bottomAlpha).next();
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    /**
     * Draws an outlined box on the screen.
     *
     * @param x1       The x position of the top left corner of the box
     * @param y1       The y position of the top left corner of the box
     * @param x2       The x position of the bottom right corner of the box
     * @param y2       The y position of the bottom right corner of the box
     * @param color    The color to draw the box with
     */
    public static void drawOutlinedBox(DrawContext drawContext, int x1, int y1, int x2, int y2, int color) {
        drawContext.fill( x1, y1, x2, y1 + 1, color);
        drawContext.fill( x1, y2 - 1, x2, y2, color);
        drawContext.fill( x1, y1 + 1, x1 + 1, y2 - 1, color);
        drawContext.fill( x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

}
