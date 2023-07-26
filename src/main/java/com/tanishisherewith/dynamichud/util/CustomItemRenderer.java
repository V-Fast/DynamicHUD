package com.tanishisherewith.dynamichud.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

public class CustomItemRenderer {
    private final ItemStack itemStack; // The text renderer instance
    private final float scale; // The scaling factor of the text

    /**
     * Constructs a CustomTextRenderer object.
     *
     * @param stack The stack to render
     * @param scale The scaling factor of the text
     */
    public CustomItemRenderer(ItemStack stack, float scale) {
        // The Minecraft client instance
        this.itemStack = stack;
        this.scale = scale;
    }

    /**
     * Draws a text with shadow on the screen with the given parameters.
     *
     * @param context The drawContext to use for rendering
     * @param x The x position of the text in pixels
     * @param y The y position of the text in pixels
     * @param color The color of the text in ARGB format
     */
    public void draw(DrawContext context, int x, int y, int color) {
        context.getMatrices().push(); // Pushes the current matrix onto the stack
        context.getMatrices().scale(scale, scale, scale); // Scales the matrix by the scaling factor
        context.drawItem(itemStack,(int) (x/scale), (int) (y/scale),color);
        context.getMatrices().pop(); // Pops the current matrix from the stack and restores the previous one
    }
}
