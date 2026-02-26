package com.tanishisherewith.dynamichud.internal;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public interface IBufferBuilder {
    VertexConsumer dynamicHUD$writeGenericFloats(VertexFormatElement element, float... values);
}