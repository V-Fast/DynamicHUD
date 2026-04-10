package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.NonNull;

import java.util.List;

//TODO: just use fillGradient in GuiGraphics
public record GradientShadowRenderState(
        List<float[]> points,
        float bottomY,
        int startColor,
        int endColor,
        Matrix3x2fStack pose,
        RenderPipeline pipeline,
        int width,
        int height,
        @Nullable ScreenRectangle scissorArea
) implements GuiElementRenderState {

    @Override
    public void buildVertices(@NonNull VertexConsumer consumer) {
        for (float[] point : points) {
            float x = point[0];
            float y = point[1];

            consumer.addVertexWith2DPose(pose, x, y).setColor(startColor);
            consumer.addVertexWith2DPose(pose, x, bottomY).setColor(endColor);
        }
    }
    @Override
    public @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return DrawHelper.createBounds(pose,scissorArea,points.getFirst()[0],points.getFirst()[1],width,height);
    }
}