package net.dynamichud.dynamichud.Util;

import net.dynamichud.dynamichud.Widget.*;
import net.dynamichud.dynamichud.helpers.ColorHelper;
import net.dynamichud.dynamichud.helpers.DrawHelper;
import net.dynamichud.dynamichud.hudscreen.MoveScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;

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
     * @param key         The key to listen for
     * @param DynamicUtil The DynamicUtil instance to use
     */
    public static void openDynamicScreen(KeyBinding key,DynamicUtil DynamicUtil) {
        while (key.wasPressed()) {
            MinecraftClient.getInstance().setScreen(new MoveScreen(DynamicUtil));
        }
    }

    /**
     * Renders widgets on screen.
     *@param matrices - MatrixStack used for rendering.
     *@param tickDelta - Time elapsed since last frame in seconds.
     */
    public void render(MatrixStack matrices, float tickDelta) {
        // Draw each widget
        for (Widget widget : widgetManager.getWidgets()) {
            if (MinecraftClient.getInstance().currentScreen instanceof MoveScreen) {
                widget.render(matrices);
            } else if (widget.isEnabled()) {
                widget.render(matrices);
            }

            // Draw a red box around the widget if the HUD is disabled
            if (MinecraftClient.getInstance().currentScreen instanceof MoveScreen) {
                int backgroundColor = widget.isEnabled() ? ColorHelper.getColor(0, 0, 0, 128) : ColorHelper.getColor(255, 0, 0, 128);
                WidgetBox box = widget.getWidgetBox();
                if(widget instanceof TextWidget) DrawHelper.drawBox(matrices, widget.getX(), widget.getY(), box.getWidth(), box.getHeight(), backgroundColor);
                if(widget instanceof ArmorWidget) DrawHelper.fill(matrices, box.x1, box.y1, box.x2, box.y2, backgroundColor);
            }
        }
    }


    /**
     * Returns WidgetManager instance used by this class.
     *@return WidgetManager instance used by this class.
     */
    public WidgetManager getWidgetManager() {
        return widgetManager;
    }
}
