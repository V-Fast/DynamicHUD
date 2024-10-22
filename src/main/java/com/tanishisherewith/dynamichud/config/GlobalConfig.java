package com.tanishisherewith.dynamichud.config;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.config.v2.impl.autogen.ColorFieldImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public final class GlobalConfig {
    public static final ConfigClassHandler<GlobalConfig> HANDLER = ConfigClassHandler.createBuilder(GlobalConfig.class)
            .id(Identifier.of("dynamichud", "dynamichud_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("dynamichud.json5"))
                    .setJson5(true)
                    .build())
            .build();

    private static final GlobalConfig INSTANCE = new GlobalConfig();
    /**
     * Common scale for all widgets. Set by the user using YACL.
     */
    @SerialEntry
    private float scale = 1.0f;

    @SerialEntry
    private boolean displayDescriptions = false;

    @SerialEntry
    private boolean showColorPickerPreview = true;

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
                .title(Text.literal("DynamicHUD config screen."))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .tooltip(Text.literal("Set the general settings for all widgets."))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Global"))
                                .description(OptionDescription.of(Text.literal("Global settings for all widgets.")))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Scale"))
                                        .description(OptionDescription.of(Text.literal("Set scale for all widgets.")))
                                        .binding(1.0f, () -> this.scale, newVal -> this.scale = newVal)
                                        .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption).range(0.1f, 2.5f).step(0.1f))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Show Color picker preview"))
                                        .description(OptionDescription.of(Text.literal("Shows the preview below your mouse pointer on selecting color from the screen. Note: You may drop some frames with the preview on.")))
                                        .binding(true, () -> this.showColorPickerPreview, newVal -> this.showColorPickerPreview = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Show widget descriptions/tooltips"))
                                        .description(OptionDescription.of(Text.literal("Shows the description of widgets as tooltips.")))
                                        .binding(true, () -> this.displayDescriptions, newVal -> this.displayDescriptions = newVal)
                                        .controller(booleanOption -> BooleanControllerBuilder.create(booleanOption).yesNoFormatter())
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.literal("Snap Size"))
                                        .description(OptionDescription.of(Text.literal("Grid size for snapping widgets")))
                                        .binding(100, () -> this.snapSize, newVal -> this.snapSize = newVal)
                                        .controller(integerOption -> IntegerFieldControllerBuilder.create(integerOption).range(10, 500))
                                        .build())
                                .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Widget HUD Active Background Color"))
                                        .description(OptionDescription.of(Text.literal("Color of the background of the widget when it will be rendered")))
                                        .binding(new Color(0, 0, 0, 128), () -> this.hudActiveColor, newVal -> this.hudActiveColor = newVal)
                                        .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.literal("Widget HUD Inactive Background Color"))
                                .description(OptionDescription.of(Text.literal("Color of the background of the widget when it will NOT be rendered")))
                                .binding(new Color(255, 0, 0, 128), () -> this.hudInactiveColor, newVal -> this.hudInactiveColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .build())
                .save(HANDLER::save)
                .build()
                .generateScreen(null);
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

    public int getSnapSize() {
        return snapSize;
    }

    public Color getHudInactiveColor() {
        return hudInactiveColor;
    }

    public Color getHudActiveColor() {
        return hudActiveColor;
    }
}
