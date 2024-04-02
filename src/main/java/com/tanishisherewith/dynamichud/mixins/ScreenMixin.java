package com.tanishisherewith.dynamichud.mixins;

import com.tanishisherewith.dynamichud.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.newTrial.DynamicHUD;
import com.tanishisherewith.dynamichud.newTrial.DynamicHudTest;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(at = @At("TAIL"), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        for(WidgetRenderer widgetRenderer: DynamicHUD.getWidgetRenderers()){
            widgetRenderer.renderWidgets(context);
        }
    }
}
