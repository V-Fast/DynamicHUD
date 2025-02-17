package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.integration.IntegrationManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Using the fabric event {@link HudRenderCallback} to render widgets in the game HUD.
 * Mouse positions are passed in the negatives even though theoretically it's in the centre of the screen.
 */
public class HudRender implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        for (WidgetRenderer widgetRenderer : IntegrationManager.getWidgetRenderers()) {
            widgetRenderer.renderWidgets(drawContext, -120, -120);
        }
    }
}
