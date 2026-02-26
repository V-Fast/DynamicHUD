package com.tanishisherewith.dynamichud.mixins;

import com.tanishisherewith.dynamichud.integration.IntegrationManager;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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
    private void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        for (WidgetRenderer widgetRenderer : IntegrationManager.getWidgetRenderers()) {
            widgetRenderer.renderWidgets(graphics, mouseX, mouseY);
        }
    }

    //Injected before the screen is actually resized to get the new and also the old dimensions.
    @Inject(at = @At("HEAD"), method = "resize")
    private void onScreenResize(int i, int j, CallbackInfo ci) {
        WidgetManager.onScreenResized(width, height, this.width, this.height);
    }

    @Inject(at = @At("HEAD"), method = "onClose")
    private void onClose(CallbackInfo ci) {
        for (WidgetRenderer widgetRenderer : IntegrationManager.getWidgetRenderers()) {
            widgetRenderer.onCloseScreen();
        }
    }
}
