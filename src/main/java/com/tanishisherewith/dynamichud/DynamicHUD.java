package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.integration.IntegrationManager;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widgets.ItemWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class DynamicHUD implements ClientModInitializer {
    public static MinecraftClient MC = MinecraftClient.getInstance();
    public static final Logger logger = LoggerFactory.getLogger("DynamicHud");
    public static String MOD_ID = "dynamichud";


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

        IntegrationManager.integrate();

        //In game screen render.
        HudRenderCallback.EVENT.register(new HudRender());
    }

}
