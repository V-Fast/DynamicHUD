package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

import java.awt.*;

public record QuadColorRectRenderState(
        RenderPipeline pipeline,
        Matrix3x2f pose,
        float x,
        float y,
        float width,
        float height,
        int[] color,
        ScreenRectangle bounds,
        ScreenRectangle scissorArea
) implements GuiElementRenderState {

    @Override
    public void buildVertices(VertexConsumer vertices) {
        vertices.addVertexWith2DPose(this.pose(),x, y).setColor(this.color[1]);
        vertices.addVertexWith2DPose(this.pose(), x, y + height).setColor(this.color[2]);
        vertices.addVertexWith2DPose(this.pose(), x + width, y + height).setColor(this.color[3]);
        vertices.addVertexWith2DPose(this.pose(), x + width, y).setColor(this.color[0]);
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return this.scissorArea != null ? this.scissorArea.intersection(bounds) : this.bounds;
    }
}
