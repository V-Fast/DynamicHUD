package net.dynamichud.dynamichud.Util;

import net.dynamichud.dynamichud.Widget.ArmorWidget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.ItemWidget.ItemWidget;
import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.helpers.TextureHelper;
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
            ArmorWidget widget = new ArmorWidget(MinecraftClient.getInstance(), EquipmentSlot.CHEST, 0, 0, false, TextureHelper.Position.ABOVE, () -> "");
            widget.readFromTag(widgetTag);
            return widget;
        }
        if (className.equals(ItemWidget.class.getName())) {
            ItemWidget widget = new ItemWidget(MinecraftClient.getInstance(), ItemStack.EMPTY, 0, 0, true, TextureHelper.Position.ABOVE, () -> "", Color.BLUE);
            widget.readFromTag(widgetTag);
            return widget;
        }
        return null;
    }
}
