package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import com.tanishisherewith.dynamichud.interfaces.IWigdets;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger("DynamicHud");
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

    public static IWigdets getIWigdets() {
        return iWigdets;
    }

    @Override
    public void onInitializeClient() {
        dynamicutil = new DynamicUtil(mc);


        ClientTickEvents.START_CLIENT_TICK.register(server -> {
            if (mc.player != null && iWigdets != null) {
                if (!WIDGETS_FILE.exists() && !dynamicutil.WidgetAdded) {
                    iWigdets.addWigdets(dynamicutil);
                    dynamicutil.WidgetAdded = true;
                    printInfo("Widgets added");
                }
                if (WIDGETS_FILE.exists() && !dynamicutil.WidgetLoaded) {
                    iWigdets.loadWigdets(dynamicutil);
                    dynamicutil.WidgetLoaded = true;
                    printInfo("Widgets loaded");
                    File filedirectory=new File(fileDirectory,filename);
                    printInfo("Load file Directory: "+filedirectory);
                }
            }
            DynamicUtil.openDynamicScreen(EditorScreenKeyBinding, Screen);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, packetSender) -> {
            dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE);
        });
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            dynamicutil.render(drawContext, tickDelta);
        });
        printInfo("Initialised");
    }
    public static void printInfo(String msg)
    {
        logger.info(msg);
    }
    public static void printWarn(String msg)
    {
        logger.warn(msg);
    }
}
