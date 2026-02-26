package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

// State for Geometric Shapes (Circles, Arcs, Fans)
public record GeometryRenderState(
        RenderPipeline pipeline,
        Matrix3x2fStack pose,
        float[] vertices, // Flat array: [x1, y1, x2, y2, ...]
        int[] colors,     // Parallel array of ARGB colors
        @Nullable ScreenRectangle scissorArea
) implements GuiElementRenderState {

    public record VertexData(float x, float y, int color) {}

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
        return null;
    }
}