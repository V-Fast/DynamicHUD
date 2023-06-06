package net.dynamichud.dynamichud;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.Wigdets;
import net.dynamichud.dynamichud.hudscreen.AbstractMoveableScreen;
import net.dynamichud.dynamichud.hudscreen.MoveableScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class DynamicHUD implements ModInitializer {

    private DynamicUtil dynamicutil;
    private static String filename="DynamicWidgets.nbt";
    public static final File WIDGETS_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), filename);
    private AbstractMoveableScreen screen;
    private final KeyBinding EditorScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "DynamicHud Editor Screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "DynamicHud"
    ));
    @Override
    public void onInitialize() {
        dynamicutil=new DynamicUtil(MinecraftClient.getInstance());

        ServerLifecycleEvents.SERVER_STOPPING.register(client -> dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE));

        // Register a HUD render callback to draw the custom HUD
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            dynamicutil.render(matrices, tickDelta);
            DynamicUtil.openDynamicScreen(EditorScreenKeyBinding,screen);
        });
    }

    public  void setFilename(String filename) {
        this.filename = filename;
    }

    public void setScreen(AbstractMoveableScreen screen) {
        this.screen = screen;
    }
}
