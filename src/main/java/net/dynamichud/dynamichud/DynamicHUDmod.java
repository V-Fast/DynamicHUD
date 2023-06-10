package net.dynamichud.dynamichud;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Util.TextGenerator;
import net.dynamichud.dynamichud.Util.WidgetLoading;
import net.dynamichud.dynamichud.Widget.*;
import net.dynamichud.dynamichud.Widget.ArmorWidget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.ItemWidget.ItemWidget;
import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.dynamichud.dynamichud.hudscreen.MoveableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.dynamichud.dynamichud.DynamicHUD.*;

  public class DynamicHUDmod implements ClientModInitializer, Wigdets, WidgetLoading {
    MinecraftClient mc = MinecraftClient.getInstance();
    protected List<Widget> widgets = new ArrayList<>();
    private DynamicUtil dynamicutil;
    protected boolean WidgetAdded=false;
    protected boolean WidgetLoaded=false;

    @Override
    public void onInitializeClient() {
        dynamicutil = new DynamicUtil(mc);
        widgets.clear();

        // Add default widgets if this is the first run
        ClientTickEvents.START_CLIENT_TICK.register(server -> {
            if (!WIDGETS_FILE.exists() && mc.player!=null && !WidgetAdded) {
                addWigdets(dynamicutil);
            }
            if(!WidgetLoaded) {
                loadWigdets(dynamicutil);
                WidgetLoaded=true;
            }
        });

        DynamicHUD.setAbstractScreen(new MoveableScreen(Text.of("Editor Screen"), dynamicutil));

        ServerLifecycleEvents.SERVER_STOPPING.register(client -> {
            WidgetLoaded=false;
            WidgetAdded=false;
        });

        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            dynamicutil.getWidgetManager().saveWidgets(WIDGETS_FILE);
            dynamicutil.render(matrices, tickDelta);
            DynamicUtil.openDynamicScreen(EditorScreenKeyBinding, Screen);
        });

    }

    @Override
    public void addWigdets(DynamicUtil dynamicUtil) {

            widgets.add(new TextWidget(mc, "FPS: ", () -> mc.fpsDebugString.split(" ")[0], 0.5f, 0.5f, true, true, false, -1, -1, true));
            widgets.add(new TextWidget(mc, "Biome: ", () -> "PLAINS", 0.7f, 0.3f, false, false, false, -1, -1, true));
            widgets.add(new TextWidget(mc, "Ping: ", () -> "", 0.08f, 0.5f, false, false, false, -1, -1, true));
            widgets.add(new TextWidget(mc, "Position: ", () -> "", 0.4f, 0.8f, false, false, false, -1, -1, true));
            widgets.add(new TextWidget(mc, "Day/Night: ", () -> "", 0.83f, 0.8f, false, false, false, -1, -1, true));

            // Add an armor widget to the custom HUD
            String text="Text";
            widgets.add(new ArmorWidget(mc, EquipmentSlot.CHEST, 0.01f, 0.01f, true, TextureHelper.Position.ABOVE, () -> text, Color.RED));
            widgets.add(new ArmorWidget(mc, EquipmentSlot.HEAD, 0.03f, 0.01f, true, TextureHelper.Position.BELOW, () -> text, Color.BLACK));
            widgets.add(new ArmorWidget(mc, EquipmentSlot.LEGS, 0.05f, 0.01f, true, TextureHelper.Position.LEFT, () -> String.valueOf(MinecraftClient.getInstance().getCurrentFps()), Color.WHITE));

            widgets.add(new ItemWidget(mc,() -> mc.player != null ? mc.player.getInventory().getStack(3) : Items.DIAMOND_AXE.getDefaultStack(), 0.15f, 0.15f, true, TextureHelper.Position.ABOVE, () -> "",()-> Color.RED));

    }

    @Override
    public void loadWigdets(DynamicUtil dynamicUtil) {
        if (mc.player!=null) {
            List<Widget> widgets = dynamicUtil.getWidgetManager().loadWigdets(WIDGETS_FILE);
            int textIndex = 0;
            int armorIndex = 0;
            TextGenerator[] TextWidgettext = new TextGenerator[]{
                    () -> String.valueOf(mc.getCurrentFps()),
                    () -> "PLAINS",
                    () -> "",
                    () -> "",
                    () -> ""
            };
            TextGenerator[] ArmorWidgettext = new TextGenerator[]{
                    () -> String.valueOf(mc.getCurrentFps()),
                    () -> "",
                    () -> ""
            };
            for (Widget widget : widgets) {
                if (widget instanceof TextWidget textWidget) {
                    TextGenerator textGenerator = TextWidgettext[textIndex++];
                    textWidget.setDataTextGenerator(textGenerator);
                    dynamicutil.getWidgetManager().addWidget(textWidget);
                }
                if (widget instanceof ArmorWidget armorWidget) {
                    TextGenerator textGenerator = ArmorWidgettext[armorIndex++];
                    armorWidget.setTextGenerator(textGenerator);
                    dynamicutil.getWidgetManager().addWidget(armorWidget);
                }
                if (widget instanceof ItemWidget itemWidget) {
                    dynamicutil.getWidgetManager().addWidget(itemWidget);
                }
            }
        }
    }

    @Override
    public Widget loadWidgetsFromTag(String className, NbtCompound widgetTag) {

        //SAMPLE CODE EXAMPLE :
        /*if (className.equals(ItemWidget.class.getName())) {
            ItemWidget widget = new ItemWidget(MinecraftClient.getInstance(), ItemStack.EMPTY, 0, 0, true, TextureHelper.Position.ABOVE, () -> "", Color.BLUE);
            widget.readFromTag(widgetTag);
            return widget;
        }*/
        return WidgetLoading.super.loadWidgetsFromTag(className, widgetTag);
    }
}



