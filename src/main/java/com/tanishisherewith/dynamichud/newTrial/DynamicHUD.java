package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.newTrial.config.GlobalConfig;
import com.tanishisherewith.dynamichud.newTrial.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetManager;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.newTrial.widgets.TextWidget;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DynamicHUD implements ClientModInitializer {
    public static MinecraftClient MC = MinecraftClient.getInstance();
    public static String MOD_ID = "dynamichud";
    private static final Logger logger = LoggerFactory.getLogger("DynamicHud");
    private static final List<WidgetRenderer> widgetRenderers = new ArrayList<>();

    public static void addWidgetRenderer(WidgetRenderer widgetRenderer) {
        widgetRenderers.add(widgetRenderer);
    }

    public static List<WidgetRenderer> getWidgetRenderers() {
        return widgetRenderers;
    }

    public static void printInfo(String msg) {
        logger.info(msg);
    }

    public static void printWarn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void onInitializeClient() {
        printInfo("Initialising DynamicHud");

        // Add WidgetData of included widgets
        WidgetManager.addWidgetDatas(
                TextWidget.DATA
        );
        //YACL load
        GlobalConfig.HANDLER.load();

        printInfo("Integrating mods...");
        FabricLoader.getInstance()
                .getEntrypointContainers("dynamicHud", DynamicHudIntegration.class)
                .forEach(entrypoint -> {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            String modId = metadata.getId();
            AbstractMoveableScreen screen;
            try {
                DynamicHudIntegration DHIntegration = entrypoint.getEntrypoint();
                DHIntegration.init();

                File widgetsFile = DHIntegration.getWidgetsFile();

                if(widgetsFile.exists()) {
                    WidgetManager.loadWidgets(widgetsFile);
                }else{
                    DHIntegration.addWidgets();
                }
                DHIntegration.initAfter();

                screen = DHIntegration.getMovableScreen();

                KeyBinding binding =DHIntegration.EDITOR_SCREEN_KEY_BINDING;

                DHIntegration.registerCustomWidgets();

                WidgetRenderer widgetRenderer = DHIntegration.getWidgetRenderer();
                addWidgetRenderer(widgetRenderer);

                //Register events for rendering, saving, loading, and opening the hudEditor
                ClientTickEvents.START_CLIENT_TICK.register((client)-> {
                    openDynamicScreen(binding, screen);
                });

                // Save during exiting a world, server or Minecraft itself
                // Also saved when a resource pack is reloaded.
                ServerLifecycleEvents.SERVER_STOPPING.register(server -> saveWidgetsSafely(widgetsFile));
                ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, s) -> saveWidgetsSafely(widgetsFile));
                ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> saveWidgetsSafely(widgetsFile));
                Runtime.getRuntime().addShutdownHook(new Thread(() -> saveWidgetsSafely(widgetsFile)));

                printInfo(String.format("Integration of mod %s was successful",modId));
            } catch (Throwable e) {
                if(e instanceof IOException){
                    logger.warn("An error has occurred while loading widgets of mod {}", modId,e);
                }else {
                    logger.warn("Mod {} has incorrect implementation of DynamicHUD", modId, e);
                }
            }
           });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> GlobalConfig.HANDLER.save());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, s) -> GlobalConfig.HANDLER.save());
        ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> GlobalConfig.HANDLER.save());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> GlobalConfig.HANDLER.save()));

        HudRenderCallback.EVENT.register(new HudRender());
    }
    private void saveWidgetsSafely(File widgetsFile) {
        try {
            WidgetManager.saveWidgets(widgetsFile);
        } catch (IOException e) {
            logger.error("Failed to save widgets");
            throw new RuntimeException(e);
        }
    }
    /**
     * Opens the MovableScreen when the specified key is pressed.
     *
     * @param key    The key to listen for
     * @param screen The AbstractMoveableScreen instance to use to set the screen
     */
    public static void openDynamicScreen(KeyBinding key, AbstractMoveableScreen screen) {
        if (key.wasPressed()) {
            MC.setScreen(screen);
        }
    }

}