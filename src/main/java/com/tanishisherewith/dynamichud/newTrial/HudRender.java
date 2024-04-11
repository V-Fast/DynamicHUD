package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

public class HudRender implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        for (WidgetRenderer widgetRenderer : DynamicHUD.getWidgetRenderers()) {
            widgetRenderer.renderWidgets(drawContext, -120, -120);
        }
    }
}
