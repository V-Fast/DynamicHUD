package com.tanishisherewith.dynamichud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.internal.ColorTypeAdapter;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutEngine;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.screen.ContextMenuScreen;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.MinecraftSkin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class GlobalConfig {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .setPrettyPrinting()
            .create();

    private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve("dynamichud.json5").toFile();

    private static GlobalConfig INSTANCE = new GlobalConfig();


    // Fields:-

    private boolean dragSelectionEnabled = true;
    private Color dashedOutlineColor = Color.WHITE;
    private float dashedOutlineThickness = 1.0f;
    private boolean showLockButton = true;
    private int lockButtonSize = 6;
    private int scaleDotSize = 3;
    private float scaleSensitivity = 1.0f;
    /**
     * Common scale for all widgets.
     */
    private float scale = 1.0f;
    private int cmAnimationTimeInMs = 200;
    private boolean displayDescriptions = false;
    private boolean showColorPickerPreview = true;
    private boolean renderInDebugScreen = false;
    private boolean smartSnapping = true;
    private final boolean forceSameContextMenuSkin = true;
    //These package names are getting seriously long
    private Option.Complexity complexity = Option.Complexity.Simple;
    private int snapSize = 100;
    private Color hudActiveColor = new Color(0, 0, 0, 128);
    private Color hudInactiveColor = new Color(255, 0, 0, 128);

    //Mouse, Render and keyboard events are handled in OptionsScreenMixin.java
    private static ContextMenu<?> MENU = null;

    public static GlobalConfig get() {
        return INSTANCE;
    }

    private GlobalConfig() {}

    public static ContextMenu<?> getMenu() {
        return MENU;
    }

    private static ContextMenuProperties createProperties(){
        return ContextMenuProperties.builder()
                .skin(new MinecraftSkin(MinecraftSkin.PanelColor.DARK_PANEL))
                .accentColor(Color.GREEN)
                .borderColor(Color.DARK_GRAY)
                .borderWidth(2.0f)
                .build();
    }


    public void save() {
        DynamicHUD.logger.info("Saving global config...");
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GlobalConfig fromJson(String json) {
        return GSON.fromJson(json, GlobalConfig.class);
    }

    public static GlobalConfig loadOrCreate() {
        DynamicHUD.logger.info("Loading global config...");
        if (!configFile.exists()) {
            get().save();
            return get();
        }

        try (FileReader reader = new FileReader(configFile)) {
            INSTANCE = GSON.fromJson(reader, GlobalConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return INSTANCE;
    }

    public static ContextMenu<?> createMenu() {
        ContextMenu<?> temp = new ContextMenu<>(0, 0, createProperties());
        temp.setLayoutEngine(new LayoutEngine(4, 2, 2, 80));

        GlobalConfig cfg = get();

        OptionGroup general = new OptionGroup(Component.literal("General"));
        general.description(Component.literal("Set the general settings for all widgets"));
        general.addOption(new DoubleOption(Component.literal("Scale"), 0.1f, 2.5f, 0.1f, () -> (double) cfg.scale, newVal -> cfg.scale = newVal.floatValue(), temp).description(Component.literal("The scale to be applied on all widgets.")));
        general.addOption(new BooleanOption(Component.literal("Render in debug screen"), () -> cfg.renderInDebugScreen, newVal -> cfg.renderInDebugScreen = newVal).description(Component.literal("Renders widgets even when the debug screen is on")));
        general.addOption(new BooleanOption(Component.literal("Show Color picker preview"), () -> cfg.showColorPickerPreview, newVal -> cfg.showColorPickerPreview = newVal, BooleanOption.BooleanType.YES_NO).description(Component.literal("Shows the preview below your mouse pointer on selecting color from the screen. Note: You may drop some frames with the preview on.")));
        general.addOption(new BooleanOption(Component.literal("Display widget descriptions"), () -> cfg.displayDescriptions, newVal -> cfg.displayDescriptions = newVal, BooleanOption.BooleanType.YES_NO).description(Component.literal("Shows the description of widgets as tooltips.")));
        general.addOption(new BooleanOption(Component.literal("Smart Snapping"), () -> cfg.smartSnapping, newVal -> cfg.smartSnapping = newVal, BooleanOption.BooleanType.YES_NO).description((Component.literal("Enables widgets to automatically snap to each other or the center of the screen, displaying alignment guidelines while dragging"))));
        general.addOption(new DoubleOption(Component.literal("Snap Size"), 10, 500, 1, () -> (double) cfg.snapSize, newVal -> cfg.snapSize = newVal.intValue(), temp).description(Component.literal("Grid size for snapping widgets")));
        general.addOption(new DoubleOption(Component.literal("ContextMenu Animation Time"), 0, 500, 1, () -> (double) cfg.cmAnimationTimeInMs, newVal -> cfg.cmAnimationTimeInMs = newVal.intValue(), temp).description(Component.literal("Grid size for snapping widgets")));
        general.addOption(new CycleOption<Object>(Component.literal("Settings Complexity"), () -> cfg.complexity, newVal -> cfg.complexity = (Option.Complexity) newVal, Option.Complexity.values()).description(Component.literal("The level of options to display. Options equal to or below cfg level will be displayed")));

        OptionGroup colors = new OptionGroup(Component.literal("Colors"));
        colors.addOption(new ColorOption(Component.literal("Widget Active Background Color"), () -> cfg.hudActiveColor, newVal -> cfg.hudActiveColor = newVal, temp).description(Component.literal("Color of the background of the widget when it will be rendered")));
        colors.addOption(new ColorOption(Component.literal("Widget Inactive Background Color"), () -> cfg.hudInactiveColor, newVal -> cfg.hudInactiveColor = newVal, temp).description(Component.literal("Color of the background of the widget when it will NOT be rendered")));
        colors.addOption(new ColorOption(Component.literal("Dashed Outline Color"), () -> cfg.dashedOutlineColor, newVal -> cfg.dashedOutlineColor = newVal, temp).description(Component.literal("Color of the dashed bounding boxes of hovered/selected widgets.")));

        OptionGroup advancedUI = new OptionGroup(Component.literal("Advanced UI"));
        advancedUI.description(Component.literal("Configure drag selection, lock size, keybind shortcuts, and outlines"));
        advancedUI.addOption(new BooleanOption(Component.literal("Enable Drag Selection"), () -> cfg.dragSelectionEnabled, newVal -> cfg.dragSelectionEnabled = newVal).description(Component.literal("Draw selection boxes on empty space to select widgets")));
        advancedUI.addOption(new DoubleOption(Component.literal("Dashed Outline Thickness"), 0.5f, 3.0f, 0.1f, () -> (double) cfg.dashedOutlineThickness, newVal -> cfg.dashedOutlineThickness = newVal.floatValue(), temp).description(Component.literal("Grid size for snapping widgets")));
        advancedUI.addOption(new BooleanOption(Component.literal("Show Lock Button"), () -> cfg.showLockButton, newVal -> cfg.showLockButton = newVal).description(Component.literal("Show the lock toggle icon on hovered widgets")));
        advancedUI.addOption(new DoubleOption(Component.literal("Lock Button Size"), 4, 12, 1, () -> (double) cfg.lockButtonSize, newVal -> cfg.lockButtonSize = newVal.intValue(), temp).description(Component.literal("Grid size for snapping widgets")));
        advancedUI.addOption(new DoubleOption(Component.literal("Scale Dot Size"), 1, 6, 1, () -> (double) cfg.scaleDotSize, newVal -> cfg.scaleDotSize = newVal.intValue(), temp).description(Component.literal("Dimensions of the resize anchor dot")));
        advancedUI.addOption(new DoubleOption(Component.literal("Scale Sensitivity"), 0.1f, 3.0f, 0.1f, () -> (double) cfg.scaleSensitivity, newVal -> cfg.scaleSensitivity = newVal.floatValue(), temp).description(Component.literal("Sensitivity multiplier for resize-dragging (1.0 is exact visual tracking)")));

        temp.addOption(general);
        temp.addOption(colors);
        temp.addOption(advancedUI);

        return temp;
    }

    public Screen openMenu() {
        if(MENU == null) {
            MENU = createMenu();
        }
        MENU.open();
        return new ContextMenuScreen(MENU);
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public float getScale() {
        return scale;
    }

    public boolean showColorPickerPreview() {
        return showColorPickerPreview;
    }

    public boolean shouldDisplayDescriptions() {
        return displayDescriptions;
    }

    public boolean renderInDebugScreen() {
        return renderInDebugScreen;
    }

    public int getSnapSize() {
        return snapSize;
    }

    public Color getHudInactiveColor() {
        return hudInactiveColor;
    }

    public Color getHudActiveColor() {
        return hudActiveColor;
    }

    public int getCmAnimationTimeInMs() {
        return cmAnimationTimeInMs;
    }

    public boolean doSmartSnapping() {
        return smartSnapping;
    }

    public Option.Complexity complexity() {
        return complexity;
    }

    public boolean isDragSelectionEnabled() {
        return dragSelectionEnabled;
    }

    public Color getDashedOutlineColor() {
        return dashedOutlineColor;
    }

    public float getDashedOutlineThickness() {
        return dashedOutlineThickness;
    }

    public boolean showLockButton() {
        return showLockButton;
    }

    public int getLockButtonSize() {
        return lockButtonSize;
    }

    public int getScaleDotSize() {
        return scaleDotSize;
    }

    public float getScaleSensitivity() {
        return scaleSensitivity;
    }
}
