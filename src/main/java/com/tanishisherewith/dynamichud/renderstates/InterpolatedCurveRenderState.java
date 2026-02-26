package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record InterpolatedCurveRenderState(
        List<float[]> points,
        float thickness,
        int color,
        Matrix3x2fc pose,
        RenderPipeline pipeline,
        @Nullable ScreenRectangle scissorArea
) implements GuiElementRenderState {

    @Override
    public void buildVertices(@NonNull VertexConsumer consumer) {
        consumer.addVertexWith2DPose(pose, 10, 10).setColor(color);
        consumer.addVertexWith2DPose(pose, 100, 10).setColor(color);
        consumer.addVertexWith2DPose(pose, 10, 20).setColor(color);
        consumer.addVertexWith2DPose(pose, 100, 20).setColor(color);
        /*
        for (int i = 0; i < points.size(); i++) {
            float[] point = points.get(i);
            float x = point[0];
            float y = point[1];

            float dx = (i < points.size() - 1) ? points.get(i + 1)[0] - x : x - points.get(i - 1)[0];
            float dy = (i < points.size() - 1) ? points.get(i + 1)[1] - y : y - points.get(i - 1)[1];
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length == 0) continue;

            float offsetX = (thickness * 0.5f * dy) / length;
            float offsetY = (thickness * 0.5f * -dx) / length;

            consumer.addVertexWith2DPose(pose, x + offsetX, y + offsetY).setColor(color);
            consumer.addVertexWith2DPose(pose, x - offsetX, y - offsetY).setColor(color);
        }

         */
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
