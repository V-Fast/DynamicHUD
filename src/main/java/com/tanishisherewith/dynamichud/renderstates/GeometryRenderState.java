package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

// State for Geometric Shapes (Circles, Arcs, Fans)
public record GeometryRenderState(
        RenderPipeline pipeline,
        Matrix3x2f pose,
        float[] vertices, // Flat array: [x1, y1, x2, y2, ...]
        int[] colors,     // Parallel array of ARGB colors
        @Nullable ScreenRectangle scissorArea
) implements GuiElementRenderState {

    @Override
    public void buildVertices(VertexConsumer consumer) {
        for (int i = 0; i < vertices.length / 2; i++) {
            consumer.addVertexWith2DPose(pose, vertices[i * 2], vertices[i * 2 + 1])
                    .setColor(colors[i]);
        }
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        if (vertices == null || vertices.length < 2) return null;

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        for (int i = 0; i < vertices.length / 2; i++) {
            float x = vertices[i * 2];
            float y = vertices[i * 2 + 1];

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        return DrawHelper.createBounds(pose, scissorArea, minX, minY, maxX - minX, maxY - minY);
    }
}