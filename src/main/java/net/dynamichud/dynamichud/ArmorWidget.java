package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ArmorWidget extends Widget {
    private final EquipmentSlot slot;
    public ArmorWidget(MinecraftClient client, EquipmentSlot slot, float xPercent, float yPercent) {
        super(client);
        this.slot = slot;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
    }

    @Override
    public void render(MatrixStack matrices) {
        ItemRenderer itemRenderer = client.getItemRenderer();
        ItemStack armorItem = client.player.getEquippedStack(slot);
        TextureHelper.drawItemTexture(matrices, itemRenderer, armorItem, getX(), getY());
    }
    public int getWidth() {
        return 16; // The width of an item texture is 16 pixels
    }

    public int getHeight() {
        return 16; // The height of an item texture is 16 pixels
    }
}

