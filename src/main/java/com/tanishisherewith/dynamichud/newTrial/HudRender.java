package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

/**
 * Using the fabric event {@link HudRenderCallback} to render widgets in the game HUD.
 * Mouse positions are passed in the negatives even though theoretically it's in the centre of the screen.
 */
public class HudRender implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        for (WidgetRenderer widgetRenderer : DynamicHUD.getWidgetRenderers()) {
            widgetRenderer.renderWidgets(drawContext, -120, -120);
        }
    }
}
