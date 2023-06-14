package com.tanishisherewith.dynamichud.util;


import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.option.KeyBinding;

/**
 * This class provides utility methods for working with the DynamicHUD mod.
 */
public class DynamicUtil extends DrawHelper {
    private final WidgetManager widgetManager; // The WidgetManager instance used by this class
    public boolean WidgetAdded = false;
    public boolean WidgetLoaded = false;

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
     * @param context   - MatrixStack used for rendering.
     * @param delta - Time elapsed since last frame in seconds.
     */
    public void render(DrawContext context, float delta) {
        // Draw each widget
        for (Widget widget : widgetManager.getWidgets()) {
            if (!MinecraftClient.getInstance().options.debugEnabled || MinecraftClient.getInstance().currentScreen instanceof AbstractMoveableScreen) {
                if (MinecraftClient.getInstance().currentScreen instanceof AbstractMoveableScreen) {
                    widget.render(context.getMatrices());
                } else if (widget.isEnabled()) {
                    widget.render(context.getMatrices());
                }
            }

            // Draw a red box around the widget if the HUD is disabled
            if (MinecraftClient.getInstance().currentScreen instanceof AbstractMoveableScreen) {
                int backgroundColor = widget.isEnabled() ? ColorHelper.getColor(0, 0, 0, 128) : ColorHelper.getColor(255, 0, 0, 128);
                WidgetBox box = widget.getWidgetBox();
                DrawHelper.fill(context.getMatrices(), box.x1, box.y1, box.x2, box.y2, backgroundColor);
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