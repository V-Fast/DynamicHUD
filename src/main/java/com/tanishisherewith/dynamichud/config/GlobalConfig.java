package com.tanishisherewith.dynamichud.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.*;

public final class GlobalConfig {
    public static final ConfigClassHandler<GlobalConfig> HANDLER = ConfigClassHandler.createBuilder(GlobalConfig.class)
            .id(Identifier.fromNamespaceAndPath("dynamichud", "dynamichud_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("dynamichud.json5"))
                    .setJson5(true)
                    .build())
            .build();

    private static final GlobalConfig INSTANCE = new GlobalConfig();

    @SerialEntry
    private boolean dragSelectionEnabled = true;

    @SerialEntry
    private Color dashedOutlineColor = Color.WHITE;

    @SerialEntry
    private float dashedOutlineThickness = 1.0f;

    @SerialEntry
    private boolean showLockButton = true;

    @SerialEntry
    private int lockButtonSize = 6;

    @SerialEntry
    private int scaleDotSize = 3;

    @SerialEntry
    private float scaleSensitivity = 1.0f;

    /**
     * Common scale for all widgets.
     */
    @SerialEntry
    private float scale = 1.0f;

    @SerialEntry
    private int cmAnimationTimeInMs = 200;

    @SerialEntry
    private boolean displayDescriptions = false;

    @SerialEntry
    private boolean showColorPickerPreview = true;

    @SerialEntry
    private boolean renderInDebugScreen = false;

    @SerialEntry
    private boolean smartSnapping = true;

    @SerialEntry
    private final boolean forceSameContextMenuSkin = true;

    //These package names are getting seriously long
    @SerialEntry
    private com.tanishisherewith.dynamichud.utils.contextmenu.options.Option.Complexity complexity = com.tanishisherewith.dynamichud.utils.contextmenu.options.Option.Complexity.Simple;

    @SerialEntry
    private int snapSize = 100;

    @SerialEntry
    private Color hudActiveColor = new Color(0, 0, 0, 128);

    @SerialEntry
    private Color hudInactiveColor = new Color(255, 0, 0, 128);

    public static GlobalConfig get() {
        return INSTANCE;
    }

    public Screen createYACLGUI() {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("DynamicHUD config screen."))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("General"))
                        .tooltip(Component.literal("Set the general settings for all widgets."))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Global"))
                                .description(OptionDescription.of(Component.literal("Global settings for all widgets.")))
                                .option(Option.<Float>createBuilder()
                                        .name(Component.literal("Scale"))
                                        .description(OptionDescription.of(Component.literal("Set scale for all widgets.")))
                                        .binding(1.0f, () -> this.scale, newVal -> this.scale = newVal)
                                        .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption).range(0.1f, 2.5f).step(0.1f))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Render in debug screen"))
                                        .description(OptionDescription.of(Component.literal("Renders widgets even when the debug screen is on")))
                                        .binding(false, () -> this.renderInDebugScreen, newVal -> this.renderInDebugScreen = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Show Color picker preview"))
                                        .description(OptionDescription.of(Component.literal("Shows the preview below your mouse pointer on selecting color from the screen. Note: You may drop some frames with the preview on.")))
                                        .binding(true, () -> this.showColorPickerPreview, newVal -> this.showColorPickerPreview = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Show widget descriptions/tooltips"))
                                        .description(OptionDescription.of(Component.literal("Shows the description of widgets as tooltips.")))
                                        .binding(false, () -> this.displayDescriptions, newVal -> this.displayDescriptions = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Smart Snapping"))
                                        .description(OptionDescription.of(Component.literal("Enables widgets to automatically snap to each other or the center of the screen, displaying alignment guidelines while dragging")))
                                        .binding(true, () -> this.smartSnapping, newVal -> this.smartSnapping = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Snap Size"))
                                        .description(OptionDescription.of(Component.literal("Grid size for snapping widgets")))
                                        .binding(100, () -> this.snapSize, newVal -> this.snapSize = newVal)
                                        .controller(integerOption -> IntegerFieldControllerBuilder.create(integerOption).range(10, 500))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("ContextMenu Animation Time"))
                                        .description(OptionDescription.of(Component.literal("The time in seconds for context menu to open")))
                                        .binding(200, () -> this.cmAnimationTimeInMs, newVal -> this.cmAnimationTimeInMs = newVal)
                                        .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption).range(0, 500).step(1))
                                        .build())
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Component.literal("Widget HUD Active Background Color"))
                                .description(OptionDescription.of(Component.literal("Color of the background of the widget when it will be rendered")))
                                .binding(new Color(0, 0, 0, 128), () -> this.hudActiveColor, newVal -> this.hudActiveColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Component.literal("Widget HUD Inactive Background Color"))
                                .description(OptionDescription.of(Component.literal("Color of the background of the widget when it will NOT be rendered")))
                                .binding(new Color(255, 0, 0, 128), () -> this.hudInactiveColor, newVal -> this.hudInactiveColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<com.tanishisherewith.dynamichud.utils.contextmenu.options.Option.Complexity>createBuilder()
                                .name(Component.literal("Settings Complexity"))
                                .description(OptionDescription.of(Component.literal("The level of options to display. Options equal to or below this level will be displayed")))
                                .binding(com.tanishisherewith.dynamichud.utils.contextmenu.options.Option.Complexity.Simple, () -> this.complexity, newVal -> this.complexity = newVal)
                                .controller((option) -> EnumControllerBuilder.create(option)
                                        .enumClass(com.tanishisherewith.dynamichud.utils.contextmenu.options.Option.Complexity.class)
                                        .formatValue(value -> Component.literal(value.name()))
                                )
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Advanced UI"))
                        .tooltip(Component.literal("Configure drag selection, lock size, keybind shortcuts, and outlines."))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Drag & Grouping"))
                                .description(OptionDescription.of(Component.literal("Configure grouping and selection.")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable Drag Selection"))
                                        .description(OptionDescription.of(Component.literal("Draw selection boxes on empty space to select widgets.")))
                                        .binding(true, () -> this.dragSelectionEnabled, newVal -> this.dragSelectionEnabled = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())

                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Overlays & Sizing"))
                                .description(OptionDescription.of(Component.literal("Configure thickness, colors, and button sizes.")))
                                .option(Option.<Color>createBuilder()
                                        .name(Component.literal("Dashed Outline Color"))
                                        .description(OptionDescription.of(Component.literal("Color of the dashed bounding boxes of hovered/selected widgets.")))
                                        .binding(Color.WHITE, () -> this.dashedOutlineColor, newVal -> this.dashedOutlineColor = newVal)
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Component.literal("Dashed Outline Thickness"))
                                        .description(OptionDescription.of(Component.literal("Thickness of the dashed bounding boxes.")))
                                        .binding(1.0f, () -> this.dashedOutlineThickness, newVal -> this.dashedOutlineThickness = newVal)
                                        .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption).range(0.5f, 3.0f).step(0.1f))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Show Lock Button"))
                                        .description(OptionDescription.of(Component.literal("Show the lock toggle icon on hovered widgets.")))
                                        .binding(true, () -> this.showLockButton, newVal -> this.showLockButton = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Lock Button Size"))
                                        .description(OptionDescription.of(Component.literal("Dimensions of the lock toggle button.")))
                                        .binding(6, () -> this.lockButtonSize, newVal -> this.lockButtonSize = newVal)
                                        .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption).range(4, 12).step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Scale Dot Size"))
                                        .description(OptionDescription.of(Component.literal("Dimensions of the resize anchor dot.")))
                                        .binding(3, () -> this.scaleDotSize, newVal -> this.scaleDotSize = newVal)
                                        .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption).range(1, 6).step(1))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Component.literal("Scale Sensitivity"))
                                        .description(OptionDescription.of(Component.literal("Sensitivity multiplier for resize-dragging (1.0 is exact visual tracking).")))
                                        .binding(1.0f, () -> this.scaleSensitivity, newVal -> this.scaleSensitivity = newVal)
                                        .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption).range(0.1f, 3.0f).step(0.1f))
                                        .build())
                                .build())
                        .build())
                .save(HANDLER::save)
                .build()
                .generateScreen(Minecraft.getInstance().screen);
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

    public com.tanishisherewith.dynamichud.utils.contextmenu.options.Option.Complexity complexity() {
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
