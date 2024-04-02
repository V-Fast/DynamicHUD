package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetManager;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public interface DynamicHudIntegration {
    String KEYBIND_CATEGORY = "DynamicHud";
    String TRANSLATION_KEY = "DynamicHud Editor Screen";
    InputUtil.Type INPUT_TYPE = InputUtil.Type.KEYSYM;
    int KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;
    KeyBinding EDITOR_SCREEN_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            TRANSLATION_KEY,
            INPUT_TYPE,
            KEY,
            KEYBIND_CATEGORY
    ));
    String FILENAME = "widgets.nbt";
    File FILE_DIRECTORY = FabricLoader.getInstance().getConfigDir().toFile();
    File WIDGETS_FILE = new File(FILE_DIRECTORY, FILENAME);

    void init();
    void addWidgets();
    default void registerWidgets(){}

    /**
     * Returns the file where widgets are saved.
     *
     * @return The widgets file.
     */
    default File getWidgetsFile() {
        return WIDGETS_FILE;
    }

    AbstractMoveableScreen getMovableScreen();

    default WidgetRenderer getWidgetRenderer(){
        return new WidgetRenderer(WidgetManager.getWidgets());
    }
}

