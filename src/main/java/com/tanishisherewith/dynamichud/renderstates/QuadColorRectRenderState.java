package com.tanishisherewith.dynamichud.renderstates;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record QuadColorRectRenderState(
        RenderPipeline pipeline,
        Matrix3x2fStack pose,
        float x,
        float y,
        float width,
        float height,
        int[] color,
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
    public @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return DrawHelper.createBounds(pose,scissorArea,x,y,width,height);
    }
}
