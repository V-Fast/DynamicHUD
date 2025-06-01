package com.tanishisherewith.dynamichud.integration;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.tanishisherewith.dynamichud.integration.IntegrationManager.FILE_MAP;

public class DynamicHudConfigurator {
    private final List<Widget> widgets = new ArrayList<>();
    private WidgetRenderer renderer;
    private Consumer<List<Widget>> saveHandler = widgetsList -> {};
    private AbstractMoveableScreen screen = null;
    public boolean markAsUtility = false; // A.k.a we don't want this mod to display a hud.

    public DynamicHudConfigurator addWidget(Widget widget) {
        this.widgets.add(widget);
        return this;
    }

    /**
     * Configure the existing renderer object with this method
     */
    public DynamicHudConfigurator configureRenderer(Consumer<WidgetRenderer> wrConsumer) {
        if (renderer == null) {
            this.renderer = new WidgetRenderer(widgets);
        }
        wrConsumer.accept(renderer);
        return this;
    }

    public DynamicHudConfigurator configureRenderer(Consumer<WidgetRenderer> wrConsumer, List<Widget> widgets) {
        this.renderer = new WidgetRenderer(widgets);
        wrConsumer.accept(renderer);
        return this;
    }

    /**
     * Override the present widget renderer with your own instance
     */
    public DynamicHudConfigurator overrideRenderer(WidgetRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    /**
     * Called before saving these widgets
     */
    public DynamicHudConfigurator onSave(Consumer<List<Widget>> saveHandler) {
        this.saveHandler = saveHandler;
        return this;
    }

    /**
     * Returns the movable screen for the hud screen.
     * <p>
     * <h3>
     * !! Should never be null !!
     * </h3>
     * </p>
     */
    public DynamicHudConfigurator withMoveableScreen(Function<DynamicHudConfigurator, AbstractMoveableScreen> screenProvider) {
        this.screen = screenProvider.apply(this);
        return this;
    }

    /**
     * Batch operation
     */
    public DynamicHudConfigurator modifyWidgets(Consumer<Widget> operation) {
        widgets.forEach(operation);
        return this;
    }

    public List<Widget> getWidgets() {
        return Collections.unmodifiableList(widgets);
    }

    public WidgetRenderer getRenderer() {
        return renderer;
    }

    public final Consumer<List<Widget>> getSaveHandler() {
        return saveHandler;
    }

    public final AbstractMoveableScreen getMovableScreen() {
        return screen;
    }

    /**
     * Internal method to save these widgets using fabric API events. Should not be called anywhere else except when loading the DHIntegration on startup.
     */
    @ApiStatus.Internal
    public void setupSaveEvents(File widgetsFile) {
        /* === Saving === */
        // Each mod is hooked to the fabric's event system to save its widget.

        //When a player exits a world (SinglePlayer worlds) or a server stops
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));

        // When a resource pack is reloaded.
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, s) -> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));

        //When player disconnects
        ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));

        //When minecraft closes
       ClientLifecycleEvents.CLIENT_STOPPING.register((mc)-> saveWidgetsSafely(widgetsFile, FILE_MAP.get(widgetsFile.getName())));
    }

    @ApiStatus.Internal
    public final void registerWidgets() {
        widgets.forEach(WidgetManager::addWidget);
    }

    private void saveWidgetsSafely(File widgetsFile, List<Widget> widgets) {
        try {
            this.saveHandler.accept(widgets);
            WidgetManager.saveWidgets(widgetsFile, widgets);
        } catch (Throwable e) {
            DynamicHUD.logger.error("Failed to save widgets. Widgets passed: {}", widgets);
            throw new RuntimeException(e);
        }
    }
}
