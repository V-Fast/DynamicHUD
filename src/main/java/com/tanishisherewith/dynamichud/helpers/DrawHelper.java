package com.tanishisherewith.dynamichud.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.renderstates.GeometryRenderState;
import com.tanishisherewith.dynamichud.renderstates.QuadColorRectRenderState;
import com.tanishisherewith.dynamichud.renderstates.RoundedRectRenderState;
import com.tanishisherewith.dynamichud.utils.CustomRenderLayers;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Vector4f;

import java.awt.*;
import java.util.Arrays;
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
    public static void drawGradient(GuiGraphics g, float x, float y, float width, float height, int startColor, int endColor, Direction direction) {
            int[] c = switch(direction) {
                case TOP_BOTTOM -> new int[]{startColor, startColor, endColor, endColor};
                case LEFT_RIGHT -> new int[]{startColor, endColor, endColor, startColor};
                case RIGHT_LEFT -> new int[]{endColor, startColor, startColor, endColor};
                case BOTTOM_TOP -> new int[]{endColor, endColor, startColor, startColor};
            };

            g.guiRenderState.submitGuiElement(
                    new QuadColorRectRenderState(RenderPipelines.GUI,g.pose(),x,y,width,height,c,
                            new ScreenRectangle((int) x, (int) y,(int)width,(int) height),
                            g.scissorStack.peek())
            );
    }

    public static void enableScissor(int x, int y, int width, int height, GuiGraphics graphics) {
        enableScissor(x, y, width, height, mc.getWindow().getGuiScale(),graphics);
    }

    public static void enableScissor(WidgetBox box,GuiGraphics graphics) {
        enableScissor((int) box.x, (int) box.y, (int) box.getWidth(), (int) box.getHeight(), mc.getWindow().getGuiScale(),graphics);
    }

    public static void enableScissor(int x, int y, int width, int height, double scaleFactor, GuiGraphics graphics) {
        int scissorX = (int) (x * scaleFactor);
        int scissorY = (int) (y * scaleFactor);
        int scissorWidth = (int) (width * scaleFactor);
        int scissorHeight = (int) (height * scaleFactor);

        ScreenRectangle rect = new ScreenRectangle(x, y, width, height);
        graphics.scissorStack.push(rect);
    }


    public static void disableScissor(GuiGraphics graphics) {
        graphics.disableScissor();
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
    public static void drawRectangle(GuiGraphics graphics, float x, float y, float width, float height, int color) {
        drawGradient(graphics,x,y,width,height,color,color,Direction.LEFT_RIGHT);
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
    public static void drawOutlineBox(GuiGraphics graphics, float x, float y, float width, float height, float thickness, int color) {
        drawRectangle(graphics, x, y, width, thickness, color);
        drawRectangle(graphics, x, y + height - thickness, width, thickness, color);
        drawRectangle(graphics, x, y + thickness, thickness, height - thickness * 2, color);
        drawRectangle(graphics, x + width - thickness, y + thickness, thickness, height - thickness * 2, color);
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
    public static void drawRectangleWithShadowBadWay(GuiGraphics graphics, float x, float y, float width, float height, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawRectangle(graphics, x + shadowOffsetX, y + shadowOffsetY, width, height, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the rectangle
        drawRectangle(graphics, x, y, width, height, color);
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
    public static void drawOutlineRoundedBox(GuiGraphics graphics, float x, float y, float width, float height, float radius, float thickness, int color) {
        Color c = new Color(color, true);
        drawOutlineRoundedBox(graphics,x,y,width,height,new Vector4f(radius),thickness,c,c,c,c);
    }

    public static void drawOutlineRoundedBox(GuiGraphics graphics, float x, float y, float width, float height, Vector4f radii, float thickness,  Color tl, Color tr, Color br, Color bl) {
        if (width <= 0 || height <= 0) return;
        float maxRadius = Math.min(width, height) / 2;
        radii.set(Math.min(radii.x, maxRadius), // top-left
                Math.min(radii.y, maxRadius), // top-right
                Math.min(radii.z, maxRadius), // bottom-right
                Math.min(radii.w, maxRadius)  // bottom-left
        );
        int[] intColors = {tl.getRGB(),tr.getRGB(),br.getRGB(),bl.getRGB()};

        graphics.guiRenderState.submitGuiElement(new RoundedRectRenderState(
                CustomRenderLayers.ROUNDED_RECT_OUTLINE,
                graphics.pose(),
                x, y, width, height, thickness, intColors, radii, graphics.scissorStack.peek(),
                new ScreenRectangle(new ScreenPosition((int) x, (int) y), (int) width, (int) height)
        ));
    }

    /**
     * Draw chroma Component (Component with a nice rainbow effect)
     *
     * @param graphics A graphics object
     * @param Component        The Component to display
     * @param x           X pos of Component
     * @param y           Y pos of Component
     * @param speed       Speed of rainbow
     * @param saturation  Saturation of the rainbow colors
     * @param brightness  Brightness of the rainbow colors
     * @param spread      How much the color difference should be between each character (ideally between 0.001 to 0.2)
     * @param shadow      Whether to render the Component as shadow.
     */
    public static void drawChromaText(@NotNull GuiGraphics graphics, String Component, int x, int y, float speed, float saturation, float brightness, float spread, boolean shadow) {
        long time = System.currentTimeMillis();
        int length = Component.length();

        for (int i = 0; i < length; i++) {
            float hue = (time % (int) (5000 / speed)) / (5000f / speed) + (i * spread); // Adjust the hue based on time and character position
            hue = floorMod(hue, 1.0f); //  hue should stay within the range [0, 1]

            // Convert the hue to an RGB color
            int color = Color.HSBtoRGB(hue, saturation, brightness);

            // Draw the character with the calculated color
            graphics.drawString(mc.font, String.valueOf(Component.charAt(i)), x + mc.font.width(Component.substring(0, i)), y, color, shadow);
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
    public static void drawOutlineCircle(GuiGraphics graphics, float xCenter, float yCenter, float radius, float lineWidth, int color) {
        int segments = 72; // 5-degree steps
        float[] verts = new float[(segments + 1) * 4];
        int[] colors = new int[(segments + 1) * 2];
        Arrays.fill(colors, color);

        for (int i = 0; i <= segments; i++) {
            float rad = (float) Math.toRadians(i * 5);
            float sin = (float) Math.sin(rad);
            float cos = (float) Math.cos(rad);


            int base = i * 4;
            verts[base] = xCenter + sin * radius;
            verts[base + 1] = yCenter + cos * radius;
            verts[base + 2] = xCenter + sin * (radius + lineWidth);
            verts[base + 3] = yCenter + cos * (radius + lineWidth);
        }

        graphics.guiRenderState.submitGuiElement(new GeometryRenderState(
                CustomRenderLayers.TRIANGLE_STRIP,
                graphics.pose(),
                verts, colors, graphics.scissorStack.peek()
        ));
    }

    /**
     * Draws a filled circle
     *
     * @param xCenter  X position of the circle outline
     * @param yCenter  Y position of the circle outline
     * @param radius   radius of the circle outline
     * @param color    color of the circle outline
     */
    public static void drawFilledCircle(GuiGraphics graphics, float xCenter, float yCenter, float radius, int color) {
        int segments = 72; // 5-degree steps for smoothness
        float[] verts = new float[(segments + 2) * 2];
        int[] colors = new int[segments + 2];

        // Center point
        verts[0] = xCenter; verts[1] = yCenter;
        colors[0] = color;

        for (int i = 0; i <= segments; i++) {
            float rad = (float) Math.toRadians(i * 5);
            int idx = (i + 1) * 2;
            verts[idx] = xCenter + (float) Math.sin(rad) * radius;
            verts[idx + 1] = yCenter + (float) Math.cos(rad) * radius;
            colors[i + 1] = color;
        }

        graphics.guiRenderState.submitGuiElement(new GeometryRenderState(
                CustomRenderLayers.TRIANGLE_FAN_CUSTOM_BLEND,
                graphics.pose(),
                verts, colors, graphics.scissorStack.peek()
        ));
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
    public static void drawCircleWithShadow(GuiGraphics graphics, float xCenter, float yCenter, float radius, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawFilledCircle(graphics, xCenter + shadowOffsetX, yCenter + shadowOffsetY, radius, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the circle
        drawFilledCircle(graphics, xCenter, yCenter, radius, color);
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
    public static void drawFilledGradientQuadrant(GuiGraphics graphics, float xCenter, float yCenter, float radius, int startColor, int endColor, int quadrant) {
        int segments = 18; // 90 degrees / 5
        float[] verts = new float[(segments + 2) * 2];
        int[] colors = new int[segments + 2];

        verts[0] = xCenter; verts[1] = yCenter;
        colors[0] = startColor;

        for (int i = 0; i <= segments; i++) {
            float angle = (quadrant * 90) + (i * 5);
            float rad = (float) Math.toRadians(angle);

            int idx = (i + 1) * 2;
            verts[idx] = xCenter + (float) Math.sin(rad) * radius;
            verts[idx + 1] = yCenter + (float) Math.cos(rad) * radius;
            colors[i + 1] = ARGB.linearLerp((float) i / segments, startColor, endColor);
        }

        graphics.guiRenderState.submitGuiElement(new GeometryRenderState(
                CustomRenderLayers.TRIANGLE_FAN_CUSTOM_BLEND,
                graphics.pose(),
                verts, colors, graphics.scissorStack.peek()
        ));
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
    public static void drawArc(GuiGraphics graphics, float xCenter, float yCenter, float radius, float thickness, int color, int startAngle, int endAngle) {
        int segments = Math.max(1, (endAngle - startAngle) / 5);
        float[] verts = new float[(segments + 1) * 4];
        int[] colors = new int[(segments + 1) * 2];
        Arrays.fill(colors, color);

        for (int i = 0; i <= segments; i++) {
            int currentAngle = startAngle + (i * 5);
            if (currentAngle > endAngle) currentAngle = endAngle;

            float rad = (float) Math.toRadians(currentAngle);
            float sin = (float) Math.sin(rad);
            float cos = (float) Math.cos(rad);

            int base = i * 4;
            // Inner Vertex
            verts[base] = xCenter + sin * (radius - thickness);
            verts[base + 1] = yCenter + cos * (radius - thickness);
            // Outer Vertex
            verts[base + 2] = xCenter + sin * radius;
            verts[base + 3] = yCenter + cos * radius;
        }

        graphics.guiRenderState.submitGuiElement(new GeometryRenderState(
                CustomRenderLayers.TRIANGLE_FAN_CUSTOM_BLEND,
                graphics.pose(),
                verts, colors, graphics.scissorStack.peek()
        ));
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
    public static void drawFilledQuadrant(GuiGraphics graphics, float xCenter, float yCenter, float radius, int color, int quadrant) {
        drawFilledGradientQuadrant(graphics, xCenter, yCenter, radius, color, color, quadrant);
    }

    /**
     * Draws a Triangle with the given coordinates
     */
    public static void drawOutlineTriangle(GuiGraphics graphics, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        // 3 lines require 6 vertices (1-2, 2-3, 3-1) to form a closed loop
        float[] vertices = {
                (float)x1, (float)y1, (float)x2, (float)y2, // Line 1
                (float)x2, (float)y2, (float)x3, (float)y3, // Line 2
                (float)x3, (float)y3, (float)x1, (float)y1  // Line 3
        };

        int[] colors = new int[6];
        java.util.Arrays.fill(colors, color);

        graphics.guiRenderState.submitGuiElement(new GeometryRenderState(
                CustomRenderLayers.COLOR_LINE,
                graphics.pose(),
                vertices,
                colors,
                graphics.scissorStack.peek()
        ));
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
    public static void drawOutlineQuadrant(GuiGraphics graphics, float xCenter, float yCenter, float radius, int quadrant, int color) {
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

        drawArc(graphics, xCenter, yCenter, radius, 1f, color, startAngle, endAngle);
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
    public static void drawRoundedRectangle(GuiGraphics graphics, float x, float y, float width, float height, float radius, int color) {
        drawRoundedRectangle(graphics, x, y, true, true, true, true, width, height, radius, color);
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
    public static void drawRoundedRectangle(GuiGraphics graphics, float x, float y, boolean TL, boolean TR, boolean BL, boolean BR, float width, float height, float radius, int color) {
        Vector4f radii = new Vector4f(TR ? radius : 0.0f, BR ? radius : 0.0f, TL ? radius : 0.0f, BL ? radius : 0.0f);

        // Turns out Color class takes rgb by default not rgba
        Color c = new Color(color, true);
        drawRoundedRectangle(graphics,x,y,width, height, radii, c,c,c,c);
    }

    /**
     * Draws a rounded rectangle with customizable corner radii, corner colors, and selective corner rounding.
     * @param graphics GuiGraphics for rendering
     * @param x X position
     * @param y Y position
     *
     *
     * @param width Width of the rectangle
     * @param height Height of the rectangle
     * @param radii Vector4f specifying radii for top-left, top-right, bottom-right, bottom-left corners
     */
    public static void drawRoundedRectangle(GuiGraphics graphics, float x, float y, float width, float height,
                                            Vector4f radii, Color tl, Color tr, Color br, Color bl) {
        if (width <= 0 || height <= 0) return;
        float maxRadius = Math.min(width, height) / 2;
        radii.set(Math.min(radii.x, maxRadius), // top-left
                Math.min(radii.y, maxRadius), // top-right
                Math.min(radii.z, maxRadius), // bottom-right
                Math.min(radii.w, maxRadius)  // bottom-left
        );
        int[] intColors = {tl.getRGB(),tr.getRGB(),br.getRGB(),bl.getRGB()};

        graphics.guiRenderState.submitGuiElement(new RoundedRectRenderState(
                CustomRenderLayers.ROUNDED_RECT,
                graphics.pose(),
                x, y, width, height, 0f, intColors, radii, graphics.scissorStack.peek(),
                new ScreenRectangle(new ScreenPosition((int) x, (int) y), (int) width, (int) height)
        ));
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
    public static void drawOutlineGradientRoundedBox(GuiGraphics graphics, float x, float y, float width, float height, float radius, float thickness, Color tl, Color tr, Color br, Color bl) {
        drawOutlineRoundedBox(graphics,x,y,width,height,new Vector4f(radius),thickness,tl, tr, br,bl);
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
    public static void drawRoundedRectangleWithShadowBadWay(GuiGraphics graphics, float x, float y, float width, float height, float radius, int color, int shadowOpacity, float shadowOffsetX, float shadowOffsetY) {
        // First, render the shadow
        drawRoundedRectangle(graphics, x + shadowOffsetX, y + shadowOffsetY, width, height, radius, ColorHelper.getColor(0, 0, 0, shadowOpacity));

        // Then, render the rounded.fsh rectangle
        drawRoundedRectangle(graphics, x, y, width, height, radius, color);
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
    public static void drawRoundedGradientRectangle(GuiGraphics graphics, Color tl, Color tr, Color br, Color bl, float x, float y, float width, float height, float radius) {
        drawRoundedGradientRectangle(graphics, tl,tr,br,bl, x, y, width, height, radius, true, true, true, true);
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
    public static void drawRoundedGradientRectangle(GuiGraphics graphics, Color tl, Color tr, Color br, Color bl, float x, float y, float width, float height, float radius, boolean TL, boolean TR, boolean BL, boolean BR) {
        drawRoundedRectangle(graphics, x, y, width, height,
                new Vector4f(TR ? radius : 0.0f, BR ? radius : 0.0f, TL ? radius : 0.0f, BL ? radius : 0.0f),
                tl,tr,br,bl);
    }

    /* ==== Drawing Lines ==== */
    public static void drawVerticalLine(GuiGraphics graphics, float x, float y1, float height, float thickness, int color) {
        drawRectangle(graphics, x, y1, thickness, height, color);
    }

    public static void drawHorizontalLine(GuiGraphics graphics, float x1, float width, float y, float thickness, int color) {
        drawRectangle(graphics, x1, y, width, thickness, color);
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
    public static void drawOutlinedBox(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y1 + 1, color);
        graphics.fill(x1, y2 - 1, x2, y2, color);
        graphics.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
        graphics.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    public static void unscaledProjection() {
        //RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
    }

    public static void scaledProjection() {
        //RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, (float) (mc.getWindow().getWidth() / mc.getWindow().getGuiScale()), (float) (mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor()), 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
    }

    public static void customScaledProjection(float scale) {
        //RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getWidth() / scale, mc.getWindow().getHeight() / scale, 0, 1000, 21000), ProjectionType.ORTHOGRAPHIC);
    }

    /**
     * This method assumes that the x, y coords are the origin of a widget.
     *
     * @param x     X position of widget
     * @param y     Y position of widget
     * @param scale Scale the matrices
     */
    public static void scaleAndPosition(Matrix3x2fStack matrices, float x, float y, float scale) {
        matrices.pushMatrix(); // Save the current transformation state

        // Translate the origin back to the desired position
        matrices.translate(x, y);

        // Scale the matrix
        matrices.scale(scale, scale);

        matrices.translate(-x, -y);
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
    public static void scaleAndPosition(Matrix3x2fStack matrices, float x, float y, float width, float height, float scale) {
        matrices.pushMatrix(); // Save the current transformation state

        // Translate the origin back to the desired position
        matrices.translate(x + width / 2.0f, y + height / 2.0f);

        // Scale the matrix
        matrices.scale(scale, scale);

        matrices.translate(-(x + width / 2.0f), -(y + height / 2.0f));
    }

    public static void stopScaling(Matrix3x2fStack matrices) {
        matrices.popMatrix(); // Restore the previous transformation state
    }

    /**
     * From minecraft
     */
    public static void drawScrollableText(GuiGraphics graphics, Font font, Component Component, int centerX, int startX, int startY, int endX, int endY, int color) {
        int i = font.width(Component);
        int var10000 = startY + endY;
        Objects.requireNonNull(font);
        int j = (var10000 - 9) / 2 + 1;
        int k = endX - startX;
        int l;
        if (i > k) {
            l = i - k;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
            double g = org.joml.Math.lerp(f, 0.0, l);
            graphics.enableScissor(startX, startY, endX, endY);
            graphics.drawString(font, Component, startX - (int) g, j, color,true);
            graphics.disableScissor();
        } else {
            l = Math.clamp(centerX, startX + i / 2, endX - i / 2);
            graphics.drawCenteredString(font, Component, l, j, color);
        }

    }

    public static float floorMod(float x, float y) {
        return x - y * (float) Math.floor(x / y);
    }

    public enum Direction {
        /* LEFT_RIGHT means from left to right. Same for others */
        LEFT_RIGHT, TOP_BOTTOM, RIGHT_LEFT, BOTTOM_TOP
    }

}