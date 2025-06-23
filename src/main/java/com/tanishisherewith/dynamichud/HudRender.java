package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.integration.IntegrationManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/**
 * Using the fabric event {@link HudLayerRegistrationCallback} to render widgets in the game HUD.
 * Mouse positions are passed in the negatives even though theoretically it's in the centre of the screen.
 */
public class HudRender implements HudLayerRegistrationCallback {
    @Override
    public void register(LayeredDrawerWrapper layeredDrawer) {
        layeredDrawer.attachLayerAfter(
                IdentifiedLayer.MISC_OVERLAYS,
                IdentifiedLayer.of(Identifier.of("dynamichud","hudrender_callback"),
                (context, tickCounter) -> {
                    for (WidgetRenderer widgetRenderer : IntegrationManager.getWidgetRenderers()) {
                        widgetRenderer.renderWidgets(context, -120, -120);
                    }
                })
        );
    }
}
