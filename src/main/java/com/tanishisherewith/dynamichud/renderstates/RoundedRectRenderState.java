package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

import static com.tanishisherewith.dynamichud.helpers.DrawHelper.COSA;
import static com.tanishisherewith.dynamichud.helpers.DrawHelper.SINA;

/**
 * Using Matrix3x2f is essential for these shapes to be affected by MatrixStack changes.
 */
public record RoundedRectRenderState(
        RenderPipeline pipeline,
        Matrix3x2f pose,
        float x, float y,
        float width, float height,
        float thickness,
        int[] colors,
        Vector4f roundness,
        @Nullable ScreenRectangle scissorArea
) implements GuiElementRenderState {

    @Override
    public void buildVertices(@NonNull VertexConsumer consumer) {
        float fX = 0, fY = 0, fIX = 0, fIY = 0;
        int fC = 0;
        float pX = 0, pY = 0, pIX = 0, pIY = 0;
        int pC = 0;

        float midX = x + (width * 0.5f);
        float midY = y + (height * 0.5f);
        int midC = (thickness <= 0) ? averageColors(colors) : 0;

        // 9 steps per quadrant to match the 360 precomputed tables
        for (int k = 0; k <= 36; k++) {
            int i = k % 36;
            float r, cx, cy;
            int color;

            // Quadrants:- 0-8:BR, 9-17:BL, 18-26:TL, 27-35:TR
            if (i < 9) {
                r = roundness.z; cx = x + width - r; cy = y + height - r; color = colors[2];
            } else if (i < 18) {
                r = roundness.w; cx = x + r; cy = y + height - r; color = colors[3];
            } else if (i < 27) {
                r = roundness.x; cx = x + r; cy = y + r; color = colors[0];
            } else {
                r = roundness.y; cx = x + width - r; cy = y + r; color = colors[1];
            }

            float cX = cx + (r * COSA[i]);
            float cY = cy + (r * SINA[i]);
            float cIX = 0, cIY = 0;

            if (thickness > 0) {
                // clamping inner radius to 0
                float ir = Math.max(0, r - thickness);
                cIX = cx + (ir * COSA[i]);
                cIY = cy + (ir * SINA[i]);
            }

            if (k == 0) {
                // Cache the first vertex set to ensure pixel-perfect loop closure
                fX = cX; fY = cY; fIX = cIX; fIY = cIY; fC = color;
            } else {
                float tX = (k == 36) ? fX : cX;
                float tY = (k == 36) ? fY : cY;
                float tIX = (k == 36) ? fIX : cIX;
                float tIY = (k == 36) ? fIY : cIY;
                int tC = (k == 36) ? fC : color;

                if (thickness > 0) {
                    consumer.addVertexWith2DPose(pose, pX, pY).setColor(pC);
                    consumer.addVertexWith2DPose(pose, pIX, pIY).setColor(pC);
                    consumer.addVertexWith2DPose(pose, tIX, tIY).setColor(tC);
                    consumer.addVertexWith2DPose(pose, tX, tY).setColor(tC);
                } else {
                    consumer.addVertexWith2DPose(pose, midX, midY).setColor(midC);
                    consumer.addVertexWith2DPose(pose, pX, pY).setColor(pC);
                    consumer.addVertexWith2DPose(pose, tX, tY).setColor(tC);
                    consumer.addVertexWith2DPose(pose, tX, tY).setColor(tC);
                }
            }

            pX = cX; pY = cY; pIX = cIX; pIY = cIY; pC = color;
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
        return ((a / 4) << 24) | ((r / 4) << 16) | ((g / 4) << 8) | (b / 4);
    }

    @Override
    public @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return DrawHelper.createBounds(pose, scissorArea, x, y, width, height);
    }
}