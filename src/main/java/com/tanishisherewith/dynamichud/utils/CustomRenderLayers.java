package com.tanishisherewith.dynamichud.utils;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Vector4f;


public class CustomRenderLayers {
    public static final RenderPipeline COLOR_LINE = RenderPipeline.builder()
            .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "color_line"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES)
            .withFragmentShader(Identifier.withDefaultNamespace("position_color"))
            .withVertexShader(Identifier.withDefaultNamespace("position_color"))
            .build();

    // Width/Height in UV1
    public static final VertexFormatElement ELM_WIDTH_HEIGHT =
            new VertexFormatElement(1, 2, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 8);

    // Roundness in UV2
    public static final VertexFormatElement ELM_ROUNDNESS =
            new VertexFormatElement(2, 4, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 16);

    public static final VertexFormat ROUNDED_FORMAT = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("UV0", VertexFormatElement.UV0)
            .add("WidthHeight", ELM_WIDTH_HEIGHT)
            .add("Roundness", ELM_ROUNDNESS)
            .build();


    public static final RenderPipeline ROUNDED_RECT = RenderPipelines.register(
            RenderPipeline.builder()
                    .withVertexFormat(ROUNDED_FORMAT, VertexFormat.Mode.QUADS)
                    .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA))
                    .withFragmentShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded"))
                    .withVertexShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded"))
                    .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "pipeline/rounded"))
                    .withUniform("Roundness", UniformType.UNIFORM_BUFFER)
                    .withUniform("widthHeight", UniformType.UNIFORM_BUFFER)
                    .build()
    );

    public static final RenderPipeline ROUNDED_RECT_OUTLINE = RenderPipelines.register(
            RenderPipeline.builder()
                    .withVertexFormat(ROUNDED_FORMAT, VertexFormat.Mode.QUADS)
                    .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA))
                    .withFragmentShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded_outline"))
                    .withVertexShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded"))
                    .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "pipeline/rounded_outline"))
                    .withUniform("Roundness", UniformType.UNIFORM_BUFFER)
                    .withUniform("widthHeight", UniformType.UNIFORM_BUFFER)
                    .withUniform("Thickness", UniformType.UNIFORM_BUFFER)
                    .build()
    );

    public static RenderPipeline QUADS_CUSTOM_BLEND = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "pipeline/quad_custom_blend_func"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withBlend(new BlendFunction(SourceFactor.DST_ALPHA, DestFactor.ONE_MINUS_DST_ALPHA))
            .build()
    );

    public static RenderPipeline TRIANGLE_FAN_CUSTOM_BLEND = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "pipeline/triangle_fan_custom_blend_func"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN)
            .withBlend(new BlendFunction(SourceFactor.DST_ALPHA, DestFactor.ONE_MINUS_DST_ALPHA))
            .build()
    );
    public static RenderPipeline TRIANGLE_STRIP = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "pipeline/triangle_strip"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
    );

    /*
    private static final RenderPipeline ROUNDED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(VertexFormat.builder()
                    .add("Position", VertexFormatElement.POSITION)
                    .add("UV0", VertexFormatElement.UV0)
                    .add("Color", VertexFormatElement.COLOR)
                    .build(), VertexFormat.Mode.QUADS)
            .withCull(true)
            .withColorLogic(LogicOp.NONE)
            .withFragmentShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded"))
            .withVertexShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded"))
            .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "pipeline/rounded"))
            .withUniform("Roundness", UniformType.UNIFORM_BUFFER)
            .withUniform("widthHeight", UniformType.UNIFORM_BUFFER)
            .build());

    private static final RenderPipeline ROUNDED_OUTLINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(VertexFormat.builder()
                    .add("Position", VertexFormatElement.POSITION)
                    .add("UV0", VertexFormatElement.UV0)
                    .add("Color", VertexFormatElement.COLOR)
                    .build(), VertexFormat.Mode.QUADS)
            .withCull(true)
            .withColorLogic(LogicOp.NONE)
            .withFragmentShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded_outline"))
            .withVertexShader(Identifier.fromNamespaceAndPath("dynamichud", "core/rounded"))
            .withLocation(Identifier.fromNamespaceAndPath("dynamichud", "pipeline/rounded_outline"))
            .withUniform("Thickness", UniformType)
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

    public static final Function<OutlineParameters, RenderLayer> ROUNDED_RECT_OUTLINE = Util.memoize(params -> {
        RenderLayer rl = RenderLayer.of(
                "neverdies/2d/rounded_rect_outline",
                1024,
                false,
                false,
                ROUNDED_OUTLINE,
                RenderLayer.MultiPhaseParameters.builder()
                        .build(false)
        );
        ((IRenderLayer) rl).dynamichud$setUniform("Roundness", params.roundness);
        ((IRenderLayer) rl).dynamichud$setUniform("Thickness", new Vector4f(params.thickness, 0f, 0f, 0f));
        ((IRenderLayer) rl).dynamichud$setUniform("widthHeight", params.widthHeight);
        return rl;
    });

     */

    private static int getNewVertexFormatElementsId() {
        for (int id = 0; id < 32; id++) {
            if (VertexFormatElement.byId(id) == null) {
                return id;
            }
        }
        throw new RuntimeException("Too many registered VertexFormatElements");
    }


    public record RoundedParameters(Vector4f roundness, float[] widthHeight) {
    }

    public record OutlineParameters(Vector4f roundness, float thickness, float[] widthHeight) {
    }
}
