package net.dynamichud.dynamichud.Widget;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages a list of widgets that can be added, removed and retrieved.
 */
public class WidgetManager implements loading {

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
     *
     * @return list of all widgets.
     */
    public List<Widget> getWidgets() {
        return widgets;
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

    public List<Widget> loadWigdets(File file) {
        List<Widget> widgets = new ArrayList<>();
        if (file.exists()) {
            System.out.println("Widgets File exists");
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                NbtCompound rootTag = NbtIo.readCompressed(in);
                NbtList widgetList = rootTag.getList("widgets", NbtType.COMPOUND);
                for (int i = 0; i < widgetList.size(); i++) {
                    NbtCompound widgetTag = widgetList.getCompound(i);
                    String className = widgetTag.getString("class");
                    widgets.add(loadWidgetsFromTag(className, widgetTag));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println("Widgets File does not exist");
        System.out.println("Wigdets: " + widgets);
        return widgets;
    }
}