package com.tanishisherewith.dynamichud.helpers;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.internal.IRenderLayer;
import com.tanishisherewith.dynamichud.utils.CustomRenderLayers;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL40C;

import java.awt.*;
import java.util.Objects;

import static com.tanishisherewith.dynamichud.helpers.TextureHelper.mc;

/**
 * Credits: <a href="https://github.com/HeliosMinecraft/HeliosClient/blob/main/src/main/java/dev/heliosclient/util/render/Renderer2D.java">HeliosClient</a>
 */
public class DrawHelper {

    /**
     * Draws a singular gradient rectangle  on screen with the given parameters
     *
     * @param x          X position of the gradient
     * @param y          Y position of the gradient
     * @param width      Width of the gradient
     * @param height     Height of the gradient
     * @param startColor start color of the gradient
     * @param endColor   end color of the gradient
     * @param direction  Draws the gradient in the given direction
     */
    public static void drawGradient(DrawContext drawContext, float x, float y, float width, float height, int startColor, int endColor, Direction direction) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getDebugQuads());

            switch (direction) {
                case LEFT_RIGHT:
                    consumer.vertex(matrix4f, x, y + height, 0.0F).color(startColor);
                    consumer.vertex(matrix4f, x + width, y + height, 0.0F).color(endColor);
                    consumer.vertex(matrix4f, x + width, y, 0.0F).color(endColor);
                    consumer.vertex(matrix4f, x, y, 0.0F).color(startColor);
                    break;
                case TOP_BOTTOM:
                    consumer.vertex(matrix4f, x, y + height, 0.0F).color(endColor);
                    consumer.vertex(matrix4f, x + width, y + height, 0.0F).color(endColor);
                    consumer.vertex(matrix4f, x + width, y, 0.0F).color(startColor);
                    consumer.vertex(matrix4f, x, y, 0.0F).color(startColor);
                    break;
                case RIGHT_LEFT:
                    consumer.vertex(matrix4f, x, y + height, 0.0F).color(endColor);
                    consumer.vertex(matrix4f, x + width, y + height, 0.0F).color(startColor);
                    consumer.vertex(matrix4f, x + width, y, 0.0F).color(startColor);
                    consumer.vertex(matrix4f, x, y, 0.0F).color(endColor);
                    break;
                case BOTTOM_TOP:
                    consumer.vertex(matrix4f, x, y + height, 0.0F).color(startColor);
                    consumer.vertex(matrix4f, x + width, y + height, 0.0F).color(startColor);
                    consumer.vertex(matrix4f, x + width, y, 0.0F).color(endColor);
                    consumer.vertex(matrix4f, x, y, 0.0F).color(endColor);
                    break;
            }
        });
    }

    public static void enableScissor(int x, int y, int width, int height) {
        enableScissor(x, y, width, height, mc.getWindow().getScaleFactor());
    }

    public static void enableScissor(WidgetBox box) {
        enableScissor((int) box.x, (int) box.y, (int) box.getWidth(), (int) box.getHeight(), mc.getWindow().getScaleFactor());
    }

    public static void enableScissor(int x, int y, int width, int height, double scaleFactor) {
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
     * @param x        X position of the rectangle
     * @param y        Y position of the rectangle
     * @param width    Width of the rectangle
     * @param height   Height of the rectangle
     * @param color    Color of the rectangle
     */
    public static void drawRectangle(DrawContext drawContext, float x, float y, float width, float height, int color) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer =  vcp.getBuffer(RenderLayer.getDebugQuads());

            consumer.vertex(matrix4f, x, y + height, 0.0F).color(color);
            consumer.vertex(matrix4f, x + width, y + height, 0.0F).color(color);
            consumer.vertex(matrix4f, x + width, y, 0.0F).color(color);
            consumer.vertex(matrix4f, x, y, 0.0F).color(color);
        });
    }

    /* ==== Drawing Rectangles ==== */

    /**
     * Draws a singular outline rectangle on screen with the given parameters
     *
     * @param x        X position of the rectangle
     * @param y        Y position of the rectangle
     * @param width    Width of the rectangle
     * @param height   Height of the rectangle
     * @param color    Color of the rectangle
     */
    public static void drawOutlineBox(DrawContext drawContext, float x, float y, float width, float height, float thickness, int color) {
        drawRectangle(drawContext, x, y, width, thickness, color);
        drawRectangle(drawContext, x, y + height - thickness, width, thickness, color);
        drawRectangle(drawContext, x, y + thickness, thickness, height - thickness * 2, color);
        drawRectangle(drawContext, x + width - thickness, y + thickness, thickness, height - thickness * 2, color);
    }

    /**
     * Draws a singular rectangle with a dark shadow on screen with the given parameters
     * Bad way because there is a better way
     *
     * @param x             X position of the rectangle
     * @param y             Y position of the rectangle
     * @param width         Width of the rectangle
     * @param height        Height of the rectangle
     * @param color         Color of the rectangle
     * @param shadowOpacity Opacity of the shadow (Dark --> Lighter)
     * @param shadowOffsetX X position Offset of the shadow from the main rectangle X pos
     * @param shadowOffsetY Y position Offset of the shadow from the main rectangle Y pos
     */
    public static void drawRectangleWithShadowBadWay(DrawContext drawContext, float x, float y, float width, float height, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawRectangle(drawContext, x + shadowOffsetX, y + shadowOffsetY, width, height, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the rectangle
        drawRectangle(drawContext, x, y, width, height, color);
    }

    /**
     * Draws an outline rounded rectangle
     *
     * @param x         X pos
     * @param y         Y pos
     * @param width     Width of rounded.fsh rectangle
     * @param height    Height of rounded.fsh rectangle
     * @param radius    Radius of the quadrants / the rounded.fsh rectangle
     * @param color     Color of the rounded.fsh rectangle
     * @param thickness thickness of the outline
     */
    public static void drawOutlineRoundedBox(DrawContext drawContext, float x, float y, float width, float height, float radius, float thickness, int color) {
        Color c = new Color(color, true);
        drawOutlineRoundedBox(drawContext,x,y,width,height,new Vector4f(radius),thickness,c,c,c,c);
    }

    public static void drawOutlineRoundedBox(DrawContext drawContext, float x, float y, float width, float height, Vector4f radii, float thickness,  Color tl, Color tr, Color br, Color bl) {
        if (width <= 0 || height <= 0) return;
        float maxRadius = Math.min(width, height) / 2;
        radii.set(Math.min(radii.x, maxRadius), // top-left
                Math.min(radii.y, maxRadius), // top-right
                Math.min(radii.z, maxRadius), // bottom-right
                Math.min(radii.w, maxRadius)  // bottom-left
        );
        drawContext.draw(vcp -> {
            VertexConsumer dvc =  vcp.getBuffer(CustomRenderLayers.ROUNDED_RECT_OUTLINE.apply(new CustomRenderLayers.OutlineParameters(radii, thickness ,new float[]{width, height})));
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

            dvc.vertex(matrix4f, x, y + height, 0).texture(0, 0).color(bl.getRGB());
            dvc.vertex(matrix4f, x + width, y + height, 0).texture(width, 0).color(br.getRGB());
            dvc.vertex(matrix4f, x + width, y, 0).texture(width, height).color(tr.getRGB());
            dvc.vertex(matrix4f, x, y, 0).texture(0, height).color(tl.getRGB());
        });
    }

    /**
     * Draw chroma text (text with a nice rainbow effect)
     *
     * @param drawContext A drawContext object
     * @param text        The text to display
     * @param x           X pos of text
     * @param y           Y pos of text
     * @param speed       Speed of rainbow
     * @param saturation  Saturation of the rainbow colors
     * @param brightness  Brightness of the rainbow colors
     * @param spread      How much the color difference should be between each character (ideally between 0.001 to 0.2)
     * @param shadow      Whether to render the text as shadow.
     */
    public static void drawChromaText(@NotNull DrawContext drawContext, String text, int x, int y, float speed, float saturation, float brightness, float spread, boolean shadow) {
        long time = System.currentTimeMillis();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float hue = (time % (int) (5000 / speed)) / (5000f / speed) + (i * spread); // Adjust the hue based on time and character position
            hue = MathHelper.floorMod(hue, 1.0f); //  hue should stay within the range [0, 1]

            // Convert the hue to an RGB color
            int color = Color.HSBtoRGB(hue, saturation, brightness);

            // Draw the character with the calculated color
            drawContext.drawText(mc.textRenderer, String.valueOf(text.charAt(i)), x + mc.textRenderer.getWidth(text.substring(0, i)), y, color, shadow);
        }
    }



    /* ====  Drawing filled and outline circles  ==== */

    /**
     * Draws an outline of a circle
     *
     * @param xCenter  X position of the circle outline
     * @param yCenter  Y position of the circle outline
     * @param radius   radius of the circle outline
     * @param color    color of the circle outline
     */
    public static void drawOutlineCircle(DrawContext drawContext, float xCenter, float yCenter, float radius, float lineWidth, int color) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getDebugLineStrip(lineWidth));

            for (int i = 0; i <= 360; i++) {
                double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
                double y = yCenter + Math.cos(Math.toRadians(i)) * radius;
                double x2 = xCenter + Math.sin(Math.toRadians(i)) * (radius + lineWidth);
                double y2 = yCenter + Math.cos(Math.toRadians(i)) * (radius + lineWidth);
                consumer.vertex(matrix4f, (float) x, (float) y, 0).color(color);
                consumer.vertex(matrix4f, (float) x2, (float) y2, 0).color(color);
            }
        });
    }

    /**
     * Draws a filled circle
     *
     * @param xCenter  X position of the circle outline
     * @param yCenter  Y position of the circle outline
     * @param radius   radius of the circle outline
     * @param color    color of the circle outline
     */
    public static void drawFilledCircle(DrawContext drawContext, float xCenter, float yCenter, float radius, int color) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getDebugTriangleFan());

            consumer.vertex(matrix4f, xCenter, yCenter, 0).color(color);

            for (int i = 0; i <= 360; i++) {
                double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
                double y = yCenter + Math.cos(Math.toRadians(i)) * radius;
                consumer.vertex(matrix4f, (float) x, (float) y, 0).color(color);
            }
        });
    }

    /**
     * Draws a filled circle with a shadow bad way
     *
     * @param xCenter       X position of the circle
     * @param yCenter       Y position of the circle
     * @param radius        Radius of the circle
     * @param color         Color of the circle
     * @param shadowOffsetX X position of the circle shadow offset from main circle
     * @param shadowOffsetY X position of the circle shadow offset from main circle
     * @param shadowOpacity Opacity of the circle shadow offset from main circle
     */
    public static void drawCircleWithShadow(DrawContext drawContext, float xCenter, float yCenter, float radius, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawFilledCircle(drawContext, xCenter + shadowOffsetX, yCenter + shadowOffsetY, radius, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the circle
        drawFilledCircle(drawContext, xCenter, yCenter, radius, color);
    }

    /**
     * Not Tested
     *
     * @param x
     * @param y
     * @param radius
     * @param startAngle
     * @param endAngle
     * @param color
     */
    @Deprecated
    public static void drawFilledArc(DrawContext drawContext, float x, float y, float radius, float startAngle, float endAngle, int color) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getDebugLineStrip(1.0f));

            for (float angle = startAngle; angle <= endAngle; angle += 1.0F) {
                float x1 = x + MathHelper.cos(angle * 0.017453292F) * radius;
                float y1 = y + MathHelper.sin(angle * 0.017453292F) * radius;
                float x2 = x + MathHelper.cos((angle + 1.0F) * 0.017453292F) * radius;
                float y2 = y + MathHelper.sin((angle + 1.0F) * 0.017453292F) * radius;

                consumer.vertex(matrix4f, x, y, 0).color(color);
                consumer.vertex(matrix4f, x1, y1, 0).color(color);
                consumer.vertex(matrix4f, x2, y2, 0).color(color);
            }
        });
    }
    /* ====  Drawing Quadrants, Arcs, and Triangles  ==== */

    /**
     * Draws a filled Gradient quadrant
     *
     * @param xCenter    X position of the quadrant
     * @param yCenter    Y position of the quadrant
     * @param radius     Radius of the quadrant
     * @param startColor start color of the gradient
     * @param endColor   end color of the gradient
     * @param quadrant   Integer value of the quadrant of the circle. 1 == Top Right, 2 == Top Left, 3 == Bottom Right, 4 == Bottom Left
     */
    public static void drawFilledGradientQuadrant(DrawContext drawContext, float xCenter, float yCenter, float radius, int startColor, int endColor, int quadrant) {
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;

        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;

        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getDebugTriangleFan());

            consumer.vertex(matrix4f, xCenter, yCenter, 0).color(startColor);

            for (int i = quadrant * 90; i <= quadrant * 90 + 90; i++) {
                double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
                double y = yCenter + Math.cos(Math.toRadians(i)) * radius;

                // Interpolate the color based on the angle
                float t = (float) (i - quadrant * 90) / 90.0f;
                float red = startRed * (1 - t) + endRed * t;
                float green = startGreen * (1 - t) + endGreen * t;
                float blue = startBlue * (1 - t) + endBlue * t;
                float alpha = startAlpha * (1 - t) + endAlpha * t;

                consumer.vertex(matrix4f, (float) x, (float) y, 0).color(red,green,blue,alpha);
            }
        });
    }

    /**
     * Draws an arc
     *
     * @param xCenter    X position of the arc's center
     * @param yCenter    Y position of the arc's center
     * @param radius     Radius of the arc's center circle
     * @param startAngle start Angle of the arc
     * @param endAngle   end Angle of the arc
     * @param thickness  Thickness of the arc (width of the arc)
     */
    public static void drawArc(DrawContext drawContext, float xCenter, float yCenter, float radius, float thickness, int color, int startAngle, int endAngle) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getDebugTriangleFan());

            for (int i = startAngle; i <= endAngle; i++) {
                double innerX = xCenter + Math.sin(Math.toRadians(i)) * (radius - thickness);
                double innerY = yCenter + Math.cos(Math.toRadians(i)) * (radius - thickness);
                double outerX = xCenter + Math.sin(Math.toRadians(i)) * radius;
                double outerY = yCenter + Math.cos(Math.toRadians(i)) * radius;

                consumer.vertex(matrix4f, (float) innerX, (float) innerY, 0).color(color);
                consumer.vertex(matrix4f, (float) outerX, (float) outerY, 0).color(color);
            }
        });
    }

    /**
     * Draws a filled quadrant
     *
     * @param xCenter  X position of the quadrant
     * @param yCenter  Y position of the quadrant
     * @param radius   Radius of the quadrant
     * @param color    color of the quadrant
     * @param quadrant Integer value of the quadrant of the circle. 1 == Top Right, 2 == Top Left, 3 == Bottom Right, 4 == Bottom Left
     */
    public static void drawFilledQuadrant(DrawContext drawContext, float xCenter, float yCenter, float radius, int color, int quadrant) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getDebugTriangleFan());

            consumer.vertex(matrix4f, xCenter, yCenter, 0).color(color);

            for (int i = quadrant * 90; i <= quadrant * 90 + 90; i++) {
                double x = xCenter + Math.sin(Math.toRadians(i)) * radius;
                double y = yCenter + Math.cos(Math.toRadians(i)) * radius;
                consumer.vertex(matrix4f, (float) x, (float) y, 0).color(color);
            }
        });
    }

    /**
     * Draws a Triangle with the given coordinates
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param color
     */
    public static void drawOutlineTriangle(DrawContext drawContext, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        drawContext.draw(vcp -> {
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getLines());

            consumer.vertex(matrix4f, x1, y1, 0).color(color);
            consumer.vertex(matrix4f, x2, y2, 0).color(color);
            consumer.vertex(matrix4f, x3, y3, 0).color(color);
            consumer.vertex(matrix4f, x1, y1, 0).color(color);
        });
    }

    /**
     * Draws a outline quadrant
     *
     * @param xCenter  X position of the quadrant
     * @param yCenter  Y position of the quadrant
     * @param radius   Radius of the quadrant
     * @param color    color of the quadrant
     * @param quadrant Integer value of the quadrant of the circle. 1 == Top Right, 2 == Top Left, 3 == Bottom Right, 4 == Bottom Left
     */
    public static void drawOutlineQuadrant(DrawContext drawContext, float xCenter, float yCenter, float radius, int quadrant, int color) {
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

        drawArc(drawContext, xCenter, yCenter, radius, 1f, color, startAngle, endAngle);
    }

    /**
     * Draws a filled rounded.fsh rectangle by drawing 1 main rectangle, 4 side rectangles, and 4 filled quadrants
     *
     * @param x        X pos
     * @param y        Y pos
     * @param width    Width of rounded.fsh rectangle
     * @param height   Height of rounded.fsh rectangle
     * @param radius   Radius of the quadrants / the rounded.fsh rectangle
     * @param color    Color of the rounded.fsh rectangle
     */
    public static void drawRoundedRectangle(DrawContext drawContext, float x, float y, float width, float height, float radius, int color) {
        drawRoundedRectangle(drawContext, x, y, true, true, true, true, width, height, radius, color);
    }

    /* ====  Drawing Rounded Rectangles  ==== */

    /**
     * Draws a filled rounded.fsh rectangle by drawing 1 main rectangle, 4 side rectangles, and specified filled quadrants
     *
     * @param x        X pos
     * @param y        Y pos
     * @param TL       Whether to draw the top left quadrant
     * @param TR       Whether to draw the top right quadrant
     * @param BL       Whether to draw the bottom left quadrant
     * @param BR       Whether to draw the bottom right quadrant
     * @param width    Width of rounded.fsh rectangle
     * @param height   Height of rounded.fsh rectangle
     * @param radius   Radius of the quadrants / the rounded.fsh rectangle
     * @param color    Color of the rounded.fsh rectangle
     */
    public static void drawRoundedRectangle(DrawContext drawContext, float x, float y, boolean TL, boolean TR, boolean BL, boolean BR, float width, float height, float radius, int color) {
        Vector4f radii = new Vector4f(TR ? radius : 0.0f, BR ? radius : 0.0f, TL ? radius : 0.0f, BL ? radius : 0.0f);

        // Turns out Color class takes rgb by default not rgba
        Color c = new Color(color, true);
        drawRoundedRectangle(drawContext,x,y,width, height, radii, c,c,c,c);
    }

    /**
     * Draws a rounded rectangle with customizable corner radii, corner colors, and selective corner rounding.
     * @param drawContext DrawContext for rendering
     * @param x X position
     * @param y Y position
     *
     *
     * @param width Width of the rectangle
     * @param height Height of the rectangle
     * @param radii Vector4f specifying radii for top-left, top-right, bottom-right, bottom-left corners
     */
    public static void drawRoundedRectangle(DrawContext drawContext, float x, float y, float width, float height,
                                            Vector4f radii, Color tl, Color tr, Color br, Color bl) {
        if (width <= 0 || height <= 0) return;
        float maxRadius = Math.min(width, height) / 2;
        radii.set(Math.min(radii.x, maxRadius), // top-left
                Math.min(radii.y, maxRadius), // top-right
                Math.min(radii.z, maxRadius), // bottom-right
                Math.min(radii.w, maxRadius)  // bottom-left
        );
        drawContext.draw(vcp -> {
            VertexConsumer dvc =  vcp.getBuffer(CustomRenderLayers.ROUNDED_RECT.apply(new CustomRenderLayers.RoundedParameters(radii, new float[]{width, height})));
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

            dvc.vertex(matrix4f, x, y + height, 0).texture(0, 0).color(bl.getRGB());
            dvc.vertex(matrix4f, x + width, y + height, 0).texture(width, 0).color(br.getRGB());
            dvc.vertex(matrix4f, x + width, y, 0).texture(width, height).color(tr.getRGB());
            dvc.vertex(matrix4f, x, y, 0).texture(0, height).color(tl.getRGB());
        });
    }

    /**
     * Draws an outline rounded.fsh gradient rectangle
     *
     * @param tl   is applied to the top-left vertex (x, y).
     * @param tr   is applied to the top-right vertex (x + width, y).
     * @param br   is applied to the bottom-right vertex (x + width, y + height).
     * @param bl   is applied to the bottom-left vertex (x, y + height).
     * @param x        X pos
     * @param y        Y pos
     * @param width    Width of rounded.fsh gradient rectangle
     * @param height   Height of rounded.fsh gradient rectangle
     * @param radius   Radius of the quadrants / the rounded.fsh gradient rectangle
     */
    public static void drawOutlineGradientRoundedBox(DrawContext drawContext, float x, float y, float width, float height, float radius, float thickness, Color tl, Color tr, Color br, Color bl) {
        drawOutlineRoundedBox(drawContext,x,y,width,height,new Vector4f(radius),thickness,tl, tr, br,bl);
    }

    public static void drawCutRectangle(DrawContext drawContext, int x1, int y1, int x2, int y2, int z, int color, int cornerRadius) {
        // Draw the rectangles
        drawContext.fill(x1 + cornerRadius, y1, x2 - cornerRadius, y1 + cornerRadius, z, color);
        drawContext.fill(x1 + cornerRadius, y2 - cornerRadius, x2 - cornerRadius, y2, z, color);
        drawContext.fill(x1, y1 + cornerRadius, x2, y2 - cornerRadius, z, color);
    }

    /**
     * Draws a rounded.fsh rectangle with a shadow in a bad way
     *
     * @param x             X pos
     * @param y             Y pos
     * @param width         Width of rounded.fsh rectangle
     * @param height        Height of rounded.fsh rectangle
     * @param radius        Radius of the quadrants / the rounded.fsh rectangle
     * @param color         Color of the rounded.fsh rectangle
     * @param shadowOpacity opacity of the shadow
     * @param shadowOffsetX X offset of the shadow
     * @param shadowOffsetY Y offset of the shadow
     */
    public static void drawRoundedRectangleWithShadowBadWay(DrawContext drawContext, float x, float y, float width, float height, float radius, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawRoundedRectangle(drawContext, x + shadowOffsetX, y + shadowOffsetY, width, height, radius, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the rounded.fsh rectangle
        drawRoundedRectangle(drawContext, x, y, width, height, radius, color);
    }

    /**
     * Draws a rounded.fsh gradient rectangle
     *
     * @param tl   is applied to the top-left vertex (x, y).
     * @param tr   is applied to the top-right vertex (x + width, y).
     * @param br   is applied to the bottom-right vertex (x + width, y + height).
     * @param bl   is applied to the bottom-left vertex (x, y + height).
     * @param x      X pos
     * @param y      Y pos
     * @param width  Width of rounded.fsh gradient rectangle
     * @param height Height of rounded.fsh gradient rectangle
     * @param radius Radius of the quadrants / the rounded.fsh gradient rectangle
     */
    public static void drawRoundedGradientRectangle(DrawContext drawContext, Color tl, Color tr, Color br, Color bl, float x, float y, float width, float height, float radius) {
        drawRoundedGradientRectangle(drawContext, tl,tr,br,bl, x, y, width, height, radius, true, true, true, true);
    }

    /**
     * Draws a rounded.fsh gradient rectangle
     *
     * @param tl   is applied to the top-left vertex (x, y).
     * @param tr   is applied to the top-right vertex (x + width, y).
     * @param br   is applied to the bottom-right vertex (x + width, y + height).
     * @param bl   is applied to the bottom-left vertex (x, y + height).
     * @param x      X pos
     * @param y      Y pos
     * @param width  Width of rounded.fsh gradient rectangle
     * @param height Height of rounded.fsh gradient rectangle
     * @param radius Radius of the quadrants / the rounded.fsh gradient rectangle
     */
    public static void drawRoundedGradientRectangle(DrawContext drawContext, Color tl, Color tr, Color br, Color bl, float x, float y, float width, float height, float radius, boolean TL, boolean TR, boolean BL, boolean BR) {
        drawRoundedRectangle(drawContext, x, y, width, height,
                new Vector4f(TR ? radius : 0.0f, BR ? radius : 0.0f, TL ? radius : 0.0f, BL ? radius : 0.0f),
                tl,tr,br,bl);
    }

    /* ==== Drawing Lines ==== */
    public static void drawVerticalLine(DrawContext drawContext, float x, float y1, float height, float thickness, int color) {
        drawRectangle(drawContext, x, y1, thickness, height, color);
    }

    public static void drawHorizontalLine(DrawContext drawContext, float x1, float width, float y, float thickness, int color) {
        drawRectangle(drawContext, x1, y, width, thickness, color);
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

    public static void unscaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, (float) (mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor()), (float) (mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor()), 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
    }

    public static void customScaledProjection(float scale) {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getFramebufferWidth() / scale, mc.getWindow().getFramebufferHeight() / scale, 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
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

    /**
     * From minecraft
     */
    public static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color) {
        int i = textRenderer.getWidth(text);
        int var10000 = startY + endY;
        Objects.requireNonNull(textRenderer);
        int j = (var10000 - 9) / 2 + 1;
        int k = endX - startX;
        int l;
        if (i > k) {
            l = i - k;
            double d = (double) Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
            double g = MathHelper.lerp(f, 0.0, l);
            context.enableScissor(startX, startY, endX, endY);
            context.drawTextWithShadow(textRenderer, text, startX - (int) g, j, color);
            context.disableScissor();
        } else {
            l = MathHelper.clamp(centerX, startX + i / 2, endX - i / 2);
            context.drawCenteredTextWithShadow(textRenderer, text, l, j, color);
        }

    }

    public enum Direction {
        /* LEFT_RIGHT means from left to right. Same for others */
        LEFT_RIGHT, TOP_BOTTOM, RIGHT_LEFT, BOTTOM_TOP
    }

}