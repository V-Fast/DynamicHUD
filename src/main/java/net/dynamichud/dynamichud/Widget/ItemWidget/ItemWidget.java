package net.dynamichud.dynamichud.Widget.ItemWidget;

import net.dynamichud.dynamichud.Widget.TextGenerator;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.Widget.WidgetBox;
import net.dynamichud.dynamichud.helpers.ColorHelper;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ItemWidget extends Widget {
    public final TextureHelper.Position[] currentTextPosition = TextureHelper.Position.values();
    private TextGenerator textGenerator;
    private ItemStack itemStack;
    private final Color color;

    /**
     * Constructs a Widget object.
     *
     * @param client The Minecraft client instance
     */
    public ItemWidget(MinecraftClient client, ItemStack itemStack, float xPercent, float yPercent, boolean enabled, TextureHelper.Position currentTextPosition, TextGenerator textGenerator, Color color) {
        super(client);
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.enabled = enabled;
        this.itemStack = itemStack;
        this.currentTextPosition[0] = currentTextPosition;
        this.textGenerator = textGenerator;
        this.color = color;
    }

    @Override
    public WidgetBox getWidgetBox() {
        return new WidgetBox(this.getX() - 2, this.getY() - 2, this.getX() + this.getWidth() + 2, this.getY() + this.getHeight() + 2);
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

    /**
     * Returns the text displayed by this widget.
     *
     * @return The text displayed by this widget
     */
    public String getText() {
        return textGenerator.generateText();
    }

    public void setTextGenerator(TextGenerator textGenerator) {
        this.textGenerator = textGenerator;
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putString("class", getClass().getName());
        tag.putFloat("xPercent", xPercent);
        tag.putFloat("yPercent", yPercent);
        tag.putFloat("yPercent", yPercent);
        tag.putBoolean("Enabled", this.enabled);
        tag.putString("Position", String.valueOf(this.currentTextPosition[0]));
        tag.putInt("ItemID", Item.getRawId(itemStack.getItem()));
        tag.putInt("ItemCount", itemStack.getItem().getMaxCount());
        tag.putString("text", getText());
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        xPercent = tag.getFloat("xPercent");
        yPercent = tag.getFloat("yPercent");
        enabled = tag.getBoolean("Enabled");

        String Position = tag.getString("Position");

        int itemID = tag.getInt("ItemID");
        int itemCount=tag.getInt("ItemCount");
        itemStack = getItemStack(itemID,itemCount);

        if (TextureHelper.Position.getByUpperCaseName(Position) != null && !tag.getString("Position").isEmpty())
            currentTextPosition[0] = TextureHelper.Position.getByUpperCaseName(Position);
        else
            currentTextPosition[0] = TextureHelper.Position.ABOVE;
    }

    public ItemStack getItemStack(int itemID,int itemCount) {
        Item item = Item.byRawId(itemID);
        return new ItemStack(item, itemCount);
    }

    @Override
    public void render(MatrixStack matrices) {
        ItemRenderer itemRenderer = client.getItemRenderer();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        TextureHelper.drawItemTextureWithText(matrices, itemRenderer, textRenderer, itemStack, getX(), getY(), getText(), ColorHelper.ColorToInt(color), currentTextPosition[0], 0.5f);
    }
}
