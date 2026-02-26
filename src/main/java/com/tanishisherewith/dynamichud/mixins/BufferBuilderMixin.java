package com.tanishisherewith.dynamichud.mixins;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.tanishisherewith.dynamichud.internal.IBufferBuilder;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements VertexConsumer, IBufferBuilder {

    @Shadow
    protected abstract long beginElement(VertexFormatElement element);

    /**
     * The "Library" secret sauce.
     * This allows you to write N floats to a custom GENERIC element.
     */
    @Unique
    public VertexConsumer dynamicHUD$writeGenericFloats(VertexFormatElement element, float... values) {
        long addr = this.beginElement(element);
        if (addr != -1L) {
            for (int i = 0; i < values.length; i++) {
                // We use the same MemoryUtil Mojang uses in addVertex and setUv
                MemoryUtil.memPutFloat(addr + (i * 4L), values[i]);
            }
        }
        return this;
    }
}