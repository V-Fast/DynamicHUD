package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

import static com.tanishisherewith.dynamichud.helpers.DrawHelper.COSA;
import static com.tanishisherewith.dynamichud.helpers.DrawHelper.SINA;

public record RoundedRectRenderState(
        RenderPipeline pipeline,
        Matrix3x2fStack pose, // Using Matrix3x2fc for 2D optimization
        float x, float y,
        float width, float height,
        float thickness,
        int[] colors,       // [TL, BL, BR, TR]
        Vector4f roundness, // [x=TL, y=TR, z=BR, w=BL]
        @Nullable ScreenRectangle scissorArea
) implements GuiElementRenderState {

        @Override
    public void buildVertices(@NonNull VertexConsumer consumer) {
        if (thickness > 0) {
            drawContinuousOutline(consumer);
        } else {
            drawFill(consumer);
        }
    }

    private int averageColors(int[] c) {
        int r = 0, g = 0, b = 0, a = 0;
        for (int color : c) {
            a += (color >> 24) & 0xFF;
            r += (color >> 16) & 0xFF;
            g += (color >> 8) & 0xFF;
            b += color & 0xFF;
        }
        return (a / 4 << 24) | (r / 4 << 16) | (g / 4 << 8) | (b / 4);
    }

    private void drawFill(VertexConsumer consumer) {
        float cx = x + (width / 2.0f);
        float cy = y + (height / 2.0f);
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        // Start Fan at center
        consumer.addVertexWith2DPose(pose, cx, cy).setColor(averageColors(colors));

        // 1. Bottom-Right (Original i=0-9)
        // Original logic: x0 = cx + (0.5f * dx), y0 = cy + (0.5f * dy)
        float brRadius = roundness.z;
        float dx = width - (brRadius * 2); // This is localized for the specific corner
        float dy = height - (brRadius * 2);

        float x0 = cx + (0.5f * (width - brRadius * 2));
        float y0 = cy + (0.5f * (height - brRadius * 2));

        if (brRadius > 0) {
            for (int i = 0; i < 9; i++) {
                // Using + for SINA to match your original: y = y0 + (r * sina[i])
                consumer.addVertexWith2DPose(pose, x0 + (brRadius * COSA[i]), y0 + (brRadius * SINA[i])).setColor(colors[2]);
            }
        } else {
            consumer.addVertexWith2DPose(pose, cx + halfWidth, cy + halfHeight).setColor(colors[2]);
        }

        // 2. Bottom-Left (Original i=9-18)
        float blRadius = roundness.w;
        x0 = cx - (0.5f * (width - blRadius * 2));
        y0 = cy + (0.5f * (height - blRadius * 2));
        if (blRadius > 0) {
            for (int i = 9; i < 18; i++) {
                consumer.addVertexWith2DPose(pose, x0 + (blRadius * COSA[i]), y0 + (blRadius * SINA[i])).setColor(colors[1]);
            }
        } else {
            consumer.addVertexWith2DPose(pose, cx - halfWidth, cy + halfHeight).setColor(colors[1]);
        }

        // 3. Top-Left (Original i=18-27)
        float tlRadius = roundness.x;
        x0 = cx - (0.5f * (width - tlRadius * 2));
        y0 = cy - (0.5f * (height - tlRadius * 2));
        if (tlRadius > 0) {
            for (int i = 18; i < 27; i++) {
                consumer.addVertexWith2DPose(pose, x0 + (tlRadius * COSA[i]), y0 + (tlRadius * SINA[i])).setColor(colors[0]);
            }
        } else {
            consumer.addVertexWith2DPose(pose, cx - halfWidth, cy - halfHeight).setColor(colors[0]);
        }

        // 4. Top-Right (Original i=27-36)
        float trRadius = roundness.y;
        x0 = cx + (0.5f * (width - trRadius * 2));
        y0 = cy - (0.5f * (height - trRadius * 2));
        if (trRadius > 0) {
            for (int i = 27; i < 36; i++) {
                consumer.addVertexWith2DPose(pose, x0 + (trRadius * COSA[i]), y0 + (trRadius * SINA[i])).setColor(colors[3]);
            }
        } else {
            consumer.addVertexWith2DPose(pose, cx + halfWidth, cy - halfHeight).setColor(colors[3]);
        }

        // 5. Final Closing Vertex (Matches your specific !BR logic)
        if (roundness.z <= 0) {
            consumer.addVertexWith2DPose(pose, cx + halfWidth, cy + halfHeight).setColor(colors[2]);
        } else {
            // Your original: buf.vertex(ma, x, cy + (0.5f * dy), 0)
            // x here was the last calculated x (cx + halfWidth)
            consumer.addVertexWith2DPose(pose, cx + halfWidth, cy + (0.5f * (height - brRadius * 2))).setColor(colors[2]);
        }
    }

    private void drawContinuousOutline(VertexConsumer consumer) {
        consumer.setLineWidth(thickness);
        float innerShift = thickness;

        // Quadrant 1: Top-Right (i=0-9)
        float cx = x + width - roundness.y;
        float cy = y + roundness.y;
        walkArc(consumer, cx, cy, roundness.y, roundness.y - innerShift, colors[3], 0, 9);

        // Quadrant 2: Top-Left (i=9-18)
        cx = x + roundness.x;
        cy = y + roundness.x;
        walkArc(consumer, cx, cy, roundness.x, roundness.x - innerShift, colors[0], 9, 18);

        // Quadrant 3: Bottom-Left (i=18-27)
        cx = x + roundness.w;
        cy = y + height - roundness.w;
        walkArc(consumer, cx, cy, roundness.w, roundness.w - innerShift, colors[1], 18, 27);

        // Quadrant 4: Bottom-Right (i=27-36)
        cx = x + width - roundness.z;
        cy = y + height - roundness.z;
        walkArc(consumer, cx, cy, roundness.z, roundness.z - innerShift, colors[2], 27, 36);

        // Close the loop back to the start of Quadrant 1
        float startX = x + width;
        float startY = y + roundness.y;
        consumer.addVertexWith2DPose(pose, startX, startY).setColor(colors[3]);
        consumer.addVertexWith2DPose(pose, startX - innerShift, startY).setColor(colors[3]);
    }

    /**
     * Walks a specific corner arc, pushing vertices for both outer and inner radii.
     */
    private void walkArc(VertexConsumer c, float cx, float cy, float outerR, float innerR, int color, int start, int end) {
        for (int i = start; i <= end; i++) {
            // Outer Ring Vertex
            c.addVertexWith2DPose(pose, cx + (outerR * COSA[i]), cy - (outerR * SINA[i])).setColor(color);
            // Inner Ring Vertex
            c.addVertexWith2DPose(pose, cx + (innerR * COSA[i]), cy - (innerR * SINA[i])).setColor(color);
        }
    }

    @Override
    public @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return DrawHelper.createBounds(pose,scissorArea,x,y,width,height);
    }
}
