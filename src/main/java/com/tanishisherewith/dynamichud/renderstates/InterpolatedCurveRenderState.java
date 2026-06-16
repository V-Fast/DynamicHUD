package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record InterpolatedCurveRenderState(
        List<float[]> points,
        float thickness,
        int color,
        Matrix3x2f pose,
        RenderPipeline pipeline,
        int width,
        int height,
        @Nullable ScreenRectangle scissorArea
) implements GuiElementRenderState {

    @Override
    public void buildVertices(@NonNull VertexConsumer consumer) {
        consumer.setLineWidth(thickness);
        if (points.size() < 2) return;

        //  build individual QUADS
        for (int i = 0; i < points.size() - 1; i++) {
            float[] p1 = points.get(i);
            float[] p2 = points.get(i + 1);

            float x1 = p1[0];
            float y1 = p1[1];
            float x2 = p2[0];
            float y2 = p2[1];

            float dx = x2 - x1;
            float dy = y2 - y1;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length == 0) continue;

            // normals for line thickness
            float offsetX = (thickness * 0.5f * dy) / length;
            float offsetY = (thickness * 0.5f * -dx) / length;

            //Topleft
            consumer.addVertexWith2DPose(pose, x1 + offsetX, y1 + offsetY).setColor(color);
            //Bottomleft
            consumer.addVertexWith2DPose(pose, x1 - offsetX, y1 - offsetY).setColor(color);
            //Bottomright
            consumer.addVertexWith2DPose(pose, x2 - offsetX, y2 - offsetY).setColor(color);
            //Topright
            consumer.addVertexWith2DPose(pose, x2 + offsetX, y2 + offsetY).setColor(color);
        }
    }

    @Override
    public @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        if (points.isEmpty()) return null;
        return DrawHelper.createBounds(pose, scissorArea, points.getFirst()[0], points.getFirst()[1], width, height);
    }
}