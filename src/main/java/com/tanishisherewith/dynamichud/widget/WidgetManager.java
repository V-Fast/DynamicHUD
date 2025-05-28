package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.mixins.ScreenMixin;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

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
    public static void registerCustomWidget(WidgetData<?> data) {
        widgetDataMap.put(data.name(), data);
    }

    /**
     * Adds multiple WidgetData objects to the map.
     *
     * @param widgetDatas The WidgetData objects to add.
     */
    public static void registerCustomWidgets(WidgetData<?>... widgetDatas) {
        for (WidgetData<?> data : widgetDatas) {
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
     * Attempts to restore the widgets back to their place on screen resize.
     * <p>
     * It works by storing the position of widgets as relative to the screen size before the resize
     * and then using that percentage to restore the widget to their appropriate place.
     * <p>
     * Widgets will move around on smaller GUI scales.
     * Larger the GUI scale, more accurate is the position on any resolution.
     * </p>
     * <p>
     * Called in {@link ScreenMixin}
     * </p>
     * </p>
     *
     * @param newWidth       Screen width after resize
     * @param newHeight      Screen height after resize
     * @param previousWidth  Screen width before resize
     * @param previousHeight Screen height before resize
     */
    public static void onScreenResized(int newWidth, int newHeight, int previousWidth, int previousHeight) {
        for (Widget widget : widgets) {
            // To ensure that infinite coordinates is not returned for the first time its resized.

            widget.updatePosition(newWidth, newHeight);

            /*
            if (widget.xPercent <= 0.0f) {
                widget.xPercent = (float) widget.getX() / previousWidth;
            }
            if (widget.yPercent <= 0.0f) {
                widget.yPercent = (float) widget.getY() / previousHeight;
            }

            widget.updatePositionFromPercentages(newWidth, newHeight);

            widget.xPercent = (widget.getX() + widget.getWidth() / 2) / newWidth;
            widget.yPercent = (widget.getY() + widget.getHeight() / 2) / newHeight;

             */
        }
    }


    /**
     * Saves the state of all widgets to the given file.
     *
     * @param file The file to save to
     */
    public static void saveWidgets(File file, List<Widget> widgets) throws IOException {
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
            //I faced this exception once and had to spend 10 minutes trying to find it. P.S. It leaves 0 stacktrace message
            try {
                widget.writeToTag(widgetTag);
            } catch (Throwable e){
                DynamicHUD.logger.error("Widget [{}] occurred an exception while writing to NBT. This may be due to duplicate entries or invalid entry values", widget.toString());
            }

            // Check for duplicates
            if (widgetSet.add(widgetTag.toString())) {
                printInfo("Saving Widget: " + widget);
                widgetList.add(widgetTag);
            }
        }

        rootTag.put("widgets", widgetList);

        // Backup the old file
        File backupFile = new File(file.getAbsolutePath() + ".backup");
        if (file.exists()) {
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // Write the data to a temporary file
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(tempFile))) {
            NbtIo.write(rootTag, out);
        } catch (IOException e) {
            DynamicHUD.logger.warn("Error while saving", e);
            // If save operation failed, restore the backup
            Files.move(backupFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            throw e;  // rethrow the exception
        }

        // If save operation was successful, replace the old file with the new one
        Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Loads the state of all widgets from the given file.
     *
     * @param file The file to load from
     */
    public static List<Widget> loadWidgets(File file) throws IOException {
        if (!file.exists()) {
            // The backup file check is done in the doesWidgetFileExist() function below this
            DynamicHUD.logger.warn("Main file {} was not found... Loading from a found backup file", file.getAbsolutePath());
            file = new File(file.getAbsolutePath() + ".backup");
        }

        NbtCompound rootTag = NbtIo.read(file.toPath());

        if (rootTag == null) {
            printWarn("RootTag is null. File is either empty or corrupted: " + file);
            return Collections.emptyList();
        }
        NbtList widgetList = rootTag.getList("widgets", NbtElement.COMPOUND_TYPE);
        if (widgetList == null) {
            printWarn("WidgetList is null. File is empty: " + file);
            return Collections.emptyList();
        }
        List<Widget> widgetsToAdd = new ArrayList<>();
        for (int i = 0; i < widgetList.size(); i++) {
            NbtCompound widgetTag = widgetList.getCompound(i);
            WidgetData<?> widgetData = widgetDataMap.get(widgetTag.getString("name"));
            if (widgetData == null) {
                throw new IllegalStateException("A Widget named '" + widgetTag.getString("name") + "' was found in the save file, but no matching WidgetData for its class could be located. This may indicate that the mod responsible for saving this widget has been removed or its implementation is incorrect.");
            }
            Widget widget = widgetData.createWidget();
            widget.readFromTag(widgetTag);
            printInfo("Loaded Widget: " + widget);
            widgetsToAdd.add(widget);
            widgets.add(widget);
        }
        return widgetsToAdd;
    }

    /**
     * Checks if the given file exists, or if a backup file exists.
     */
    public static boolean doesWidgetFileExist(File file) {
        return file.exists() || new File(file.getAbsolutePath() + ".backup").exists();
    }

    /**
     * Returns the list of managed widgets.
     *
     * @return The list of managed widgets.
     */
    public static List<Widget> getWidgets() {
        return widgets;
    }

    /**
     * Returns the list of managed widgets with the same modID.
     *
     * @return The list of managed widgets with the same modID.
     */
    public static List<Widget> getWidgetsForMod(String modID) {
        return getWidgets().stream()
                .filter(widget -> modID.equalsIgnoreCase(widget.getModId()))
                .toList();
    }

    public static Map<String, WidgetData<?>> getWidgetDataMap() {
        return widgetDataMap;
    }
}
