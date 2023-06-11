package net.dynamichud.dynamichud.Util;

import net.dynamichud.dynamichud.DynamicHUD;
import net.dynamichud.dynamichud.Widget.ArmorWidget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.ItemWidget.ItemWidget;
import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.Widget.WidgetBox;
import net.dynamichud.dynamichud.Widget.WidgetManager;
import net.dynamichud.dynamichud.helpers.ColorHelper;
import net.dynamichud.dynamichud.helpers.DrawHelper;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.dynamichud.dynamichud.hudscreen.AbstractMoveableScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.dynamichud.dynamichud.DynamicHUD.WIDGETS_FILE;

/**
 * This class provides utility methods for working with the DynamicHUD mod.
 */
public class DynamicUtil extends DrawableHelper {
    private final WidgetManager widgetManager; // The WidgetManager instance used by this class

    /**
     * Constructs a DynamicUtil object.
     *
     * @param client The Minecraft client instance
     */
    public DynamicUtil(MinecraftClient client) {
        this.widgetManager = new WidgetManager();
    }

    /**
     * Opens the MoveScreen when the specified key is pressed.
     *
     * @param key    The key to listen for
     * @param screen The AbstractMoveableScreen instance to use to set the screen
     */
    public static void openDynamicScreen(KeyBinding key, AbstractMoveableScreen screen) {
        while (key.wasPressed()) {
            MinecraftClient.getInstance().setScreen(screen);
        }
    }

    /**
     * Renders widgets on screen.
     *
     * @param matrices  - MatrixStack used for rendering.
     * @param tickDelta - Time elapsed since last frame in seconds.
     */
    public void render(MatrixStack matrices, float tickDelta) {
        // Draw each widget
        for (Widget widget : widgetManager.getWidgets()) {
            if (MinecraftClient.getInstance().currentScreen instanceof AbstractMoveableScreen) {
                widget.render(matrices);
            } else if (widget.isEnabled()) {
                widget.render(matrices);
            }

            // Draw a red box around the widget if the HUD is disabled
            if (MinecraftClient.getInstance().currentScreen instanceof AbstractMoveableScreen) {
                int backgroundColor = widget.isEnabled() ? ColorHelper.getColor(0, 0, 0, 128) : ColorHelper.getColor(255, 0, 0, 128);
                WidgetBox box = widget.getWidgetBox();
                if (widget instanceof TextWidget)
                    DrawHelper.drawBox(matrices, widget.getX(), widget.getY(), box.getWidth() + 1, box.getHeight(), backgroundColor);
                else if (widget instanceof ArmorWidget || widget instanceof ItemWidget)
                    DrawHelper.fill(matrices, box.x1, box.y1, box.x2, box.y2, backgroundColor);
                else
                    DrawHelper.fill(matrices, box.x1, box.y1, box.x2, box.y2, backgroundColor);
            }
        }
    }


    /**
     * Returns WidgetManager instance used by this class.
     *
     * @return WidgetManager instance used by this class.
     */
    public WidgetManager getWidgetManager() {
        return widgetManager;
    }

}