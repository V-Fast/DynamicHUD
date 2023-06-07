package net.dynamichud.dynamichud;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.ArmorWidget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.ItemWidget.ItemWidget;
import net.dynamichud.dynamichud.Widget.TextGenerator;
import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.Widget.Wigdets;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.dynamichud.dynamichud.hudscreen.MoveableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.dynamichud.dynamichud.DynamicHUD.*;

public class DynamicHUDmod implements ClientModInitializer, Wigdets {
    MinecraftClient mc = MinecraftClient.getInstance();
    List<Widget> widgets = new ArrayList<>();
    private DynamicUtil dynamicutil;

    @Override
    public void onInitializeClient() {
        dynamicutil = new DynamicUtil(mc);
        widgets.clear();

        // Add default widgets if this is the first run
        if (!WIDGETS_FILE.exists()) {
            addWigdets(dynamicutil);
        }
        loadWigdets(dynamicutil);

        DynamicHUD.setAbstractScreen(new MoveableScreen(Text.of("Editor Screen"), dynamicutil));

        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            dynamicutil.render(matrices, tickDelta);
            DynamicUtil.openDynamicScreen(EditorScreenKeyBinding, Screen);
        });

    }

    @Override
    public void addWigdets(DynamicUtil dynamicUtil) {
        System.out.println("Widgets Added");

        widgets.add(new TextWidget(mc, () -> "FPS: " + mc.fpsDebugString.split(" ")[0], 0.5f, 0.5f, true, true, false, -1, true));
        widgets.add(new TextWidget(mc, () -> "Biome: " + mc.world.getBiome(mc.player.getBlockPos()), 0.7f, 0.3f, false, false, false, -1, true));
        widgets.add(new TextWidget(mc, () -> "Ping: ", 0.08f, 0.5f, false, false, false, -1, true));
        widgets.add(new TextWidget(mc, () -> "Position: ", 0.4f, 0.8f, false, false, false, -1, true));
        widgets.add(new TextWidget(mc, () -> "Day/Night: ", 0.83f, 0.8f, false, false, false, -1, true));

        // Add an armor widget to the custom HUD
        String text = "Yellow";
        widgets.add(new ArmorWidget(mc, EquipmentSlot.CHEST, 0.01f, 0.01f, true, TextureHelper.Position.ABOVE, () -> text));
        widgets.add(new ArmorWidget(mc, EquipmentSlot.HEAD, 0.03f, 0.01f, true, TextureHelper.Position.BELOW, () -> text));
        widgets.add(new ArmorWidget(mc, EquipmentSlot.LEGS, 0.05f, 0.01f, true, TextureHelper.Position.LEFT, () -> String.valueOf(MinecraftClient.getInstance().getCurrentFps())));

        widgets.add(new ItemWidget(mc, mc.player != null ? mc.player.getInventory().getStack(4) : Items.DIAMOND_SWORD.getDefaultStack(), 0.05f, 0.05f, true, TextureHelper.Position.ABOVE, () -> "", Color.RED));
        widgets.add(new ItemWidget(mc, mc.player != null ? mc.player.getInventory().getStack(1) : Items.DIAMOND_SHOVEL.getDefaultStack(), 0.1f, 0.1f, true, TextureHelper.Position.ABOVE, () -> "", Color.RED));
        widgets.add(new ItemWidget(mc, mc.player != null ? mc.player.getInventory().getStack(3) : Items.DIAMOND_AXE.getDefaultStack(), 0.15f, 0.15f, true, TextureHelper.Position.ABOVE, () -> "", Color.RED));

        for (Widget wigdet : widgets) {
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
                () -> {
                    assert mc.player != null;
                    assert mc.world != null;
                    return "Biome: " + mc.world.getBiome(mc.player.getBlockPos());
                },
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



