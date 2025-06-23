package com.tanishisherewith.dynamichud.utils;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.tanishisherewith.dynamichud.internal.IRenderLayer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Vector4f;

import java.awt.*;
import java.util.function.Function;

public class CustomRenderLayers {
    public static RenderLayer QUADS_CUSTOM_BLEND = RenderLayer.of(
            "dynamichud/quads_custom_blend",
            1536,
            false,
            true,
            RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                            .withLocation(Identifier.of("dynamichud", "pipeline/quad_custom_blend_func"))
                            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                            .withBlend(new BlendFunction(SourceFactor.DST_ALPHA, DestFactor.ONE_MINUS_DST_ALPHA))
                            .build()
            ),
            RenderLayer.MultiPhaseParameters.builder().build(false)
    );

    public static RenderLayer TRIANGLE_FAN_CUSTOM_BLEND = RenderLayer.of(
            "dynamichud/triangle_fan_custom_blend",
            1536,
            false,
            true,
            RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of("dynamichud", "pipeline/triangle_fan_custom_blend_func"))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN)
                    .withBlend(new BlendFunction(SourceFactor.DST_ALPHA, DestFactor.ONE_MINUS_DST_ALPHA))
                    .build()
            ),
            RenderLayer.MultiPhaseParameters.builder().build(false)
    );
    public static RenderLayer TRIANGLE_STRIP = RenderLayer.of(
            "dynamichud/triangle_strip",
            1536,
            false,
            true,
            RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of("dynamichud", "pipeline/triangle_strip"))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .build()
            ),
            RenderLayer.MultiPhaseParameters.builder().build(false)
    );

    private static final RenderPipeline ROUNDED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(VertexFormat.builder()
                    .add("Position", VertexFormatElement.POSITION)
                    .add("UV0", VertexFormatElement.UV0)
                    .add("Color", VertexFormatElement.COLOR)
                    .build(), VertexFormat.DrawMode.QUADS)
            .withCull(true)
            .withColorLogic(LogicOp.NONE)
            .withFragmentShader(Identifier.of("dynamichud", "core/rounded"))
            .withVertexShader(Identifier.of("dynamichud", "core/rounded"))
            .withLocation(Identifier.of("dynamichud", "pipeline/rounded"))
            .withUniform("Roundness", UniformType.VEC4)
            .withUniform("widthHeight", UniformType.VEC2)
            .build());

    public static final Function<RoundedParameters, RenderLayer> ROUNDED_RECT = Util.memoize(params -> {
        RenderLayer rl = RenderLayer.of(
                "dynamichud/rounded",
                1024,
                false,
                false,
                ROUNDED,
                RenderLayer.MultiPhaseParameters.builder()
                        .build(false)
        );
        ((IRenderLayer) rl).dynamichud$setUniform("Roundness", params.roundness);
        ((IRenderLayer) rl).dynamichud$setUniform("widthHeight", params.widthHeight);
        return rl;
    });


    private static int getNewVertexFormatElementsId() {
        for(int id = 0; id < 32; id++) {
            if (VertexFormatElement.byId(id) == null) {
                return id;
            }
        }
        throw new RuntimeException("Too many registered VertexFormatElements");
    }


    public record RoundedParameters(Vector4f roundness, float[] widthHeight){}
}
