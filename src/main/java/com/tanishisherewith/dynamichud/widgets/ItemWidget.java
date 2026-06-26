package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * This is just an example widget, not supposed to be used.
 */
public class ItemWidget extends Widget {
    public ItemStack item;
    public static WidgetData<?> DATA = new WidgetData<>("ItemWidget", "Displays item texture", ItemWidget::new);

    public ItemWidget(ItemStack itemStack, String modId) {
        super(DATA, modId);
        this.item = itemStack;
    }

    public ItemWidget() {
        this(ItemStack.EMPTY, "empty");
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.renderItem(item, x, y);
        widgetBox.setDimensions(getX(), getY(), 16, 16, this.canScale);
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        super.writeToTag(tag);
        tag.putInt("ItemID", Item.getId(item.getItem()));
    }

    @Override
    public void readFromTag(CompoundTag tag) {
        super.readFromTag(tag);
        item = Item.byId(tag.getInt("ItemID").orElse(0)).getDefaultInstance();
    }

    public void setItemStack(ItemStack item) {
        this.item = item;
    }

    public static class Builder extends WidgetBuilder<Builder, ItemWidget> {
        ItemStack itemStack;

        public Builder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public ItemWidget build() {
            return new ItemWidget(itemStack, modID);
        }
    }
}