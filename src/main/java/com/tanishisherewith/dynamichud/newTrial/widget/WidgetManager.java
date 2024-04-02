package com.tanishisherewith.dynamichud.newTrial.widget;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.*;

import static com.tanishisherewith.dynamichud.DynamicHUD.printInfo;
import static com.tanishisherewith.dynamichud.DynamicHUD.printWarn;

/**
 * Manages a collection of widgets, providing methods to add, remove, save, and load widgets.
 */
public class WidgetManager {
    /**
     * The list of widgets managed by this manager.
     */
    private static final List<Widget> widgets = new ArrayList<>();

    /**
     * A map from widget names to WidgetData objects, used for creating new widgets.
     */
    private static final Map<String, WidgetData<?>> widgetDataMap = new TreeMap<>();

    /**
     * Adds a WidgetData object to the map.
     *
     * @param data The WidgetData object to add.
     */
    public static void addWidgetData(WidgetData<?> data){
        widgetDataMap.put(data.name(),data);
    }

    /**
     * Adds multiple WidgetData objects to the map.
     *
     * @param widgetDatas The WidgetData objects to add.
     */
    public static void addWidgetDatas(WidgetData<?>... widgetDatas){
        for(WidgetData<?> data: widgetDatas) {
            widgetDataMap.put(data.name(), data);
        }
    }

    /**
     * Adds a widget to the list of managed widgets.
     *
     * @param widget The widget to add.
     */
    public static void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     * Adds multiple widgets to the list of managed widgets.
     *
     * @param widget The widgets to add.
     */
    public static void addWidgets(Widget... widget) {
        widgets.addAll(Arrays.stream(widget).toList());
    }

    /**
     * Removes a widget from the list of managed widgets.
     *
     * @param widget The widget to remove.
     */
    public static void removeWidget(Widget widget) {
        widgets.remove(widget);
    }

    /**
     * Saves the state of all widgets to the given file.
     *
     * @param file The file to save to
     */
    public static void saveWidgets(File file) throws IOException {
        NbtCompound rootTag = new NbtCompound();
        NbtList widgetList = new NbtList();

        printInfo("Saving widgets");

        if (widgets.isEmpty()) {
            printWarn("Widgets are empty.. Saving interrupted to prevent empty file");
            return;
        }

        Set<String> widgetSet = new HashSet<>();
        for (Widget widget : widgets) {
            NbtCompound widgetTag = new NbtCompound();
            widget.writeToTag(widgetTag);
            // Check for duplicates
            if (widgetSet.add(widgetTag.toString())) {
                widgetList.add(widgetTag);
            }
        }

        rootTag.put("widgets", widgetList);

        // Use a temporary file to write the data
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(tempFile))) {
            NbtIo.write(rootTag, out);
            // Check if the data has been written successfully
            if (tempFile.length() > 0) {
                // Check if the temporary file exists and can be renamed
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IOException("Failed to write data to temporary file OR Empty data passed");
            }
        }
    }

    /**
     * Loads the state of all widgets from the given file.
     *
     * @param file The file to load from
     */
    public static void loadWidgets(File file) throws IOException {
        widgets.clear();

        if (file.exists()) {
                NbtCompound rootTag = NbtIo.read(file.toPath());
                NbtList widgetList = rootTag.getList("widgets", NbtType.COMPOUND);

                for (int i = 0; i < widgetList.size(); i++) {
                    NbtCompound widgetTag = widgetList.getCompound(i);
                    WidgetData<?> widgetData = widgetDataMap.get(widgetTag.getString("name"));
                    Widget widget = widgetData.createWidget();
                    widget.readFromTag(widgetTag);
                    widgets.add(widget);
                }
        }else{
            printWarn("Widget File does not exist. Try saving one first");
        }
    }

    /**
     * Returns the list of managed widgets.
     *
     * @return The list of managed widgets.
     */
    public static List<Widget> getWidgets() {
        return widgets;
    }
}
