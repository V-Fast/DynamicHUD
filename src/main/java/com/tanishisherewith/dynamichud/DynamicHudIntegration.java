package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.NonnullDefault;

import java.io.File;

/**
 * This interface provides methods for integrating DynamicHud into a mod.
 */
public interface DynamicHudIntegration {
    /**
     * The category for the key binding.
     */
    String KEYBIND_CATEGORY = "DynamicHud";

    /**
     * The translation key for the editor screen.
     */
    String TRANSLATION_KEY = "DynamicHud Editor Screen";

    /**
     * The input type for the key binding.
     */
    InputUtil.Type INPUT_TYPE = InputUtil.Type.KEYSYM;

    /**
     * The key code for the key binding.
     */
    int KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

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
     * Initializes the DynamicHud integration.
     * <p>
     * Suggested to be used to initialize {@link com.tanishisherewith.dynamichud.utils.DynamicValueRegistry} and widgets with their respective values
     * </p>
     */
    void init();

    /**
     * To be used to add widgets using {@link WidgetManager}.
     */
    void addWidgets();

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
     *
     * Custom widgets can be registered in any method in the interface
     * but to avoid any errors and mishaps it is recommended you add them here
     */
    default void registerCustomWidgets() {
    }

    /**
     * Performs any necessary initialization after the widgets have been added. This method can be overridden by implementations.
     * <p>
     * Suggested to be used to initialize a {@link WidgetRenderer} object with the added widgets.
     * </p>
     */
    default void initAfter() {
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

    /**
     * Returns the movable screen for the DynamicHud.
     * <p>
     * <h3>
     * !! Should never be null !!
     * </h3>
     *</p>
     *
     * @return The movable screen.
     */
   AbstractMoveableScreen getMovableScreen();

    /**
     * To return a {@link WidgetRenderer} object.
     * By default, it returns a widget renderer consisting of all widgets in the {@link WidgetManager}
     *
     * @return The widget renderer.
     */
    default WidgetRenderer getWidgetRenderer() {
        return new WidgetRenderer(WidgetManager.getWidgets());
    }
}
