package com.tanishisherewith.dynamichud.widget.item;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.TextureHelper;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import com.tanishisherewith.dynamichud.util.TextGenerator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.awt.*;
import java.util.function.Supplier;

public class ItemWidget extends Widget {
    public final TextureHelper.Position[] currentTextPosition = TextureHelper.Position.values();
    protected Supplier<Color> color;
    protected TextGenerator textGenerator;
    protected Supplier<ItemStack> itemStack;

    /**
     * Constructs a Widget object.
     *
     * @param client The Minecraft client instance
     */
    public ItemWidget(MinecraftClient client, Supplier<ItemStack> itemStack, float xPercent, float yPercent, boolean enabled, TextureHelper.Position currentTextPosition, TextGenerator textGenerator, Supplier<Color> color) {
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

    public ItemStack getItemStack() {
        return itemStack.get();
    }

    public void setItemStack(Supplier<ItemStack> itemStack) {
        this.itemStack = itemStack;
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

    public Supplier<Color> getColor() {
        return color;
    }
    public void setColor(Supplier<Color> color) {
        this.color=color;
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
        tag.putInt("ItemID", Item.getRawId(getItemStack().getItem()));
        tag.putInt("ItemCount", getItemStack().getMaxCount());
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
        int itemCount = tag.getInt("ItemCount");
        itemStack = () -> getItemStack(itemID, itemCount);

        if (TextureHelper.Position.getByUpperCaseName(Position) != null && !tag.getString("Position").isEmpty())
            currentTextPosition[0] = TextureHelper.Position.getByUpperCaseName(Position);
        else
            currentTextPosition[0] = TextureHelper.Position.ABOVE;
    }

    public ItemStack getItemStack(int itemID, int itemCount) {
        Item item = Item.byRawId(itemID);
        return new ItemStack(item, itemCount);
    }

    @Override
    public void render(DrawContext drawContext) {
        ItemRenderer itemRenderer = client.getItemRenderer();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        TextureHelper.drawItemTextureWithText(drawContext.getMatrices(),drawContext, itemRenderer, textRenderer, getItemStack(), getX(), getY(), getText(), ColorHelper.ColorToInt(color.get()), currentTextPosition[0], 0.5f);
    }
}
