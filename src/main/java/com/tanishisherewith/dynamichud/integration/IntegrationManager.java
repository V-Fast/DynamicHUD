package com.tanishisherewith.dynamichud.integration;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.internal.ModError;
import com.tanishisherewith.dynamichud.internal.WarningScreen;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.BooleanPool;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.tanishisherewith.dynamichud.DynamicHUD.printInfo;

public final class IntegrationManager {

    /**
     * This is a map to store the list of widgets for each widget file to be saved.
     * <p>
     * Allows saving widgets across different mods with same save file name.
     */
    public static final Map<String, List<Widget>> FILE_MAP = new HashMap<>();
    private static final List<WidgetRenderer> widgetRenderers = new ArrayList<>();

    private static boolean enableTestIntegration = false;


    public static void addWidgetRenderer(WidgetRenderer widgetRenderer) {
        widgetRenderers.add(widgetRenderer);
    }

    public static List<WidgetRenderer> getWidgetRenderers() {
        return widgetRenderers;
    }

    /**
     * Opens the MovableScreen when the specified key is pressed.
     *
     * @param key    The key to listen for
     * @param screen The AbstractMoveableScreen instance to use to set the screen
     */
    public static void openScreen(KeyBinding key, AbstractMoveableScreen screen) {
        if (key.wasPressed()) {
            DynamicHUD.MC.setScreen(screen);
        }
    }

    private static void checkToEnableTestIntegration() {
        String[] args = FabricLoader.getInstance().getLaunchArguments(true);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--dynamicHudTest") && i + 1 < args.length) {
                enableTestIntegration = Boolean.parseBoolean(args[i + 1]);
                break;
            }
        }
    }

    public static void integrate() {
        checkToEnableTestIntegration();

        printInfo("Integrating mods...");

        var integrations = new ArrayList<>(getRegisteredIntegrations());

        if (enableTestIntegration) {
            EntrypointContainer<DynamicHudIntegration> testIntegration = getTestIntegration();
            if (testIntegration != null) {
                integrations.add(testIntegration);
                printInfo("Test integration enabled and loaded successfully.");
            }
        }

        List<ModError> bad_implementations = new ArrayList<>();

        integrations.forEach(container -> {
            //Register custom widget data's by WidgetManager.registerCustomWidgets() first for every entrypoint
            container.getEntrypoint().registerCustomWidgets();
        });

        for (var entrypoint : integrations) {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            String modId = metadata.getId();

            AbstractMoveableScreen screen;
            KeyBinding binding;
            WidgetRenderer widgetRenderer;
            File widgetsFile;
            try {
                DynamicHudIntegration DHIntegration = entrypoint.getEntrypoint();

                //Calls the init method
                DHIntegration.init();

                DynamicHudConfigurator configurator = DHIntegration.configure(new DynamicHudConfigurator());

                if (configurator.markAsUtility) {
                    printInfo(String.format("Supported utility mod with id %s was found!", modId));
                    continue;
                }

                printInfo(String.format("Supported mod with id %s was found!", modId));

                //Gets the widget file to save and load the widgets from
                widgetsFile = DHIntegration.getWidgetsFile();

                // Adds / loads widgets from file
                if (WidgetManager.doesWidgetFileExist(widgetsFile)) {
                    List<Widget> widgets = WidgetManager.loadWidgets(widgetsFile);
                    configurator.configureRenderer(renderer -> renderer.clearAndAdd(widgets));
                    DHIntegration.postWidgetLoading(configurator.getRenderer());
                } else {
                    configurator.registerWidgets();
                }


                // Get the instance of AbstractMoveableScreen
                screen = Objects.requireNonNull(configurator.getMovableScreen(), "AbstractMovableScreen instance should not be null!");

                // Get the keybind to open the screen instance
                binding = DHIntegration.getKeyBind();

                //WidgetRenderer with widgets instance
                widgetRenderer = configurator.getRenderer();

                addWidgetRenderer(widgetRenderer);

                updateFileMap(widgetsFile.getName(), widgetRenderer.getWidgets());

                //Register events for rendering, saving, loading, and opening the hudEditor
                ClientTickEvents.START_CLIENT_TICK.register((client) -> openScreen(binding, screen));

                configurator.setupSaveEvents(widgetsFile);

                printInfo(String.format("Integration of mod %s was successful", modId));
            } catch (Throwable e) {
                if (e instanceof IOException) {
                    DynamicHUD.logger.warn("An IO error has occurred while loading widgets of mod {}", modId, e);
                } else {
                    DynamicHUD.logger.error("Mod {} has improper implementation of DynamicHUD", modId, e);
                }
                bad_implementations.add(new ModError(modId, e.getMessage().trim()));
            }
        }
        printInfo("(DynamicHUD) Integration of supported mods was successful");


        // Sheesh
        if (!bad_implementations.isEmpty()) {
            BooleanPool.put("WarningScreenFlag", false);

            ClientTickEvents.START_CLIENT_TICK.register((client) -> {
                if (BooleanPool.get("WarningScreenFlag")) return;

                if (DynamicHUD.MC.currentScreen instanceof TitleScreen) {
                    DynamicHUD.MC.setScreen(new WarningScreen(bad_implementations));
                    BooleanPool.put("WarningScreenFlag", true);
                }
            });
        }

    }

    private static void updateFileMap(String fileName, List<Widget> widgets) {
        FILE_MAP.compute(fileName, (k, v) -> {
            // Concat existing and the new widget list.
            if (v == null) return new ArrayList<>(widgets);
            v.addAll(widgets);
            return v;
        });
    }

    private static List<EntrypointContainer<DynamicHudIntegration>> getRegisteredIntegrations() {
        return new ArrayList<>(FabricLoader.getInstance()
                .getEntrypointContainers("dynamicHud", DynamicHudIntegration.class));
    }

    /**
     * This makes it so that if minecraft is launched with the program arguments
     * <p>
     * {@code --dynamicHudTest true}
     * </p>
     * then it will
     * load the {@link com.tanishisherewith.dynamichud.IntegrationTest} class as an entrypoint, eliminating any errors due to human incapacity of
     * adding/removing a single line from the `fabric.mod.json`
     */
    private static EntrypointContainer<DynamicHudIntegration> getTestIntegration() {
        DynamicHudIntegration testIntegration;
        try {
            Class<?> testClass = Class.forName("com.tanishisherewith.dynamichud.IntegrationTest");
            testIntegration = (DynamicHudIntegration) testClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            DynamicHUD.logger.info("DynamicHudTest class not found. Skipping test integration.");
            return null;
        } catch (Exception e) {
            DynamicHUD.logger.error("Error instantiating DynamicHudTest", e);
            return null;
        }

        return new EntrypointContainer<>() {
            @Override
            public DynamicHudIntegration getEntrypoint() {
                return testIntegration;
            }

            @Override
            public ModContainer getProvider() {
                return FabricLoader.getInstance().getModContainer(DynamicHUD.MOD_ID).orElseThrow();
            }
        };
    }
}
