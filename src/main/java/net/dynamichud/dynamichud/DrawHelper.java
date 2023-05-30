package net.dynamichud.dynamichud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class DrawHelper extends DrawableHelper {
    public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
    }

    public static void drawTextWithShadow(MatrixStack matrices,
                                          TextRenderer textRenderer,
                                          String text,
                                          int x,
                                          int y,
                                          int color) {
        textRenderer.drawWithShadow(matrices, text, x, y, color);
    }
    public static void drawText(MatrixStack matrices,
                                          TextRenderer textRenderer,
                                          String text,
                                          int x,
                                          int y,
                                          int color) {
        textRenderer.draw(matrices, text, x, y, color);
    }
}
