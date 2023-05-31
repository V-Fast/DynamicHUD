package net.dynamichud.dynamichud.Widget;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;

public interface Widgetfactory {
    static void loadWidgetsFromTag(String className, NbtCompound widgetTag)
    {
        DynamicUtil dynamicUtil=new DynamicUtil(MinecraftClient.getInstance());
        if (className.equals(TextWidget.class.getName())) {
            String text = widgetTag.getString("text");
            float xPercent = widgetTag.getFloat("xPercent");
            float yPercent = widgetTag.getFloat("yPercent");
            boolean rainbow = widgetTag.getBoolean("Rainbow");
            boolean shadow = widgetTag.getBoolean("Shadow");
            boolean verticalrainbow = widgetTag.getBoolean("VerticalRainbow");
            int color = widgetTag.getInt("Color");
            dynamicUtil.getWidgetManager().addWidget(new TextWidget(MinecraftClient.getInstance(), text, xPercent, yPercent, shadow, rainbow, verticalrainbow, color));
        }
        if (className.equals(ArmorWidget.class.getName())) {
            EquipmentSlot slot = EquipmentSlot.byName(widgetTag.getString("slot"));
            float xPercent = widgetTag.getFloat("xPercent");
            float yPercent = widgetTag.getFloat("yPercent");
            dynamicUtil.getWidgetManager().addWidget(new ArmorWidget(MinecraftClient.getInstance(), slot, xPercent, yPercent));
        }
    }
}
