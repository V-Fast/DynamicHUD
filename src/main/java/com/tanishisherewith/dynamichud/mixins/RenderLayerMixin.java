package com.tanishisherewith.dynamichud.mixins;


import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import com.tanishisherewith.dynamichud.internal.IRenderLayer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(RenderLayer.MultiPhase.class)
public class RenderLayerMixin implements IRenderLayer {
    @Unique
    private final Map<String, Object> uniforms = new HashMap<>();

    @Override
    public void dynamichud$setUniform(String u, Object v) {
        if (v == null) uniforms.remove(u);
        else uniforms.put(u, v);
    }

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(II)V"))
    void beforeDraw(BuiltBuffer par1, CallbackInfo ci, @Local(ordinal = 0) RenderPass pass) {
        uniforms.forEach((k, v) -> {
            switch (v) {
                case float[] fa -> pass.setUniform(k, fa);
                case int[] ia -> pass.setUniform(k, ia);
                case Matrix4f mat -> pass.setUniform(k, mat);
                default -> throw new IllegalStateException("Unknown uniform type " + v.getClass() + " (" + v + ")");
            }
        });
    }
}