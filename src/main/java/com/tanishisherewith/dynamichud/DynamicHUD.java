package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.interfaces.IWigdets;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DynamicHUD implements ModInitializer {

    private static final Logger logger = LoggerFactory.getLogger("DynamicHud");
    static AbstractMoveableScreen Screen;
    private static String keybingCategory = "DynamicHud";
    private static String TranslationKey = "DynamicHud Editor Screen";
    private static InputUtil.Type inputType = InputUtil.Type.KEYSYM;
    private static int key = GLFW.GLFW_KEY_RIGHT_SHIFT;
    public static final KeyBinding EditorScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            TranslationKey,
            inputType,
            key,
            keybingCategory
    ));
    private static String filename = "widgets.nbt";
    private static File fileDirectory = FabricLoader.getInstance().getConfigDir().toFile();
    public static final File WIDGETS_FILE = new File(fileDirectory, filename);
    private static DynamicUtil dynamicutil;
    private static IWigdets iWigdets;
    MinecraftClient mc = MinecraftClient.getInstance();

    public static void setInputType(InputUtil.Type inputType) {
        DynamicHUD.inputType = inputType;
    }

    public static void setKeyBindKey(int key) {
        DynamicHUD.key = key;
    }

    public static void setKeybingCategory(String keybingCategory) {
        DynamicHUD.keybingCategory = keybingCategory;
    }

    public static void setTranslationKey(String translationKey) {
        TranslationKey = translationKey;
    }

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

    public static DynamicUtil getDynamicUtil() {
        return dynamicutil;
    }

    public static IWigdets getIWigdets() {
        return iWigdets;
    }

    public static void setIWigdets(IWigdets iWigdets) {
        DynamicHUD.iWigdets = iWigdets;
    }

    public static void printInfo(String msg) {
        logger.info(msg);
    }

    public static void printWarn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void onInitialize() {
        dynamicutil = new DynamicUtil(mc);
        printInfo("DynamicHud Initialised");

        //Save and Load
        ClientTickEvents.START_CLIENT_TICK.register(server -> {
            if (iWigdets != null) {
                if (!WIDGETS_FILE.exists()) {
                    if (!dynamicutil.WidgetAdded) {
                        iWigdets.addWigdets(dynamicutil);
                    }
                    if (!dynamicutil.MainMenuWidgetAdded) {
                        iWigdets.addMainMenuWigdets(dynamicutil);
                    }
                }

                if (WIDGETS_FILE.exists() && !dynamicutil.WidgetLoaded) {
                    iWigdets.loadWigdets(dynamicutil);
                    printInfo("Widgets loaded");
                    File FileDirectory = new File(fileDirectory, filename);
                    printInfo("Load file Directory: " + FileDirectory);
                }
            }
            DynamicUtil.openDynamicScreen(EditorScreenKeyBinding, Screen);
        });

        //RenderCallBack
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> dynamicutil.render(drawContext, tickDelta));

        // Save during exiting a world, server or Minecraft itself
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(this::onEndDataPackReload);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE)));
    }

    private void onEndDataPackReload(MinecraftServer server, LifecycledResourceManager lifecycledResourceManager, boolean b) {
        dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE);
    }

    private void onServerStopping(MinecraftServer server) {
        dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE);
    }
}
