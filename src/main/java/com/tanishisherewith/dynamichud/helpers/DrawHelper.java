package com.tanishisherewith.dynamichud.helpers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tanishisherewith.dynamichud.DynamicHUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL40C;

import java.awt.*;

/**
 * Credits: <a href="https://github.com/HeliosMinecraft/HeliosClient/blob/main/src/main/java/dev/heliosclient/util/ColorUtils.java">HeliosClient</a>
 */
public class DrawHelper {

    /**
     * Draws a singular gradient rectangle  on screen with the given parameters
     *
     * @param matrix4f   Matrix4f object to draw the gradient
     * @param x          X position of the gradient
     * @param y          Y position of the gradient
     * @param width      Width of the gradient
     * @param height     Height of the gradient
     * @param startColor start color of the gradient
     * @param endColor   end color of the gradient
     * @param direction  Draws the gradient in the given direction
     */
    public static void drawGradient(Matrix4f matrix4f, float x, float y, float width, float height, int startColor, int endColor, Direction direction) {
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;

        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        switch (direction) {
            case LEFT_RIGHT:
                bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                break;
            case TOP_BOTTOM:
                bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                break;
            case RIGHT_LEFT:
                bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                break;
            case BOTTOM_TOP:
                bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(startRed, startGreen, startBlue, startAlpha);
                bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(endRed, endGreen, endBlue, endAlpha);
                break;
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
    }

    public static void enableScissor(int x, int y, int width, int height) {
        double scaleFactor = DynamicHUD.MC.getWindow().getScaleFactor();

        int scissorX = (int) (x * scaleFactor);
        int scissorY = (int) (DynamicHUD.MC.getWindow().getHeight() - ((y + height) * scaleFactor));
        int scissorWidth = (int) (width * scaleFactor);
        int scissorHeight = (int) (height * scaleFactor);

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    public static void disableScissor() {
        RenderSystem.disableScissor();
    }

    /**
     * Draws a singular rectangle on screen with the given parameters
     *
     * @param matrix4f Matrix4f object to draw the rectangle
     * @param x        X position of the rectangle
     * @param y        Y position of the rectangle
     * @param width    Width of the rectangle
     * @param height   Height of the rectangle
     * @param color    Color of the rectangle
     */
    public static void drawRectangle(Matrix4f matrix4f, float x, float y, float width, float height, int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(red, green, blue, alpha);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
    }

    /* ==== Drawing Rectangles ==== */

    /**
     * Draws a singular outline rectangle on screen with the given parameters
     *
     * @param matrix4f Matrix4f object to draw the rectangle
     * @param x        X position of the rectangle
     * @param y        Y position of the rectangle
     * @param width    Width of the rectangle
     * @param height   Height of the rectangle
     * @param color    Color of the rectangle
     */
    public static void drawOutlineBox(Matrix4f matrix4f, float x, float y, float width, float height, float thickness, int color) {
        drawRectangle(matrix4f, x, y, width, thickness, color);
        drawRectangle(matrix4f, x, y + height - thickness, width, thickness, color);
        drawRectangle(matrix4f, x, y + thickness, thickness, height - thickness * 2, color);
        drawRectangle(matrix4f, x + width - thickness, y + thickness, thickness, height - thickness * 2, color);
    }

    /**
     * Draws a singular rectangle with a dark shadow on screen with the given parameters
     * Bad way because there is a better way
     *
     * @param matrix4f      Matrix4f object to draw the rectangle and shadow
     * @param x             X position of the rectangle
     * @param y             Y position of the rectangle
     * @param width         Width of the rectangle
     * @param height        Height of the rectangle
     * @param color         Color of the rectangle
     * @param shadowOpacity Opacity of the shadow (Dark --> Lighter)
     * @param shadowOffsetX X position Offset of the shadow from the main rectangle X pos
     * @param shadowOffsetY Y position Offset of the shadow from the main rectangle Y pos
     */
    public static void drawRectangleWithShadowBadWay(Matrix4f matrix4f, float x, float y, float width, float height, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawRectangle(matrix4f, x + shadowOffsetX, y + shadowOffsetY, width, height, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the rectangle
        drawRectangle(matrix4f, x, y, width, height, color);
    }

    /**
     * Draws an outline rounded rectangle by drawing 4 side rectangles, and 4 arcs
     *
     * @param matrix4f  Matrix4f object to draw the rounded rectangle
     * @param x         X pos
     * @param y         Y pos
     * @param width     Width of rounded rectangle
     * @param height    Height of rounded rectangle
     * @param radius    Radius of the quadrants / the rounded rectangle
     * @param color     Color of the rounded rectangle
     * @param thickness thickness of the outline
     */
    public static void drawOutlineRoundedBox(Matrix4f matrix4f, float x, float y, float width, float height, float radius, float thickness, int color) {
        // Draw the rectangles for the outline
        drawRectangle(matrix4f, x + radius, y, width - radius * 2, thickness, color); // Top rectangle
        drawRectangle(matrix4f, x + radius, y + height - thickness, width - radius * 2, thickness, color); // Bottom rectangle
        drawRectangle(matrix4f, x, y + radius, thickness, height - radius * 2, color); // Left rectangle
        drawRectangle(matrix4f, x + width - thickness, y + radius, thickness, height - radius * 2, color); // Right rectangle

        // Draw the arcs at the corners for the outline
        drawArc(matrix4f, x + radius, y + radius, radius, thickness, color, 180, 270); // Top-left arc
        drawArc(matrix4f, x + width - radius, y + radius, radius, thickness, color, 90, 180); // Top-right arc
        drawArc(matrix4f, x + width - radius, y + height - radius, radius, thickness, color, 0, 90); // Bottom-right arc
        drawArc(matrix4f, x + radius, y + height - radius, radius, thickness, color, 270, 360); // Bottom-left arc
    }

    public static void drawRainbowGradientRectangle(Matrix4f matrix4f, float x, float y, float width, float height, float alpha) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        for (int i = 0; i <= width; i++) {
            float hue = (float) i / width;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            color = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);

            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            float alphaVal = (color >> 24 & 255) / 255.0F;

            bufferBuilder.vertex(matrix4f, x + i, y, 0.0f).color(red, green, blue, alphaVal);
            bufferBuilder.vertex(matrix4f, x + i, y + height, 0.0f).color(red, green, blue, alphaVal);
        }

        for (int i = (int) width; i >= 0; i--) {
            float hue = (float) i / width;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            color = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);

            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            float alphaVal = (color >> 24 & 255) / 255.0F;

            bufferBuilder.vertex(matrix4f, x + i, y + height, 0.0f).color(red, green, blue, alphaVal);
            bufferBuilder.vertex(matrix4f, x + i, y, 0.0f).color(red, green, blue, alphaVal);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
    }


    public static void drawRainbowGradient(Matrix4f matrix, float x, float y, float width, float height) {
        Matrix4f matrix4f = RenderSystem.getModelViewMatrix();

        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
        RenderSystem.colorMask(true, true, true, true);

        drawRectangle(matrix4f, x, y, width, height, Color.BLACK.getRGB());

        RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (float i = 0; i < width; i += 1.0f) {
            float hue = (i / width); // Multiply by 1 to go through the whole color spectrum once (red to red)
            Color color = Color.getHSBColor(hue, 1.0f, 1.0f); // Full saturation and brightness

            bufferBuilder.vertex(matrix4f, x + i, y, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            bufferBuilder.vertex(matrix4f, x + i + 1.0f, y, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            bufferBuilder.vertex(matrix4f, x + i + 1.0f, y + height, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            bufferBuilder.vertex(matrix4f, x + i, y + height, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

        RenderSystem.defaultBlendFunc();
    }


    /* ====  Drawing filled and outline circles  ==== */

    /**
     * Draws an outline of a circle
     *
     * @param matrix4f Matrix4f object to draw the circle outline
     * @param xCenter  X position of the circle outline
     * @param yCenter  Y position of the circle outline
     * @param radius   radius of the circle outline
     * @param color    color of the circle outline
     */
    public static void drawOutlineCircle(Matrix4f matrix4f, float xCenter, float yCenter, float radius, float lineWidth, int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        for (int i = 0; i <= 360; i++) {
            double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
            double y = yCenter + Math.cos(Math.toRadians(i)) * radius;
            double x2 = xCenter + Math.sin(Math.toRadians(i)) * (radius + lineWidth);
            double y2 = yCenter + Math.cos(Math.toRadians(i)) * (radius + lineWidth);
            bufferBuilder.vertex(matrix4f, (float) x, (float) y, 0).color(red, green, blue, alpha);
            bufferBuilder.vertex(matrix4f, (float) x2, (float) y2, 0).color(red, green, blue, alpha);
        }


        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    /**
     * Draws a filled circle
     *
     * @param matrix4f Matrix4f object to draw the circle outline
     * @param xCenter  X position of the circle outline
     * @param yCenter  Y position of the circle outline
     * @param radius   radius of the circle outline
     * @param color    color of the circle outline
     */
    public static void drawFilledCircle(Matrix4f matrix4f, float xCenter, float yCenter, float radius, int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);


        bufferBuilder.vertex(matrix4f, xCenter, yCenter, 0).color(red, green, blue, alpha);

        for (int i = 0; i <= 360; i++) {
            double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
            double y = yCenter + Math.cos(Math.toRadians(i)) * radius;
            bufferBuilder.vertex(matrix4f, (float) x, (float) y, 0).color(red, green, blue, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    /**
     * Draws a filled circle with a shadow bad way
     *
     * @param matrix4f      Matrix4f object to draw the circle
     * @param xCenter       X position of the circle
     * @param yCenter       Y position of the circle
     * @param radius        Radius of the circle
     * @param color         Color of the circle
     * @param shadowOffsetX X position of the circle shadow offset from main circle
     * @param shadowOffsetY X position of the circle shadow offset from main circle
     * @param shadowOpacity Opacity of the circle shadow offset from main circle
     */
    public static void drawCircleWithShadow(Matrix4f matrix4f, float xCenter, float yCenter, float radius, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawFilledCircle(matrix4f, xCenter + shadowOffsetX, yCenter + shadowOffsetY, radius, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the circle
        drawFilledCircle(matrix4f, xCenter, yCenter, radius, color);
    }

    /**
     * Not Tested
     *
     * @param matrix4f
     * @param x
     * @param y
     * @param radius
     * @param startAngle
     * @param endAngle
     * @param color
     */
    @Deprecated
    public static void drawFilledArc(Matrix4f matrix4f, float x, float y, float radius, float startAngle, float endAngle, int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();

        for (float angle = startAngle; angle <= endAngle; angle += 1.0F) {
            float x1 = x + MathHelper.cos(angle * 0.017453292F) * radius;
            float y1 = y + MathHelper.sin(angle * 0.017453292F) * radius;
            float x2 = x + MathHelper.cos((angle + 1.0F) * 0.017453292F) * radius;
            float y2 = y + MathHelper.sin((angle + 1.0F) * 0.017453292F) * radius;

            bufferBuilder.vertex(matrix4f, x, y, 0).color(red, green, blue, alpha);
            bufferBuilder.vertex(matrix4f, x1, y1, 0).color(red, green, blue, alpha);
            bufferBuilder.vertex(matrix4f, x2, y2, 0).color(red, green, blue, alpha);
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
    /* ====  Drawing Quadrants, Arcs, and Triangles  ==== */

    /**
     * Draws a filled Gradient quadrant
     *
     * @param matrix4f   Matrix4f object to draw the quadrant
     * @param xCenter    X position of the quadrant
     * @param yCenter    Y position of the quadrant
     * @param radius     Radius of the quadrant
     * @param startColor start color of the gradient
     * @param endColor   end color of the gradient
     * @param quadrant   Integer value of the quadrant of the circle. 1 == Top Right, 2 == Top Left, 3 == Bottom Right, 4 == Bottom Left
     */
    public static void drawFilledGradientQuadrant(Matrix4f matrix4f, float xCenter, float yCenter, float radius, int startColor, int endColor, int quadrant) {
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;

        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();

        bufferBuilder.vertex(matrix4f, xCenter, yCenter, 0).color(startRed, startGreen, startBlue, startAlpha);

        for (int i = quadrant * 90; i <= quadrant * 90 + 90; i++) {
            double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
            double y = yCenter + Math.cos(Math.toRadians(i)) * radius;

            // Interpolate the color based on the angle
            float t = (float) (i - quadrant * 90) / 90.0f;
            float red = startRed * (1 - t) + endRed * t;
            float green = startGreen * (1 - t) + endGreen * t;
            float blue = startBlue * (1 - t) + endBlue * t;
            float alpha = startAlpha * (1 - t) + endAlpha * t;

            bufferBuilder.vertex(matrix4f, (float) x, (float) y, 0).color(red, green, blue, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    /**
     * Draws an arc
     *
     * @param matrix4f   Matrix4f object to draw the arc
     * @param xCenter    X position of the arc's center
     * @param yCenter    Y position of the arc's center
     * @param radius     Radius of the arc's center circle
     * @param startAngle start Angle of the arc
     * @param endAngle   end Angle of the arc
     * @param thickness  Thickness of the arc (width of the arc)
     */
    public static void drawArc(Matrix4f matrix4f, float xCenter, float yCenter, float radius, float thickness, int color, int startAngle, int endAngle) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();

        for (int i = startAngle; i <= endAngle; i++) {
            double innerX = xCenter + Math.sin(Math.toRadians(i)) * (radius - thickness);
            double innerY = yCenter + Math.cos(Math.toRadians(i)) * (radius - thickness);
            double outerX = xCenter + Math.sin(Math.toRadians(i)) * radius;
            double outerY = yCenter + Math.cos(Math.toRadians(i)) * radius;

            bufferBuilder.vertex(matrix4f, (float) innerX, (float) innerY, 0).color(red, green, blue, alpha);
            bufferBuilder.vertex(matrix4f, (float) outerX, (float) outerY, 0).color(red, green, blue, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
    }

    /**
     * Draws a filled quadrant
     *
     * @param matrix4f Matrix4f object to draw the quadrant
     * @param xCenter  X position of the quadrant
     * @param yCenter  Y position of the quadrant
     * @param radius   Radius of the quadrant
     * @param color    color of the quadrant
     * @param quadrant Integer value of the quadrant of the circle. 1 == Top Right, 2 == Top Left, 3 == Bottom Right, 4 == Bottom Left
     */
    public static void drawFilledQuadrant(Matrix4f matrix4f, float xCenter, float yCenter, float radius, int color, int quadrant) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();

        bufferBuilder.vertex(matrix4f, xCenter, yCenter, 0).color(red, green, blue, alpha);

        for (int i = quadrant * 90; i <= quadrant * 90 + 90; i++) {
            double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
            double y = yCenter + Math.cos(Math.toRadians(i)) * radius;
            bufferBuilder.vertex(matrix4f, (float) x, (float) y, 0).color(red, green, blue, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

    }

    /**
     * Draws a Triangle with the given coordinates
     *
     * @param matrix4f
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param color
     */
    public static void drawOutlineTriangle(Matrix4f matrix4f, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix4f, x1, y1, 0).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, x2, y2, 0).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, x3, y3, 0).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, x1, y1, 0).color(red, green, blue, alpha);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    /**
     * Draws a outline quadrant
     *
     * @param matrix4f Matrix4f object to draw the quadrant
     * @param xCenter  X position of the quadrant
     * @param yCenter  Y position of the quadrant
     * @param radius   Radius of the quadrant
     * @param color    color of the quadrant
     * @param quadrant Integer value of the quadrant of the circle. 1 == Top Right, 2 == Top Left, 3 == Bottom Right, 4 == Bottom Left
     */
    public static void drawOutlineQuadrant(Matrix4f matrix4f, float xCenter, float yCenter, float radius, int quadrant, int color) {
        int startAngle = 0;
        int endAngle = 0;

        if (quadrant == 1) {
            startAngle = 270;
            endAngle = 360;
        } else if (quadrant == 2) {
            startAngle = 180;
            endAngle = 270;
        } else if (quadrant == 3) {
            startAngle = 90;
            endAngle = 180;
        } else if (quadrant == 4) {
            endAngle = 90;
        }

        drawArc(matrix4f, xCenter, yCenter, radius, 1f, color, startAngle, endAngle);
    }

    /**
     * Draws a filled rounded rectangle by drawing 1 main rectangle, 4 side rectangles, and 4 filled quadrants
     *
     * @param matrix4f Matrix4f object to draw the rounded rectangle
     * @param x        X pos
     * @param y        Y pos
     * @param width    Width of rounded rectangle
     * @param height   Height of rounded rectangle
     * @param radius   Radius of the quadrants / the rounded rectangle
     * @param color    Color of the rounded rectangle
     */
    public static void drawRoundedRectangle(Matrix4f matrix4f, float x, float y, float width, float height, float radius, int color) {
        drawRoundedRectangle(matrix4f, x, y, true, true, true, true, width, height, radius, color);
    }

    /* ====  Drawing Rounded Rectangles  ==== */

    /**
     * Draws a filled rounded rectangle by drawing 1 main rectangle, 4 side rectangles, and specified filled quadrants
     *
     * @param matrix4f Matrix4f object to draw the rounded rectangle
     * @param x        X pos
     * @param y        Y pos
     * @param TL       Whether to draw the top left quadrant
     * @param TR       Whether to draw the top right quadrant
     * @param BL       Whether to draw the bottom left quadrant
     * @param BR       Whether to draw the bottom right quadrant
     * @param width    Width of rounded rectangle
     * @param height   Height of rounded rectangle
     * @param radius   Radius of the quadrants / the rounded rectangle
     * @param color    Color of the rounded rectangle
     */
    public static void drawRoundedRectangle(Matrix4f matrix4f, float x, float y, boolean TL, boolean TR, boolean BL, boolean BR, float width, float height, float radius, int color) {
        // Draw the main rectangle
        drawRectangle(matrix4f, x + radius, y + radius, width - 2 * radius, height - 2 * radius, color);

        // Draw rectangles at the sides
        drawRectangle(matrix4f, x + radius, y, width - 2 * radius, radius, color); // top
        drawRectangle(matrix4f, x + radius, y + height - radius, width - 2 * radius, radius, color); // bottom
        drawRectangle(matrix4f, x, y + radius, radius, height - 2 * radius, color); // left
        drawRectangle(matrix4f, x + width - radius, y + radius, radius, height - 2 * radius, color); // right

        if (TL) {
            drawFilledQuadrant(matrix4f, x + radius, y + radius, radius, color, 2);
        } else {
            drawRectangle(matrix4f, x, y, radius, radius, color);
        }
        if (TR) {
            drawFilledQuadrant(matrix4f, x + width - radius, y + radius, radius, color, 1);
        } else {
            drawRectangle(matrix4f, x + width - radius, y, radius, radius, color);
        }
        if (BL) {
            drawFilledQuadrant(matrix4f, x + radius, y + height - radius, radius, color, 3);
        } else {
            drawRectangle(matrix4f, x, y + height - radius, radius, radius, color);
        }
        if (BR) {
            drawFilledQuadrant(matrix4f, x + width - radius, y + height - radius, radius, color, 4);
        } else {
            drawRectangle(matrix4f, x + width - radius, y + height - radius, radius, radius, color);
        }
    }

    /**
     * Draws an outline rounded gradient rectangle
     *
     * @param matrix4f Matrix4f object to draw the rounded gradient rectangle
     * @param color1   is applied to the bottom-left vertex (x, y + height).
     * @param color2   is applied to the bottom-right vertex (x + width, y + height).
     * @param color3   is applied to the top-right vertex (x + width, y).
     * @param color4   is applied to the top-left vertex (x, y).
     * @param x        X pos
     * @param y        Y pos
     * @param width    Width of rounded gradient rectangle
     * @param height   Height of rounded gradient rectangle
     * @param radius   Radius of the quadrants / the rounded gradient rectangle
     */
    public static void drawOutlineGradientRoundedBox(Matrix4f matrix4f, float x, float y, float width, float height, float radius, float thickness, Color color1, Color color2, Color color3, Color color4) {
        // Draw the rectangles for the outline with gradient
        drawGradient(matrix4f, x + radius, y, width - radius * 2, thickness, color1.getRGB(), color2.getRGB(), Direction.LEFT_RIGHT); // Top rectangle
        drawGradient(matrix4f, x + radius, y + height - thickness, width - radius * 2, thickness, color3.getRGB(), color4.getRGB(), Direction.RIGHT_LEFT); // Bottom rectangle

        drawGradient(matrix4f, x, y + radius, thickness, height - radius * 2, color4.getRGB(), color1.getRGB(), Direction.BOTTOM_TOP); // Left rectangle
        drawGradient(matrix4f, x + width - thickness, y + radius, thickness, height - radius * 2, color2.getRGB(), color3.getRGB(), Direction.TOP_BOTTOM); // Right rectangle

        // Draw the arcs at the corners for the outline with gradient
        drawArc(matrix4f, x + radius, y + radius, radius, thickness, color1.getRGB(), 180, 270); // Top-left arc
        drawArc(matrix4f, x + width - radius, y + radius, radius, thickness, color2.getRGB(), 90, 180); // Top-right arc
        drawArc(matrix4f, x + width - radius, y + height - radius, radius, thickness, color3.getRGB(), 0, 90); // Bottom-right arc
        drawArc(matrix4f, x + radius, y + height - radius, radius, thickness, color4.getRGB(), 270, 360); // Bottom-left arc
    }

    public static void drawCutRectangle(DrawContext drawContext, int x1, int y1, int x2, int y2, int z, int color, int cornerRadius) {
        // Draw the rectangles
        drawContext.fill(x1 + cornerRadius, y1, x2 - cornerRadius, y1 + cornerRadius, z, color);
        drawContext.fill(x1 + cornerRadius, y2 - cornerRadius, x2 - cornerRadius, y2, z, color);
        drawContext.fill(x1, y1 + cornerRadius, x2, y2 - cornerRadius, z, color);
    }

    /**
     * Draws a rounded rectangle with a shadow in a bad way
     *
     * @param matrix4f      Matrix4f object to draw the rounded rectangle
     * @param x             X pos
     * @param y             Y pos
     * @param width         Width of rounded rectangle
     * @param height        Height of rounded rectangle
     * @param radius        Radius of the quadrants / the rounded rectangle
     * @param color         Color of the rounded rectangle
     * @param shadowOpacity opacity of the shadow
     * @param shadowOffsetX X offset of the shadow
     * @param shadowOffsetY Y offset of the shadow
     */
    public static void drawRoundedRectangleWithShadowBadWay(Matrix4f matrix4f, float x, float y, float width, float height, float radius, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawRoundedRectangle(matrix4f, x + shadowOffsetX, y + shadowOffsetY, width, height, radius, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the rounded rectangle
        drawRoundedRectangle(matrix4f, x, y, width, height, radius, color);
    }

    /**
     * Draws a rounded gradient rectangle
     *
     * @param matrix Matrix4f object to draw the rounded gradient rectangle
     * @param color1 is applied to the bottom-left vertex (x, y + height).
     * @param color2 is applied to the bottom-right vertex (x + width, y + height).
     * @param color3 is applied to the top-right vertex (x + width, y).
     * @param color4 is applied to the top-left vertex (x, y).
     * @param x      X pos
     * @param y      Y pos
     * @param width  Width of rounded gradient rectangle
     * @param height Height of rounded gradient rectangle
     * @param radius Radius of the quadrants / the rounded gradient rectangle
     */
    public static void drawRoundedGradientRectangle(Matrix4f matrix, Color color1, Color color2, Color color3, Color color4, float x, float y, float width, float height, float radius) {
        drawRoundedGradientRectangle(matrix, color1, color2, color3, color4, x, y, width, height, radius, true, true, true, true);
    }

    /**
     * Draws a rounded gradient rectangle
     *
     * @param matrix Matrix4f object to draw the rounded gradient rectangle
     * @param color1 is applied to the bottom-left vertex (x, y + height).
     * @param color2 is applied to the bottom-right vertex (x + width, y + height).
     * @param color3 is applied to the top-right vertex (x + width, y).
     * @param color4 is applied to the top-left vertex (x, y).
     * @param x      X pos
     * @param y      Y pos
     * @param width  Width of rounded gradient rectangle
     * @param height Height of rounded gradient rectangle
     * @param radius Radius of the quadrants / the rounded gradient rectangle
     */
    public static void drawRoundedGradientRectangle(Matrix4f matrix, Color color1, Color color2, Color color3, Color color4, float x, float y, float width, float height, float radius, boolean TL, boolean TR, boolean BL, boolean BR) {
        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
        RenderSystem.colorMask(true, true, true, true);

        drawRoundedRectangle(matrix, x, y, TL, TR, BL, BR, width, height, (int) radius, color1.getRGB());

        RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(color1.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(color2.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(color3.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(color4.getRGB());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();

        RenderSystem.defaultBlendFunc();
    }

    /* ==== Drawing Lines ==== */
    public static void drawVerticalLine(Matrix4f matrix4f, float x, float y1, float height, float thickness, int color) {
        drawRectangle(matrix4f, x, y1, thickness, height, color);
    }

    public static void drawHorizontalLine(Matrix4f matrix4f, float x1, float width, float y, float thickness, int color) {
        drawRectangle(matrix4f, x1, y, width, thickness, color);
    }

    /**
     * Draws an outlined box on the screen.
     *
     * @param x1    The x position of the top left corner of the box
     * @param y1    The y position of the top left corner of the box
     * @param x2    The x position of the bottom right corner of the box
     * @param y2    The y position of the bottom right corner of the box
     * @param color The color to draw the box with
     */
    public static void drawOutlinedBox(DrawContext drawContext, int x1, int y1, int x2, int y2, int color) {
        drawContext.fill(x1, y1, x2, y1 + 1, color);
        drawContext.fill(x1, y2 - 1, x2, y2, color);
        drawContext.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
        drawContext.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    /**
     * This method assumes that the x, y coords are the origin of a widget.
     *
     * @param x     X position of widget
     * @param y     Y position of widget
     * @param scale Scale the matrices
     */
    public static void scaleAndPosition(MatrixStack matrices, float x, float y, float scale) {
        matrices.push(); // Save the current transformation state

        // Translate the origin back to the desired position
        matrices.translate(x, y, 0);

        // Scale the matrix
        matrices.scale(scale, scale, 1.0F);

        matrices.translate(-x, -y, 0);
    }

    /**
     * This method scales the matrices by the centre of the widget
     *
     * @param x      X position of widget
     * @param y      Y position of widget
     * @param height height of widget
     * @param width  width of widget
     * @param scale  Scale the matrices
     */
    public static void scaleAndPosition(MatrixStack matrices, float x, float y, float width, float height, float scale) {
        matrices.push(); // Save the current transformation state

        // Translate the origin back to the desired position
        matrices.translate(x + width / 2.0f, y + height / 2.0f, 0);

        // Scale the matrix
        matrices.scale(scale, scale, 1.0F);

        matrices.translate(-(x + width / 2.0f), -(y + height / 2.0f), 0);
    }

    public static void stopScaling(MatrixStack matrices) {
        matrices.pop(); // Restore the previous transformation state
    }

    public enum Direction {
        /* LEFT_RIGHT means from left to right. Same for others */
        LEFT_RIGHT, TOP_BOTTOM, RIGHT_LEFT, BOTTOM_TOP
    }

}