package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.helpers.TextureHelper;
import com.tanishisherewith.dynamichud.huds.MoveableScreen;
import com.tanishisherewith.dynamichud.interfaces.IWigdets;
import com.tanishisherewith.dynamichud.interfaces.WidgetLoading;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.armor.ArmorWidget;
import com.tanishisherewith.dynamichud.widget.item.ItemWidget;
import com.tanishisherewith.dynamichud.widget.text.TextWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static com.tanishisherewith.dynamichud.DynamicHUD.WIDGETS_FILE;
import static com.tanishisherewith.dynamichud.DynamicHUD.printInfo;

public class DynamicHUDmod implements ClientModInitializer, IWigdets, WidgetLoading {
    protected Set<Widget> widgets = new HashSet<>();
    protected Set<Widget> MainMenuwidgets = new HashSet<>();
    MinecraftClient mc = MinecraftClient.getInstance();
    private DynamicUtil dynamicutil;


    @Override
    public void onInitializeClient() {
        dynamicutil = DynamicHUD.getDynamicUtil();
        widgets.clear();
        MainMenuwidgets.clear();

        DynamicHUD.setAbstractScreen(new MoveableScreen(Text.of("Editor Screen"), dynamicutil));
        DynamicHUD.setIWigdets(new DynamicHUDmod());
        dynamicutil.getWidgetManager().setWidgetLoading(new DynamicHUDmod());

    }

    @Override
    public void addWigdets(DynamicUtil dynamicUtil) {
        if (mc.player != null) {
            printInfo("Widgets added");
            addTextWidgets(dynamicUtil);
            addArmorWidgets(dynamicUtil);
            addItemWidgets(dynamicUtil);
            dynamicUtil.WidgetAdded = true;
        }
    }

    private void addTextWidgets(DynamicUtil dynamicUtil) {
        widgets.add(new TextWidget(mc, "Non Draggable FPS: ", () -> mc.fpsDebugString.split(" ")[0], 0.5f, 0.5f, true, true,  -1, -1, true));
        widgets.add(new TextWidget(mc, "Dynamic", () -> "HUD", 0.7f, 0.3f, false, false,  -1, -1, true));
        widgets.add(new TextWidget(mc, "Test", () -> "", 0.08f, 0.5f, false, false, -1, -1, true));
        widgets.add(new TextWidget(mc, "", () -> "Data Test", 0.4f, 0.8f, false, false,  -1, -1, true));
        widgets.add(new TextWidget(mc, "HUD Test ", () -> "", 0.83f, 0.8f, false, false,  -1, -1, true));

        for (Widget widget : widgets) {
            if (widget instanceof TextWidget textWidget) {
                if (textWidget.getText().equalsIgnoreCase("Non Draggable FPS: ")) {
                    textWidget.setDraggable(false);
                }
            }
            dynamicUtil.getWidgetManager().addWidget(widget);
        }
    }

    private void addArmorWidgets(DynamicUtil dynamicUtil) {
        String text = "Text";
        widgets.add(new ArmorWidget(mc, EquipmentSlot.CHEST, 0.01f, 0.01f, true,
                TextureHelper.Position.ABOVE,
                () -> text,
                () -> Color.RED,
                true,
                "Text"));
        widgets.add(new ArmorWidget(mc,
                EquipmentSlot.LEGS,
                0.05f,
                0.01f,
                true,
                TextureHelper.Position.LEFT,
                () -> String.valueOf(MinecraftClient.getInstance().getCurrentFps()),
                () -> Color.WHITE,
                true,
                "FPS"));

        for (Widget widget : widgets) {
            if (widget instanceof ArmorWidget armorWidget) {
                dynamicUtil.getWidgetManager().addWidget(armorWidget);
            }
        }
    }

    private void addItemWidgets(DynamicUtil dynamicUtil) {
        widgets.add(new ItemWidget(mc,
                Items.DIAMOND_AXE::getDefaultStack,
                0.15f,
                0.15f,
                true,
                TextureHelper.Position.ABOVE,
                () -> "Label",
                () -> Color.RED,
                true,
                "Label"));

        for (Widget widgetItem : widgets) {
            if (widgetItem instanceof ItemWidget itemWidgetItem) {
                dynamicUtil.getWidgetManager().addWidget(itemWidgetItem);
            }
        }
    }


    @Override
    public void addMainMenuWigdets(DynamicUtil dynamicUtil) {
        printInfo("MainMenu Widgets added");

        MainMenuwidgets.add(new TextWidget(mc, "Test ", () -> "", 0.83f, 0.8f, false, false,  -1, -1, true));
        MainMenuwidgets.add(new TextWidget(mc, "E Test ", () -> "", 0.85f, 0.3f, false, false,  -1, -1, true));
        MainMenuwidgets.add(new TextWidget(mc, "Non Draggable FPS: ", () -> String.valueOf(mc.getCurrentFps()), 0.67f, 0.5f, false, false,  -1, -1, true));
        for (Widget mmwigdet : MainMenuwidgets) {
            if (mmwigdet instanceof TextWidget textWidget) {
                if (textWidget.getText().equalsIgnoreCase("Non Draggable FPS: ")) {
                    textWidget.setDraggable(false);
                }
            }
            dynamicUtil.getWidgetManager().addMainMenuWidget(mmwigdet);
        }
        dynamicUtil.MainMenuWidgetAdded = true;
    }

    @Override
    public void loadWigdets(DynamicUtil dynamicUtil) {
        Set<Widget> widgets = dynamicUtil.getWidgetManager().loadWigdets(WIDGETS_FILE);
        Set<Widget> MainMenuWidget = dynamicUtil.getWidgetManager().loadMainMenuWigdets(WIDGETS_FILE);

        Widget.addTextGenerator("Non Draggable FPS: ", () -> String.valueOf(mc.getCurrentFps()));
        Widget.addTextGenerator("Dynamic", () -> "HUD");
        Widget.addTextGenerator("Test", () -> "");
        Widget.addTextGenerator("", () -> "Data Test");
        Widget.addTextGenerator("HUD Test ", () -> "");
        Widget.addTextGenerator("Text", () -> "Text");
        Widget.addTextGenerator("Test ", () -> String.valueOf(mc.getCurrentFps()));
        Widget.addTextGenerator("Non Draggable FPS: ", () -> String.valueOf(mc.getCurrentFps()));
        Widget.addTextGenerator("Label", () -> "Label");

        for (Widget widget : widgets) {
            dynamicUtil.getWidgetManager().addWidget(widget);
        }

        for (Widget widgetItem : MainMenuWidget) {
            dynamicUtil.getWidgetManager().addMainMenuWidget(widgetItem);
        }

        dynamicUtil.WidgetLoaded = true;
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



