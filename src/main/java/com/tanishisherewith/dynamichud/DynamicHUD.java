package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import com.tanishisherewith.dynamichud.widget.IWigdets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class DynamicHUD implements ClientModInitializer {

    public static final KeyBinding EditorScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "DynamicHud Editor Screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "DynamicHud"
    ));
    static AbstractMoveableScreen Screen;
    private static String filename = "widgets.nbt";
    private static File fileDirectory = FabricLoader.getInstance().getConfigDir().toFile();
    public static final File WIDGETS_FILE = new File(fileDirectory, filename);
    private static DynamicUtil dynamicutil;
    private static IWigdets iWigdets;
    MinecraftClient mc = MinecraftClient.getInstance();

    public static void setFilename(String filename) {
        DynamicHUD.filename = filename;
    }

    public static void setFileDirectory(File fileDirectory) {
        DynamicHUD.fileDirectory = fileDirectory;
    }

    public static void setAbstractScreen(AbstractMoveableScreen screen) {
        Screen = screen;
    }

    public static AbstractMoveableScreen getScreen() {
        return Screen;
    }

    public static void setIWigdets(IWigdets iWigdets) {
        DynamicHUD.iWigdets = iWigdets;
    }

    public static DynamicUtil getDynamicUtil() {
        return dynamicutil;
    }

    @Override
    public void onInitializeClient() {
        dynamicutil = new DynamicUtil(mc);

        ClientTickEvents.START_CLIENT_TICK.register(client -> DynamicUtil.openDynamicScreen(EditorScreenKeyBinding, Screen));

        ClientTickEvents.START_CLIENT_TICK.register(server -> {
            if (mc.player != null && iWigdets != null) {
                if (!WIDGETS_FILE.exists() && !dynamicutil.WidgetAdded) {
                    iWigdets.addWigdets(dynamicutil);
                    dynamicutil.WidgetAdded = true;

                }
                if (WIDGETS_FILE.exists() && !dynamicutil.WidgetLoaded) {
                    iWigdets.loadWigdets(dynamicutil);
                    dynamicutil.WidgetLoaded = true;
                }
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> {
            dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE);
        });
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            dynamicutil.render(matrices, tickDelta);
        });
    }
}
