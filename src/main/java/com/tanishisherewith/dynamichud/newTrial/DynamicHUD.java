package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetManager;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DynamicHUD implements ClientModInitializer {

    public static MinecraftClient MC = MinecraftClient.getInstance();
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
        printInfo("DynamicHud Initialised");

        //Save and Load
       /* ClientTickEvents.START_CLIENT_TICK.register(server -> {
            if (iWigdets != null) {
                if (!WIDGETS_FILE.exists()) {
                    if (!dynamicutil.WidgetAdded) {
                        iWigdets.addWigdets(dynamicutil);
                    }
                    if (!dynamicutil.MainMenuWidgetAdded) {
                        iWigdets.addMainMenuWigdets(dynamicutil);
                    }
                }

                if (WIDGETS_FILE.exists() && !dynamicutil.WidgetLoaded) {
                    iWigdets.loadWigdets(dynamicutil);
                    printInfo("Widgets loaded");
                    File FileDirectory = new File(fileDirectory, filename);
                    printInfo("Load file Directory: " + FileDirectory);
                }
            }
            DynamicUtil.openDynamicScreen(EditorScreenKeyBinding, Screen);
        });

        */
        FabricLoader.getInstance().getEntrypointContainers("dynamicHud", DynamicHudIntegration.class).forEach(entrypoint -> {
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

                screen = DHIntegration.getMovableScreen();
                KeyBinding binding =DHIntegration.EDITOR_SCREEN_KEY_BINDING;
                WidgetRenderer widgetRenderer = DHIntegration.getWidgetRenderer();
                addWidgetRenderer(widgetRenderer);

                //Register events for rendering, saving, loading, and opening the hudEditor
                ClientTickEvents.START_CLIENT_TICK.register((client)->{
                    DynamicUtil.openDynamicScreen(binding, screen);
                });

                // Save during exiting a world, server or Minecraft itself
                ServerLifecycleEvents.SERVER_STOPPING.register(server -> saveWidgetsSafely(widgetsFile));
                ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, s) -> saveWidgetsSafely(widgetsFile));
                ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> saveWidgetsSafely(widgetsFile));
                Runtime.getRuntime().addShutdownHook(new Thread(() -> saveWidgetsSafely(widgetsFile)));
            } catch (Throwable e) {
                if(e instanceof IOException){
                    logger.warn("An error has occured while loading widgets of mod {}", modId,e);
                }else {
                    logger.warn("Mod {} has incorrect implementation of dynamicHud", modId, e);
                }
            }
           });
    }
    private void saveWidgetsSafely(File widgetsFile) {
        try {
            WidgetManager.saveWidgets(widgetsFile);
        } catch (IOException e) {
            logger.error("Failed to save widgets");
            throw new RuntimeException(e);
        }
    }

}
