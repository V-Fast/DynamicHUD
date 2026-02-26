package com.tanishisherewith.dynamichud.integration;

import com.mojang.blaze3d.platform.InputConstants;
import com.tanishisherewith.dynamichud.IntegrationTest;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.File;

/**
 * This interface provides methods for integrating DynamicHud into a mod.
 * @see IntegrationTest
 * @see DefaultIntegrationImpl
 */
public interface DynamicHudIntegration {
    /**
     * The filename for the widgets file.
     */
    String FILENAME = "widgets.nbt";

    /**
     * The directory for the widgets file.
     */
    File FILE_DIRECTORY = FabricLoader.getInstance().getConfigDir().toFile();

    /**
     * The file where widgets are saved.
     */
    File WIDGETS_FILE = new File(FILE_DIRECTORY, FILENAME);


    /**
     * Entry point for configuring DynamicHUD integration.
     *
     * @param configurator Configuration context
     */
    DynamicHudConfigurator configure(DynamicHudConfigurator configurator);

    /**
     * Initializes the DynamicHud integration.
     * <p>
     * Suggested to be used to initialize {@link com.tanishisherewith.dynamichud.utils.DynamicValueRegistry} and widgets with their respective values
     * </p>
     */
    void init();

    /**
     * This method is called after widgets from the widget file have been loaded successfully and added to the renderer.
     */
    default void postWidgetLoading(WidgetRenderer renderer) {
    }

    /**
     * To register custom widgets. This method can be overridden by implementations.
     * <p>
     * Use {@link WidgetManager#registerCustomWidget(WidgetData)} to register custom widgets.
     * <pre>
     * Example:
     *     {@code
     *     WidgetManager.registerCustomWidget(TextWidget.DATA);
     *     }
     * </pre>
     * <p>
     * Custom widgets can be registered in any method in the interface
     * but to avoid any errors and mishaps it is recommended you add them here
     */
    default void registerCustomWidgets() {
    }

    /**
     * Returns the file where widgets are to be saved and loaded from.
     *
     * @return The widgets file.
     */
    default File getWidgetsFile() {
        return WIDGETS_FILE;
    }

    /**
     * Returns the keybind to open the {@link AbstractMoveableScreen} instance.
     *
     * @return The keybind.
     */
     KeyMapping getKeyBind();
}
