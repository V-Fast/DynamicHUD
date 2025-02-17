package com.tanishisherewith.dynamichud.integration;

import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;

/**
 * This interface provides methods for integrating DynamicHud into a mod.
 */
public interface DynamicHudIntegration {
    /**
     * The key binding for opening the editor screen.
     */
    KeyBinding EDITOR_SCREEN_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "DynamicHud Editor Screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "DynamicHud"
    ));

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
    default KeyBinding getKeyBind() {
        return EDITOR_SCREEN_KEY_BINDING;
    }
}
