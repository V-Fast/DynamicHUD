package com.tanishisherewith.dynamichud.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GlobalConfig {
    public static final ConfigClassHandler<GlobalConfig> HANDLER = ConfigClassHandler.createBuilder(GlobalConfig.class)
            .id(new Identifier("dynamichud", "dynamichud_config"))
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
    private boolean showColorPickerPreview = true;

    public static GlobalConfig get() {
        return INSTANCE;
    }

    public final Screen createYACLGUI() {
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
                                .build())
                        .build())
                .build()
                .generateScreen(null);
    }
    public float getScale(){
        return scale;
    }

    public boolean showColorPickerPreview() {
        return showColorPickerPreview;
    }
}
