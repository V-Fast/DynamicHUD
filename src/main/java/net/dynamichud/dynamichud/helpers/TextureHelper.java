package net.dynamichud.dynamichud.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/**
 * This class provides helper methods for drawing textures on the screen.
 */
public class TextureHelper {
    /**
     * Draws an item texture on the screen.
     *
     * @param matrices    The matrix stack used for rendering
     * @param itemRenderer The item renderer instance used for rendering the item texture
     * @param itemStack   The item stack to render the texture for
     * @param x           The x position to draw the texture at
     * @param y           The y position to draw the texture at
     */
    public static void drawItemTexture(MatrixStack matrices,
                                       ItemRenderer itemRenderer,
                                       ItemStack itemStack,
                                       int x,
                                       int y) {
        itemRenderer.renderInGui(matrices,itemStack, x, y);
    }

    /**
     * Draws the texture of the item in the player's main hand on the screen.
     *
     * @param matrices    The matrix stack used for rendering
     * @param itemRenderer The item renderer instance used for rendering the item texture
     * @param client      The Minecraft client instance
     * @param x           The x position to draw the texture at
     * @param y           The y position to draw the texture at
     */
    public static void drawMainHandTexture(MatrixStack matrices,
                                           ItemRenderer itemRenderer,
                                           MinecraftClient client,
                                           int x,
                                           int y) {
        assert client.player != null;
        ItemStack mainHandItem = client.player.getMainHandStack();
        drawItemTexture(matrices, itemRenderer, mainHandItem, x, y);
    }
}
