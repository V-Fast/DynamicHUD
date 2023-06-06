package net.dynamichud.dynamichud;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.hudscreen.AbstractMoveableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class DynamicHUD implements ClientModInitializer {

    private static String filename = "widgets.nbt";
    public static final File WIDGETS_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), filename);
    static AbstractMoveableScreen Screen;
    public static final KeyBinding EditorScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "DynamicHud Editor Screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "DynamicHud"
    ));
    private DynamicUtil dynamicutil;

    public static void setFilename(String filename) {
        DynamicHUD.filename = filename;
    }

    public static void setAbstractScreen(AbstractMoveableScreen screen) {
        Screen = screen;
    }

    @Override
    public void onInitializeClient() {
    }
}
