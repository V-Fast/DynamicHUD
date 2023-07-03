package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.interfaces.WidgetLoading;
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
public class WidgetManager {


    private final List<Widget> widgets = new ArrayList<>(); // The list of widgets
    private final List<Widget> MainMenuWidgets = new ArrayList<>(); // The list of MainMenu widgets
    private WidgetLoading widgetLoading = new WidgetLoading() {};

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
    public List<Widget> getWidgets() {
        return widgets;
    }
    /**
     * Returns list of all MainMenu widgets.
     *
     * @return list of all MainMenu widgets.
     */
    public List<Widget> getMainMenuWidgets() {
        return MainMenuWidgets;
    }
    public List<Widget> getOtherWidgets(Widget SelectedWidget) {
        List<Widget> otherWidgets = new ArrayList<>();
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

        DynamicHUD.printInfo("Saving widgets");

        for (Widget widget : widgets) {
            NbtCompound widgetTag = new NbtCompound();
            widget.writeToTag(widgetTag);
            widgetList.add(widgetTag);
        }

        rootTag.put("widgets", widgetList);
        for (Widget mmwidget : MainMenuWidgets) {
            NbtCompound widgetTag = new NbtCompound();
            mmwidget.writeToTag(widgetTag);
            MainMenuwidgetList.add(widgetTag);
        }
        rootTag.put("MainMenuwidgets", MainMenuwidgetList);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            NbtIo.writeCompressed(rootTag, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Widget> loadWigdets(File file) {
        List<Widget> widgets = new ArrayList<>();
        if (file.exists()) {
            DynamicHUD.printInfo("Widgets File exists");
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                NbtCompound rootTag = NbtIo.readCompressed(in);
                NbtList widgetList = rootTag.getList("widgets", NbtType.COMPOUND);
                for (int i = 0; i < widgetList.size(); i++) {
                    NbtCompound widgetTag = widgetList.getCompound(i);
                    String className = widgetTag.getString("class");
                    widgets.add(widgetLoading.loadWidgetsFromTag(className, widgetTag));
                    DynamicHUD.printInfo("Wigdet " + i+ ": "+widgets.get(i).toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            DynamicHUD.printWarn("Widgets File does not exist");
        return widgets;
    }
    public List<Widget> loadMainMenuWigdets(File file) {
        List<Widget> MainMenuwidgets = new ArrayList<>();
        if (file.exists()) {
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                NbtCompound rootTag = NbtIo.readCompressed(in);
                NbtList MainMenuwidgetList = rootTag.getList("MainMenuwidgets", NbtType.COMPOUND);
                for (int i = 0; i < MainMenuwidgetList.size(); i++) {
                    NbtCompound widgetTag = MainMenuwidgetList.getCompound(i);
                    String className = widgetTag.getString("class");
                    MainMenuwidgets.add(widgetLoading.loadWidgetsFromTag(className, widgetTag));
                    DynamicHUD.printInfo("Wigdet " + i+ ": "+MainMenuwidgets.get(i).toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            DynamicHUD.printWarn("Widgets File does not exist");
        return MainMenuwidgets;
    }
}