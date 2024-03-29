package com.tanishisherewith.dynamichud.widget.armor;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.TextureHelper;
import com.tanishisherewith.dynamichud.interfaces.TextGenerator;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.awt.*;
import java.util.function.Supplier;

/**
 * This class represents a widget that displays the armor item in a specified equipment slot.
 */
public class ArmorWidget extends Widget {
    public final TextureHelper.Position[] currentTextPosition = TextureHelper.Position.values();
    protected EquipmentSlot slot; // The equipment slot to display the armor item from
    protected TextGenerator textGenerator;
    protected Supplier<Color> color;
    protected boolean TextBackground;

    /**
     * Constructs an ArmorWidget object.
     *
     * @param client   The Minecraft client instance
     * @param slot     The equipment slot to display the armor item from
     * @param xPercent The x position of the widget as a percentage of the screen width
     * @param yPercent The y position of the widget as a percentage of the screen height
     */
    public ArmorWidget(MinecraftClient client, EquipmentSlot slot, float xPercent, float yPercent, boolean enabled, TextureHelper.Position currentTextPosition, TextGenerator textGenerator, Supplier<Color> color, boolean Textbackground, String label) {
        super(client, label);
        this.slot = slot;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.enabled = enabled;
        this.currentTextPosition[0] = currentTextPosition;
        this.textGenerator = textGenerator;
        this.color = color;
        this.TextBackground = Textbackground;
    }

    /**
     * Renders the widget on the screen.
     */
    @Override
    public void render(DrawContext drawContext) {
        ItemRenderer itemRenderer = client.getItemRenderer();
        ItemStack armorItem;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (client.player == null) {
            armorItem = Items.DIAMOND_CHESTPLATE.getDefaultStack();
        } else {
            armorItem = client.player.getEquippedStack(slot);
        }
        TextureHelper.drawItemTextureWithTextAndScale(drawContext, scale, textRenderer, armorItem, getX(), getY(), getText(), ColorHelper.ColorToInt(getColor()), currentTextPosition[0], scale*0.5f, TextBackground);
    }

    @Override
    public void setTextGeneratorFromLabel() {
        TextGenerator textGenerator = textGenerators.get(label);
        if (textGenerator != null) {
            setTextGenerator(textGenerator);
        }
    }

    @Override
    public WidgetBox getWidgetBox() {
        return new WidgetBox(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(),scale);
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

    public Color getColor() {
        return color.get();
    }

    public void setColor(Supplier<Color> color) {
        this.color = color;
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putString("slot", slot.getName());
        tag.putString("Position", String.valueOf(this.currentTextPosition[0]));
        if (this.getText() != null) tag.putString("text", this.getText());
        tag.putInt("Color", this.getColor().getRGB());
        tag.putBoolean("TextBackground", this.TextBackground);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        slot = EquipmentSlot.byName(tag.getString("slot"));
        String Position = tag.getString("Position");
        color = () -> ColorHelper.getColorFromInt(tag.getInt("Color"));
        if (TextureHelper.Position.getByUpperCaseName(Position) != null && !(tag.getString("Position") == null) && !tag.getString("Position").isEmpty())
            currentTextPosition[0] = TextureHelper.Position.getByUpperCaseName(Position);
        else
            currentTextPosition[0] = TextureHelper.Position.ABOVE;
        TextBackground = tag.getBoolean("TextBackground");
        label = tag.getString("label");
    }
}
