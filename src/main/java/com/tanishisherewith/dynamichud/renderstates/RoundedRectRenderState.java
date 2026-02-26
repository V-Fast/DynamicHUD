package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tanishisherewith.dynamichud.internal.IBufferBuilder;
import com.tanishisherewith.dynamichud.utils.CustomRenderLayers;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fc;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

public record RoundedRectRenderState(
        RenderPipeline pipeline,
        Matrix3x2fc pose,
        float x, 
        float y,
        float width,
        float height,
        float thickness,
        int[] colors,
        Vector4f roundness,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {

    @Override
    public void buildVertices(@NonNull VertexConsumer consumer) {
        if (consumer instanceof IBufferBuilder builder) {
            float[][] uvs = {{0, 0}, {0, 1}, {1, 1}, {1, 0}};
            float[][] coords = {{x, y}, {x, y + height}, {x + width, y + height}, {x + width, y}};

            for (int i = 0; i < 4; i++) {
                consumer.addVertexWith2DPose(pose, coords[i][0], coords[i][1])
                        .setColor(colors[i % colors.length])
                        .setUv(uvs[i][0], uvs[i][1]);

                builder.dynamicHUD$writeGenericFloats(CustomRenderLayers.ELM_WIDTH_HEIGHT, width, height);

                builder.dynamicHUD$writeGenericFloats(CustomRenderLayers.ELM_ROUNDNESS,
                        roundness.x, roundness.y, roundness.z, roundness.w);
            }
        }
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @org.jspecify.annotations.Nullable ScreenRectangle bounds() {
        return this.scissorArea != null ? this.scissorArea.intersection(this.bounds) : this.bounds;
    }
}
