package com.tanishisherewith.dynamichud.interfaces;

import com.tanishisherewith.dynamichud.helpers.TextureHelper;
import com.tanishisherewith.dynamichud.widget.armor.ArmorWidget;
import com.tanishisherewith.dynamichud.widget.item.ItemWidget;
import com.tanishisherewith.dynamichud.widget.text.TextWidget;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.awt.*;

public interface WidgetLoading {
    default Widget loadWidgetsFromTag(String className, NbtCompound widgetTag) {
        if (className.equals(TextWidget.class.getName())) {
            TextWidget widget = new TextWidget(MinecraftClient.getInstance(), "", () -> "", 0, 0, false, false, false, -1, -1, true);
            widget.readFromTag(widgetTag);
            return widget;
        }
        if (className.equals(ArmorWidget.class.getName())) {
            ArmorWidget widget = new ArmorWidget(MinecraftClient.getInstance(), EquipmentSlot.CHEST, 0, 0, false, TextureHelper.Position.ABOVE, () -> "", () -> Color.RED,true);
            widget.readFromTag(widgetTag);
            return widget;
        }
        if (className.equals(ItemWidget.class.getName())) {
            ItemWidget widget = new ItemWidget(MinecraftClient.getInstance(), () -> ItemStack.EMPTY, 0, 0, true, TextureHelper.Position.ABOVE, () -> "", () -> Color.WHITE,true);
            widget.readFromTag(widgetTag);
            return widget;
        }
        return null;
    }
}
