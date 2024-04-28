package com.tanishisherewith.dynamichud.mixins;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    public int width;

    @Shadow
    public int height;

    @Inject(at = @At("RETURN"), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        for (WidgetRenderer widgetRenderer : DynamicHUD.getWidgetRenderers()) {
            widgetRenderer.renderWidgets(context, mouseX, mouseY);
        }
    }

    @Inject(at = @At("HEAD"), method = "resize")
    private void onScreenResize(MinecraftClient client, int width, int height, CallbackInfo ci) {
        WidgetManager.onScreenResized(width, height, this.width, this.height);
    }

    @Inject(at = @At("HEAD"), method = "close")
    private void onClose(CallbackInfo ci) {
        for (WidgetRenderer widgetRenderer : DynamicHUD.getWidgetRenderers()) {
            widgetRenderer.onCloseScreen();
        }
    }
}
