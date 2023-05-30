package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;


public class DynamicUtil extends DrawableHelper {
    private final WidgetManager widgetManager;
    public DynamicUtil(MinecraftClient client) {
        this.widgetManager = new WidgetManager();
    }
    public static void openDynamicScreen(KeyBinding key,DynamicUtil DynamicUtil) {
        while (key.wasPressed()) {
            MinecraftClient.getInstance().setScreen(new MoveScreen(DynamicUtil));
        }

    }

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
                int backgroundColor = widget.enabled ? ColorHelper.getColor(0, 0, 0, 128) : ColorHelper.getColor(255, 0, 0, 128);
                int x1 = 0;
                int y1 = 0;
                int x2 = 0;
                int y2 = 0;
                TextRenderer textrenderer = MinecraftClient.getInstance().textRenderer;
                if (widget instanceof TextWidget text) {
                    int textWidth = textrenderer.getWidth(text.getText());
                    x1 = text.getX() - textWidth / 2 - 2;
                    y1 = text.getY() - 9 / 2 - 2;
                    x2 = text.getX() + textWidth / 2 + 2;
                    y2 = text.getY() + 9 / 2 + 2;

                } else if (widget instanceof ArmorWidget armor) {
                    x1 = (armor.getX() - 2);
                    y1 = (armor.getY() - 2);
                    x2 = (armor.getX() + armor.getWidth() + 2);
                    y2 = (armor.getY() + armor.getHeight() + 2);
                }

                DrawHelper.fill(matrices, x1, y1, x2, y2, backgroundColor);
            }
        }
    }

    public WidgetManager getWidgetManager() {
        return widgetManager;
    }
}

