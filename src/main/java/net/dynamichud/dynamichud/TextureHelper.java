package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class TextureHelper {
    public static void drawItemTexture(MatrixStack matrices,
                                       ItemRenderer itemRenderer,
                                       ItemStack itemStack,
                                       int x,
                                       int y) {
        itemRenderer.renderInGui(matrices,itemStack, x, y);
    }

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

