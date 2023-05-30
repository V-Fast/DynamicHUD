package net.dynamichud.dynamichud;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.TextWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import org.lwjgl.glfw.GLFW;

public class DynamicHUDmod implements ClientModInitializer {
    private DynamicUtil dynamicutil;
    private KeyBinding moveScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dynamicHud.move_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.dynamicHud"
    ));

    /*
    This is an example
     */
    @Override
    public void onInitializeClient() {
        MinecraftClient mc=MinecraftClient.getInstance();
        dynamicutil = new DynamicUtil(mc);
        // Add a text widget to the custom HUD

        TextWidget textWidget = new TextWidget(MinecraftClient.getInstance(), "Biome: ", 0.04f, 0.02f);
        dynamicutil.getWidgetManager().addWidget(textWidget);
        dynamicutil.getWidgetManager().addWidget(new TextWidget(mc, "Ping: ", 0.08f, 0.02f));
        dynamicutil.getWidgetManager().addWidget(new TextWidget(mc, "BPS: ", 0.12f, 0.02f));
        dynamicutil.getWidgetManager().addWidget(new TextWidget(mc, "Position: ", 0.16f, 0.02f));
        dynamicutil.getWidgetManager().addWidget(new TextWidget(mc, "Day/Night: ", 0.16f, 0.02f));


        // Add an armor widget to the custom HUD
        dynamicutil.getWidgetManager().addWidget(new ArmorWidget(mc, EquipmentSlot.CHEST, 0.01f, 0.01f));
        dynamicutil.getWidgetManager().addWidget(new ArmorWidget(mc, EquipmentSlot.HEAD, 0.03f, 0.01f));
        dynamicutil.getWidgetManager().addWidget(new ArmorWidget(mc, EquipmentSlot.LEGS, 0.05f, 0.01f));

        // Register a HUD render callback to draw the custom HUD
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            dynamicutil.render(matrices, tickDelta);
            DynamicUtil.openDynamicScreen(moveScreenKeyBinding,dynamicutil);
        });
    }

}

