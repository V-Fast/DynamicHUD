package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.interfaces.WidgetLoading;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import static com.tanishisherewith.dynamichud.DynamicHUD.printInfo;

/**
 * This class manages a list of widgets that can be added, removed and retrieved.
 */
public class WidgetManager {
    private final Set<Widget> widgets = new HashSet<>(); // The list of widgets
    private final Set<Widget> MainMenuWidgets = new HashSet<>(); // The list of MainMenu widgets
    private WidgetLoading widgetLoading = new WidgetLoading() {
    };

    /**
     * Adds a widget to the list.
     *
     * @param widget The widget to add
     */
    public void addWidget(Widget widget) {
        widget.setTextGeneratorFromLabel();
        widgets.add(widget);
    }

    /**
     * Adds a MainMenu widget to the list.
     *
     * @param widget The widget to add
     */
    public void addMainMenuWidget(Widget widget) {
        widget.setTextGeneratorFromLabel();
        MainMenuWidgets.add(widget);
    }

    public void setWidgetLoading(WidgetLoading widgetLoading) {
        this.widgetLoading = widgetLoading;
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
     * Removes a MainMenu widget from the list.
     *
     * @param widget The Main Menu widget to remove
     */
    public void removeMainMenuWidget(Widget widget) {
        MainMenuWidgets.remove(widget);
    }


    /**
     * Returns list of all widgets.
     *
     * @return list of all widgets.
     */
    public Set<Widget> getWidgets() {
        return widgets;
    }

    /**
     * Returns Set of all MainMenu widgets.
     *
     * @return Set of all MainMenu widgets.
     */
    public Set<Widget> getMainMenuWidgets() {
        return MainMenuWidgets;
    }

    public Set<Widget> getOtherWidgets(Widget SelectedWidget) {
        Set<Widget> otherWidgets = new HashSet<>();
        for (Widget widget : getWidgets()) {
            if (widget != SelectedWidget) {
                otherWidgets.add(widget);
            }
        }
        return otherWidgets;
    }

    /**
     * Saves the state of all widgets to the given file.
     *
     * @param file The file to save to
     */
    public void saveWidgets(File file) {
        NbtCompound rootTag = new NbtCompound();
        NbtList widgetList = new NbtList();
        NbtList MainMenuwidgetList = new NbtList();

        printInfo("Saving widgets");

        if (widgets.isEmpty() && MainMenuWidgets.isEmpty()) {
            printInfo("Widgets are empty.. Saving interrupted to prevent empty file");
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

        rootTag.put("Widgets", widgetList);

        Set<String> MainMenuWidgetSet = new HashSet<>();
        for (Widget mmwidget : MainMenuWidgets) {
            NbtCompound widgetTag = new NbtCompound();
            mmwidget.writeToTag(widgetTag);
            // Check for duplicates
            if (MainMenuWidgetSet.add(widgetTag.toString())) {
                MainMenuwidgetList.add(widgetTag);
            }
        }
        rootTag.put("MainMenuWidgets", MainMenuwidgetList);

        // Use a temporary file to write the data
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(tempFile))) {
            NbtIo.writeCompressed(rootTag, out);
            // Check if the data has been written successfully
            if (tempFile.length() > 0) {
                // Check if the temporary file exists and can be renamed
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IOException("Failed to write data to temporary file OR Empty data passed");
            }
        } catch (IOException e) {
            // Delete the temporary file if an error occurs
            boolean temp = tempFile.delete();
            e.printStackTrace();
        }
    }



    public Set<Widget> loadWigdets(File file) {
        Set<Widget> widgets = new HashSet<>();
        if (file.exists()) {
            printInfo("Widgets File exists");
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                DataInput input = new DataInputStream(in);
                NbtCompound rootTag = NbtIo.readCompound(input);
                NbtList widgetList = rootTag.getList("Widgets", NbtType.COMPOUND);
                for (int i = 0; i < widgetList.size(); i++) {
                    NbtCompound widgetTag = widgetList.getCompound(i);
                    String className = widgetTag.getString("class");
                    widgets.add(widgetLoading.loadWidgetsFromTag(className, widgetTag));
                    printInfo("Wigdet " + i + ": " + widgets.stream().toList().get(i).toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            DynamicHUD.printWarn("Widgets File does not exist");
        return widgets;
    }

    public Set<Widget> loadMainMenuWigdets(File file) {
        Set<Widget> MainMenuwidgets = new HashSet<>();
        if (file.exists()) {
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                DataInput input = new DataInputStream(in);
                NbtCompound rootTag = NbtIo.readCompound(input);
                NbtList MainMenuwidgetList = rootTag.getList("MainMenuWidgets", NbtType.COMPOUND);
                for (int i = 0; i < MainMenuwidgetList.size(); i++) {
                    NbtCompound widgetTag = MainMenuwidgetList.getCompound(i);
                    String className = widgetTag.getString("class");
                    MainMenuwidgets.add(widgetLoading.loadWidgetsFromTag(className, widgetTag));
                    printInfo("MainMenu Wigdet " + i + ": " + MainMenuwidgets.stream().toList().get(i).toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            DynamicHUD.printWarn("Widgets File does not exist");
        return MainMenuwidgets;
    }
}