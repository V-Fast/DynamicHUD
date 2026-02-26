package com.tanishisherewith.dynamichud;

import com.mojang.blaze3d.platform.InputConstants;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.MouseColorQuery;
import com.tanishisherewith.dynamichud.integration.IntegrationManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class DynamicHUD implements ClientModInitializer {
    public static Minecraft MC = Minecraft.getInstance();
    public static final Logger logger = LoggerFactory.getLogger("DynamicHud");
    public static String MOD_ID = "dynamichud";

    static KeyMapping EDITOR_SCREEN_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "DynamicHud Editor Screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("dynamichud", "editor_screen"))
    ));

    public static void printInfo(String msg) {
        logger.info(msg);
    }

    public static void printWarn(String msg) {
        logger.warn(msg);
    }


    @Override
    public void onInitializeClient() {
        printInfo("Initialising DynamicHUD");

        //YACL load
        GlobalConfig.HANDLER.load();

        ClientLifecycleEvents.CLIENT_STARTED.register((minecraft)-> IntegrationManager.integrate());


        //In game screen render.
        /*
         * Using the fabric event {@link HudElementRegistry} to render widgets in the game HUD.
         * Mouse positions are passed in the negatives even though theoretically it's in the centre of the screen.
         */
        HudElementRegistryImpl.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath("dynamichud","hudrender_callback"),
                        (graphics, tickCounter) -> {
                            for (WidgetRenderer widgetRenderer : IntegrationManager.getWidgetRenderers()) {
                                widgetRenderer.renderWidgets(graphics, -120, -120);
                            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc-> MouseColorQuery.processIfPending());
    }
}
