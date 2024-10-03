package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.BooleanPool;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.widgets.ItemWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
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
import java.util.Objects;

public class DynamicHUD implements ClientModInitializer {
    /**
     * This is a map to store the list of widgets for each widget file to be saved.
     * <p>
     * Allows saving widgets across different mods with same save file name.
     */
    public static final HashMap<String, List<Widget>> FILE_MAP = new HashMap<>();
    public static final Logger logger = LoggerFactory.getLogger("DynamicHud");
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
                TextWidget.DATA,
                ItemWidget.DATA
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

                        //Calls the init method
                        DHIntegration.init();

                        //Gets the widget file to save and load the widgets from
                        widgetsFile = DHIntegration.getWidgetsFile();

                        // Adds / loads widgets from file
                        if (WidgetManager.doesWidgetFileExist(widgetsFile)) {
                            WidgetManager.loadWidgets(widgetsFile);
                        } else {
                            DHIntegration.addWidgets();
                        }

                        //Calls the second init method
                        DHIntegration.initAfter();

                        // Get the instance of AbstractMoveableScreen
                        screen = Objects.requireNonNull(DHIntegration.getMovableScreen());

                        // Get the keybind to open the screen instance
                        binding = DHIntegration.getKeyBind();

                        //Register custom widget datas by WidgetManager.registerCustomWidgets();
                        DHIntegration.registerCustomWidgets();

                        //WidgetRenderer with widgets instance
                        widgetRenderer = DHIntegration.getWidgetRenderer();
                        addWidgetRenderer(widgetRenderer);

                        List<Widget> widgets = FILE_MAP.get(widgetsFile.getName());

                        if (widgets == null || widgets.isEmpty()) {
                            FILE_MAP.put(widgetsFile.getName(), widgetRenderer.getWidgets());
                        } else {
                            widgets.addAll(widgetRenderer.getWidgets());
                            FILE_MAP.put(widgetsFile.getName(), widgets);
                        }

                        //Register events for rendering, saving, loading, and opening the hudEditor
                        ClientTickEvents.START_CLIENT_TICK.register((client) -> openDynamicScreen(binding, screen));

                        /* === Saving === */

                        //When a player exits a world (SinglePlayer worlds) or a server stops
                        ServerLifecycleEvents.SERVER_STOPPING.register(server -> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));

                        // When a resource pack is reloaded.
                        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, s) -> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));

                        //When player disconnects
                        ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));

                        //When minecraft closes
                        ClientLifecycleEvents.CLIENT_STOPPING.register((minecraftClient) -> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));

                        printInfo(String.format("Integration of mod %s was successful", modId));
                    } catch (Throwable e) {
                        if (e instanceof IOException) {
                            logger.warn("An error has occurred while loading widgets of mod {}", modId, e);
                        } else {
                            logger.error("Mod {} has improper implementation of DynamicHUD", modId, e);
                        }
                    }
                });
        printInfo("(DynamicHUD) Integration of supported mods was successful");

        //In game screen render.
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
