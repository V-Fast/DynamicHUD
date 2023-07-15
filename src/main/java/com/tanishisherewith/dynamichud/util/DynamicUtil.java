package com.tanishisherewith.dynamichud.util;


import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;

import java.util.Set;


/**
 * This class provides utility methods for working with the DynamicHUD mod.
 */
public class DynamicUtil extends DrawContext {
    static MinecraftClient client = MinecraftClient.getInstance();
    private final WidgetManager widgetManager; // The WidgetManager instance used by this class
    public boolean WidgetAdded = false;
    public boolean MainMenuWidgetAdded = false;
    public boolean WidgetLoaded = false;


    /**
     * Constructs a DynamicUtil object.
     *
     * @param client The Minecraft client instance
     */
    public DynamicUtil(MinecraftClient client) {
        super(client, VertexConsumerProvider.immediate(new BufferBuilder(3)));
        this.widgetManager = new WidgetManager();
    }

    /**
     * Opens the MoveScreen when the specified key is pressed.
     *
     * @param key    The key to listen for
     * @param screen The AbstractMoveableScreen instance to use to set the screen
     */
    public static void openDynamicScreen(KeyBinding key, AbstractMoveableScreen screen) {
        if (key.wasPressed()) {
            client.setScreen(screen);
        }
    }

    /**
     * Renders widgets on screen.
     *
     * @param context - MatrixStack used for rendering.
     * @param delta   - Time elapsed since last frame in seconds.
     */
    public void render(DrawContext context, float delta) {
        Set<Widget> mainMenuWidgets = widgetManager.getMainMenuWidgets();
        Set<Widget> widgets = widgetManager.getWidgets();

        if (client.currentScreen instanceof TitleScreen) {
            // Draw each Menu widget
            for (Widget widget : mainMenuWidgets) {
                widget.render(context);
                widget.updatePosition();
                int backgroundColor = widget.isEnabled() ? ColorHelper.getColor(0, 0, 0, 128) : ColorHelper.getColor(255, 0, 0, 128);
                WidgetBox box = widget.getWidgetBox();
                DrawHelper.fill(context, box.x1 - 2, box.y1 - 2, box.x2 + 2, box.y2 + 2, backgroundColor);
            }
            return;
        }

        // Draw each widget
        if (!client.options.debugEnabled || client.currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                if (client.currentScreen instanceof AbstractMoveableScreen) {
                    widget.render(context);
                } else if (widget.isEnabled()) {
                    widget.render(context);
                }
                // Draw a red box around the widget if the HUD is disabled
                if (client.currentScreen instanceof AbstractMoveableScreen) {
                    int backgroundColor = widget.isEnabled() ? ColorHelper.getColor(0, 0, 0, 128) : ColorHelper.getColor(255, 0, 0, 128);
                    WidgetBox box = widget.getWidgetBox();
                    DrawHelper.fill(context, box.x1 - 2, box.y1 - 2, box.x2 + 2, box.y2 + 2, backgroundColor);
                }
                widget.updatePosition();
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