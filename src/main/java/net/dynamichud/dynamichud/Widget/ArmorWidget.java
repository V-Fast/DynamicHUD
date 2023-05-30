package net.dynamichud.dynamichud.Widget;

import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * This class represents a widget that displays the armor item in a specified equipment slot.
 */
public class ArmorWidget extends Widget {
    private final EquipmentSlot slot; // The equipment slot to display the armor item from

    /**
     * Constructs an ArmorWidget object.
     *
     * @param client   The Minecraft client instance
     * @param slot     The equipment slot to display the armor item from
     * @param xPercent The x position of the widget as a percentage of the screen width
     * @param yPercent The y position of the widget as a percentage of the screen height
     */
    public ArmorWidget(MinecraftClient client, EquipmentSlot slot, float xPercent, float yPercent) {
        super(client);
        this.slot = slot;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
    }

    /**
     * Renders the widget on the screen.
     *
     * @param matrices The matrix stack used for rendering
     */
    @Override
    public void render(MatrixStack matrices) {
        ItemRenderer itemRenderer = client.getItemRenderer();
        ItemStack armorItem = client.player.getEquippedStack(slot);
        TextureHelper.drawItemTexture(matrices, itemRenderer, armorItem, getX(), getY());
    }

    /**
     * Returns the width of the widget.
     *
     * @return The width of the widget in pixels
     */
    public int getWidth() {
        return 16; // The width of an item texture is 16 pixels
    }

    /**
     * Returns the height of the widget.
     *
     * @return The height of the widget in pixels
     */
    public int getHeight() {
        return 16; // The height of an item texture is 16 pixels
    }
}
