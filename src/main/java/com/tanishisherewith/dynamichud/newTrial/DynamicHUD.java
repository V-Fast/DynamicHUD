package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.newTrial.config.GlobalConfig;
import com.tanishisherewith.dynamichud.newTrial.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.widget.Widget;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetManager;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.newTrial.widgets.TextWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
import java.util.HashMap;
import java.util.List;

public class DynamicHUD implements ClientModInitializer {
    /**
     * This is a map to store the list of widgets for each widget file to be saved.
     * <p>
     * Allows saving widgets across different mods with same save file name.
     */
    public static final HashMap<String, List<Widget>> fileMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger("DynamicHud");
    private static final List<WidgetRenderer> widgetRenderers = new ArrayList<>();
    public static MinecraftClient MC = MinecraftClient.getInstance();
    public static String MOD_ID = "dynamichud";

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

    @Override
    public void onInitializeClient() {
        printInfo("Initialising DynamicHud");

        // Add WidgetData of included widgets
        WidgetManager.registerCustomWidgets(
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
                    printInfo(String.format("Supported mod with id %s was found!", modId));
                    AbstractMoveableScreen screen;
                    KeyBinding binding;
                    WidgetRenderer widgetRenderer;
                    File widgetsFile;
                    try {
                        DynamicHudIntegration DHIntegration = entrypoint.getEntrypoint();
                        DHIntegration.init();

                        widgetsFile = DHIntegration.getWidgetsFile();

                        if (widgetsFile.exists()) {
                            WidgetManager.loadWidgets(widgetsFile);
                        } else {
                            DHIntegration.addWidgets();
                        }
                        DHIntegration.initAfter();

                        screen = DHIntegration.getMovableScreen();

                        binding = DHIntegration.getKeyBind();

                        DHIntegration.registerCustomWidgets();

                        widgetRenderer = DHIntegration.getWidgetRenderer();
                        addWidgetRenderer(widgetRenderer);

                        List<Widget> widgets = fileMap.get(widgetsFile.getName());

                        if (widgets == null) {
                            fileMap.put(widgetsFile.getName(), widgetRenderer.getWidgets());
                        } else {
                            widgetRenderer.getWidgets().addAll(widgets);
                            fileMap.put(widgetsFile.getName(), widgetRenderer.getWidgets());
                        }

                        //Register events for rendering, saving, loading, and opening the hudEditor
                        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
                            openDynamicScreen(binding, screen);
                        });

                        // Save during exiting a world, server or Minecraft itself
                        // Also saved when a resource pack is reloaded.
                        ServerLifecycleEvents.SERVER_STOPPING.register(server -> saveWidgetsSafely(widgetsFile, fileMap.get(widgetsFile.getName())));
                        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, s) -> saveWidgetsSafely(widgetsFile, fileMap.get(widgetsFile.getName())));
                        ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> saveWidgetsSafely(widgetsFile, fileMap.get(widgetsFile.getName())));
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> saveWidgetsSafely(widgetsFile, fileMap.get(widgetsFile.getName()))));

                        printInfo(String.format("Integration of mod %s was successful", modId));
                    } catch (Throwable e) {
                        if (e instanceof IOException) {
                            logger.warn("An error has occurred while loading widgets of mod {}", modId, e);
                        } else {
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

    private void saveWidgetsSafely(File widgetsFile, List<Widget> widgets) {
        try {
            WidgetManager.saveWidgets(widgetsFile, widgets);
        } catch (IOException e) {
            logger.error("Failed to save widgets. Widgets passed: {}", widgets);
            throw new RuntimeException(e);
        }
    }

}
