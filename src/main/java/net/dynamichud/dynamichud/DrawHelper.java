package net.dynamichud.dynamichud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class DrawHelper extends DrawableHelper {
    public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
    }

    public static void drawTextWithShadow(MatrixStack matrices,
                                          TextRenderer textRenderer,
                                          String text,
                                          int x,
                                          int y,
                                          int color) {
        textRenderer.drawWithShadow(matrices, text, x, y, color);
    }
    public static void drawText(MatrixStack matrices,
                                          TextRenderer textRenderer,
                                          String text,
                                          int x,
                                          int y,
                                          int color) {
        textRenderer.draw(matrices, text, x, y, color);
    }
    public static void fillRoundedRect(Matrix4f matrix4f, int x1, int y1, int x2, int y2, int cornerRadius, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Draw the center rectangle
        bufferBuilder.vertex(matrix4f, x1 + cornerRadius, y2 - cornerRadius, 0).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix4f, x2 - cornerRadius, y2 - cornerRadius, 0).color(red, green, blue,alpha).next();
        bufferBuilder.vertex(matrix4f,x2 - cornerRadius,y1 + cornerRadius ,0).color(red ,green ,blue ,alpha).next();
        bufferBuilder.vertex(matrix4f,x1 + cornerRadius,y1 + cornerRadius ,0).color(red ,green ,blue ,alpha).next();

        // Draw the side rectangles
        bufferBuilder.vertex(matrix4f,x1,y1 + cornerRadius ,0).color(red ,green ,blue ,alpha).next();
        bufferBuilder.vertex(matrix4f,x1 + cornerRadius,y1 + cornerRadius ,0).color(red ,green ,blue ,alpha).next();
        bufferBuilder.vertex(matrix4f,x1 + cornerRadius,y2 - cornerRadius ,0).color(red ,green ,blue ,alpha).next();
        bufferBuilder.vertex(matrix4f,x1,y2 - cornerRadius ,0).color(red ,green ,blue ,alpha).next();

        bufferBuilder.vertex(matrix4f,x2,y1 + cornerRadius ,0).color(red ,green ,blue ,alpha).next();
        bufferBuilder.vertex(matrix4f,x2 - cornerRadius,y1 + cornerRadius ,0).color(red ,green ,blue ,alpha).next();
        bufferBuilder.vertex(matrix4f,x2 - cornerRadius,y2 - cornerRadius ,0).color(red ,green ,blue,alpha ).next();
        bufferBuilder.vertex(matrix4f,x2,y2 - cornerRadius ,0).color(red ,green ,blue,alpha ).next();


        // Draw the rounded corners
        for (int i = 0; i <= 90; i += 5) {
            double angle = Math.toRadians(i);
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            bufferBuilder.vertex(matrix4f,(float)(x1 + cornerRadius * (1 - cos)),(float)(y1 + cornerRadius * (1 - sin)),0 ).color(red, green, blue,alpha ).next();
            bufferBuilder.vertex(matrix4f,(float)(x1 + cornerRadius * (1 - cos)),(float)(y2 - cornerRadius * (1 - sin)),0 ).color(red, green, blue,alpha ).next();
            bufferBuilder.vertex(matrix4f,(float)(x2 - cornerRadius * (1 - cos)),(float)(y2 - cornerRadius * (1 - sin)),0 ).color(red, green, blue,alpha ).next();
            bufferBuilder.vertex(matrix4f,(float)(x2 - cornerRadius * (1 - cos)),(float)(y1 + cornerRadius * (1 - sin)),0 ).color(red, green, blue,alpha ).next();
        }
        tessellator.draw();
        RenderSystem.disableBlend();
    }
}
