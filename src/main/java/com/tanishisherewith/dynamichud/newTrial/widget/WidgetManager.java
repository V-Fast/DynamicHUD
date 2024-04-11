package com.tanishisherewith.dynamichud.newTrial.widget;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.tanishisherewith.dynamichud.DynamicHUD;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.*;
import net.minecraft.util.math.MathHelper;

import static com.tanishisherewith.dynamichud.DynamicHUD.*;

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
 /*   public static void onScreenResized(int newWidth, int newHeight) {
        float scaleX = (float) newWidth / MC.getWindow().getScaledWidth();
        float scaleY = (float) newHeight / MC.getWindow().getScaledHeight();
        System.out.println("newWidth: " + newWidth);
        System.out.println("newHeight: " + newHeight);

        System.out.println("scaleX: " + scaleX);

        for (Widget widget : widgets) {
            float newX = widget.getX() * scaleX;
            float newY = widget.getY() * scaleY;
            System.out.println("newX: " + newX);

            // Ensure the widget is within the screen bounds
            newX = MathHelper.clamp(newX,0,newWidth - widget.getWidth());
            newY = MathHelper.clamp(newY,0,newHeight - widget.getHeight());
            System.out.println("newX2: " + newX);

            widget.setPosition(newX, newY);
        }
    }

  */

    /**
     * Attempts to restore the widgets back to their place on screen resize.
     * <p>
     * It works by storing the position of widgets as relative to the screen size before the resize
     * and then using that percentage to restore the widget to their appropriate place.
     * <p>
     * Widgets will move around on smaller GUI scales.
     * Larger the GUI scale, more accurate is the position.
     * </p>
     * <p>
     *     Called in {@link com.tanishisherewith.dynamichud.mixins.ScreenMixin}
     * </p>
     * </p>
     *
     * @param newWidth Screen width after resize
     * @param newHeight Screen height after resize
     * @param previousWidth Screen width before resize
     * @param previousHeight Screen height before resize
     */
 public static void onScreenResized(int newWidth, int newHeight, int previousWidth, int previousHeight) {
     for (Widget widget : widgets) {
         // To ensure that infinite coords is not returned
         if(widget.xPercent <= 0.0f){
             widget.xPercent = (float) widget.getX()/previousWidth;
         }
         if(widget.yPercent <= 0.0f){
             widget.yPercent = (float) widget.getY()/previousHeight;
         }

         // Use the stored percentages to calculate the new position
         float newX = widget.xPercent * newWidth;
         float newY = widget.yPercent * newHeight;

         // Ensure the widget is within the screen bounds
         newX = MathHelper.clamp(newX, 0, newWidth - widget.getWidth());
         newY = MathHelper.clamp(newY, 0, newHeight - widget.getHeight());

         // Update the widget's position
         widget.setPosition((int) newX, (int) newY);

         // Update the stored percentages with the new screen size (after resize).
         widget.xPercent = (float) widget.getX() / newWidth;
         widget.yPercent =  (float) widget.getY() / newHeight;
     }
 }


    /**
     * Saves the state of all widgets to the given file.
     *
     * @param file The file to save to
     */
    public static void saveWidgets(File file,List<Widget> widgets) throws IOException {
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
    public static void loadWidgets(File file) throws IOException {
        widgets.clear();

        if (file.exists()) {
                NbtCompound rootTag = NbtIo.read(file.toPath());
                NbtList widgetList = rootTag.getList("widgets", NbtType.COMPOUND);

                for (int i = 0; i < widgetList.size(); i++) {
                    NbtCompound widgetTag = widgetList.getCompound(i);
                    WidgetData<?> widgetData = widgetDataMap.get(widgetTag.getString("name"));
                    Widget widget = widgetData.createWidget();
                    printInfo("Loading Widget: " + widget);
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
}
