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
        int numPoints = points.size();
        if (numPoints < 2) return;

        float halfThickness = thickness * 0.5f;

        // Pre-calculate continuous smooth joint normals
        float[] extX = new float[numPoints];
        float[] extY = new float[numPoints];

        for (int i = 0; i < numPoints; i++) {
            float dx, dy;
            if (i == 0) {
                dx = points.get(1)[0] - points.get(0)[0];
                dy = points.get(1)[1] - points.get(0)[1];
            } else if (i == numPoints - 1) {
                dx = points.get(i)[0] - points.get(i - 1)[0];
                dy = points.get(i)[1] - points.get(i - 1)[1];
            } else {
                // Blend vector headings from incoming and outgoing segments
                float dx1 = points.get(i)[0] - points.get(i - 1)[0];
                float dy1 = points.get(i)[1] - points.get(i - 1)[1];
                float dx2 = points.get(i + 1)[0] - points.get(i)[0];
                float dy2 = points.get(i + 1)[1] - points.get(i)[1];

                float len1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
                float len2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

                if (len1 > 0) { dx1 /= len1; dy1 /= len1; }
                if (len2 > 0) { dx2 /= len2; dy2 /= len2; }

                dx = dx1 + dx2;
                dy = dy1 + dy2;
            }

            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length > 0) {
                // Preserves exact original winding orientation to completely prevent culling
                extX[i] = (dy / length) * halfThickness;
                extY[i] = (-dx / length) * halfThickness;
            } else {
                extX[i] = 0;
                extY[i] = 0;
            }
        }

        // Stitch the continuous mesh strip
        for (int i = 0; i < numPoints - 1; i++) {
            float[] p1 = points.get(i);
            float[] p2 = points.get(i + 1);

            // Winding pattern: TopLeft -> BottomLeft -> BottomRight -> TopRight
            consumer.addVertexWith2DPose(pose, p1[0] + extX[i], p1[1] + extY[i]).setColor(color);
            consumer.addVertexWith2DPose(pose, p1[0] - extX[i], p1[1] - extY[i]).setColor(color);
            consumer.addVertexWith2DPose(pose, p2[0] - extX[i + 1], p2[1] - extY[i + 1]).setColor(color);
            consumer.addVertexWith2DPose(pose, p2[0] + extX[i + 1], p2[1] + extY[i + 1]).setColor(color);
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