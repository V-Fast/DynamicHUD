package net.dynamichud.dynamichud;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.ArmorWidget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.TextGenerator;
import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.Widget.Wigdets;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.dynamichud.dynamichud.hudscreen.MoveableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.dynamichud.dynamichud.DynamicHUD.WIDGETS_FILE;

public class DynamicHUDmod implements ClientModInitializer, Wigdets {
    private DynamicUtil dynamicutil;
    MinecraftClient mc = MinecraftClient.getInstance();
    List<Widget> widgets=new ArrayList<>();

    @Override
    public void onInitializeClient() {
        dynamicutil = new DynamicUtil(mc);


            // Add default widgets if this is the first run
           if (!WIDGETS_FILE.exists()) {
              addWigdets(dynamicutil);
           }
               addWigdets(dynamicutil);

        ServerLifecycleEvents.SERVER_STOPPING.register(client -> dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE));

    }

    @Override
    public void addWigdets(DynamicUtil dynamicUtil) {
        widgets.add(new TextWidget(mc, () -> "FPS: " + mc.fpsDebugString.split(" ")[0], 0.5f, 0.5f,true,true,false,-1,true));
        widgets.add(new TextWidget(mc, () -> "Biome: ", 0.7f, 0.3f,false,false,false,-1,true));
        widgets.add(new TextWidget(mc, () -> "Ping: ", 0.08f, 0.5f,false,false,false,-1,true));
        widgets.add(new TextWidget(mc, () -> "Position: ", 0.4f, 0.8f,false,false,false,-1,true));
        widgets.add(new TextWidget(mc, () -> "Day/Night: ", 0.83f, 0.8f,false,false,false,-1,true));

        // Add an armor widget to the custom HUD
        String text="Yellow";
        widgets.add(new ArmorWidget(mc, EquipmentSlot.CHEST, 0.01f, 0.01f,true, TextureHelper.Position.ABOVE,() ->text));
        widgets.add(new ArmorWidget(mc, EquipmentSlot.HEAD, 0.03f, 0.01f,true,TextureHelper.Position.BELOW,() ->text));
        widgets.add(new ArmorWidget(mc, EquipmentSlot.LEGS, 0.05f, 0.01f,true,TextureHelper.Position.LEFT,() -> String.valueOf(MinecraftClient.getInstance().getCurrentFps())));
        for(Widget wigdet: widgets)
        {
            dynamicutil.getWidgetManager().addWidget(wigdet);
        }
    }

    @Override
    public void loadWigdets(DynamicUtil dynamicUtil) {
        List<Widget> widgets = dynamicUtil.getWidgetManager().loadWigdets(WIDGETS_FILE);
        int textIndex = 0;
        int armorIndex = 0;
        TextGenerator[] TextWidgettext = new TextGenerator[]{
                () -> "FPS: " + mc.getCurrentFps(),
                () -> "Biome: "+mc.world.getBiome(mc.player.getBlockPos()).getKey(),
                () -> "Ping: ",
                () -> "Position: ",
                () -> "Day/Night: "
        };
        TextGenerator[] ArmorWidgettext = new TextGenerator[]{
                () -> String.valueOf(mc.getCurrentFps()),
                () -> "What: ",
                () -> "Why: "
        };
        System.out.println("Widgets in addwidget: " + widgets);
        for (Widget widget : widgets) {
            if (widget instanceof TextWidget textWidget) {
                TextGenerator textGenerator = TextWidgettext[textIndex++];
                textWidget.setTextGenerator(textGenerator);
                System.out.println("TextWidget: " + textWidget);
                dynamicutil.getWidgetManager().addWidget(textWidget);
            }
            if (widget instanceof ArmorWidget armorWidget) {
                TextGenerator textGenerator = ArmorWidgettext[armorIndex++];
                armorWidget.setTextGenerator(textGenerator);
                dynamicutil.getWidgetManager().addWidget(armorWidget);
            }
        }
    }

}



