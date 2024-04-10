package com.tanishisherewith.dynamichud.newTrial.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GlobalConfig {
    private static GlobalConfig INSTANCE = new GlobalConfig();
    public static ConfigClassHandler<GlobalConfig> HANDLER = ConfigClassHandler.createBuilder(GlobalConfig.class)
            .id(new Identifier("dynamichud", "dynamichud_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("dynamichud.json5"))
                    .setJson5(true)
                    .build())
            .build();

    /**
     * Common scale for all widgets. Set by the user using YACL.
     */
    @SerialEntry
    public float scale = 1.0f;

    public Screen createYACLGUI() {
      return  YetAnotherConfigLib.createBuilder()
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
                                        .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption).range(0.1f,2.5f).step(0.1f))
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(null);
    }
    public static GlobalConfig get(){
        return INSTANCE;
    }
}
