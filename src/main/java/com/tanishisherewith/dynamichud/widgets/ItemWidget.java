package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

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
    public void renderWidget(DrawContext context, int mouseX, int mouseY) {
        context.drawItem(item, x, y);
        widgetBox.setDimensions(getX(), getY(), 16, 16, this.shouldScale, GlobalConfig.get().getScale());
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putInt("ItemID", Item.getRawId(item.getItem()));
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        item = Item.byRawId(tag.getInt("ItemID")).getDefaultStack();
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