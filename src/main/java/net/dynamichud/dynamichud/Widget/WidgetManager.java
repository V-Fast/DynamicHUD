package net.dynamichud.dynamichud.Widget;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages a list of widgets that can be added, removed and retrieved.
 */
public class WidgetManager {

    private final List<Widget> widgets = new ArrayList<>(); // The list of widgets

    /**
     * Adds a widget to the list.
     *
     * @param widget The widget to add
     */
    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     * Removes a widget from the list.
     *
     * @param widget The widget to remove
     */
    public void removeWidget(Widget widget) {
        widgets.remove(widget);
    }

    /**
     * Returns list of all widgets.
     *@return list of all widgets.
     */
    public List<Widget> getWidgets() {
        return widgets;
    }
    public void loadWidgetsFromTag(String className, NbtCompound widgetTag)
    {
        if (className.equals(TextWidget.class.getName())) {
            String text = widgetTag.getString("text");
            float xPercent = widgetTag.getFloat("xPercent");
            float yPercent = widgetTag.getFloat("yPercent");
            boolean rainbow = widgetTag.getBoolean("Rainbow");
            boolean shadow = widgetTag.getBoolean("Shadow");
            boolean verticalrainbow = widgetTag.getBoolean("VerticalRainbow");
            int color = widgetTag.getInt("Color");
            boolean enabled=widgetTag.getBoolean("Enabled");
            addWidget(new TextWidget(MinecraftClient.getInstance(), text, xPercent, yPercent, shadow, rainbow, verticalrainbow, color,enabled));
        } else if (className.equals(ArmorWidget.class.getName())) {
            EquipmentSlot slot = EquipmentSlot.byName(widgetTag.getString("slot"));
            float xPercent = widgetTag.getFloat("xPercent");
            float yPercent = widgetTag.getFloat("yPercent");
            boolean enabled=widgetTag.getBoolean("Enabled");
            addWidget(new ArmorWidget(MinecraftClient.getInstance(), slot, xPercent, yPercent,enabled));
        }
    }

    /**
     * Saves the state of all widgets to the given file.
     *
     * @param file The file to save to
     */
    public void saveWidgets(File file) {
        NbtCompound rootTag = new NbtCompound();
        NbtList widgetList = new NbtList();

        System.out.println("Saving widgets");

        for (Widget widget : widgets) {
            NbtCompound widgetTag = new NbtCompound();
            widget.writeToTag(widgetTag);
            widgetList.add(widgetTag);
        }

        rootTag.put("widgets", widgetList);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            NbtIo.writeCompressed(rootTag, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadWigdets(File file) {
        if (file.exists()) {
            System.out.println("File exists");
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                NbtCompound rootTag = NbtIo.readCompressed(in);
                NbtList widgetList = rootTag.getList("widgets", NbtType.COMPOUND);

                for (int i = 0; i < widgetList.size(); i++) {
                    NbtCompound widgetTag = widgetList.getCompound(i);
                    String className = widgetTag.getString("class");
                    loadWidgetsFromTag(className,widgetTag);
                    System.out.println("Wigdets: "+widgets);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else System.out.println("File does not exist");
    }
}