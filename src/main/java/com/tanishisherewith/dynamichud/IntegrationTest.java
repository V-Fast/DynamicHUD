package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.integration.DynamicHudConfigurator;
import com.tanishisherewith.dynamichud.integration.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widgets.GraphWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class IntegrationTest implements DynamicHudIntegration {
    TextWidget FPSWidget;
    TextWidget HelloWidget;
    TextWidget DynamicHUDWidget;
    GraphWidget graphWidget;
    DynamicValueRegistry registry;

    @Override
    public void init() {

        // In rainbow mode, all styles except color will be kept as is.

        // "FPS: " in gray + live number in bright green (or red if low)
        DynamicValueRegistry.register(
                Identifier.fromNamespaceAndPath("dynamichud", "fps"),
                () -> {
                    int fps = DynamicHUD.MC.getFps();
                    TextColor fpsColor = fps < 30 ? TextColor.fromRgb(0xFF5555) :  // red
                            fps < 60 ? TextColor.fromRgb(0xFFFF55) :  // yellow
                                    TextColor.fromRgb(0x55FF55);   // green

                    return Component.literal("")
                            .append(Component.literal("FPS: ")
                                    .withStyle(Style.EMPTY
                                            .withColor(TextColor.fromRgb(0xAAAAAA))
                                            .withBold(true)))
                            .append(Component.literal(String.valueOf(fps))
                                    .withStyle(Style.EMPTY
                                            .withColor(fpsColor)
                                            .withBold(true)
                                            .withItalic(fps < 30))); // italic for fun
                }
        );

        // "Hello " in gold + username in aqua bold underlined
        DynamicValueRegistry.register(
                Identifier.fromNamespaceAndPath(DynamicHUD.MOD_ID, "hello"),
                () -> Component.literal("")
                        .append(Component.literal("Hello ")
                                .withStyle(Style.EMPTY
                                        .withColor(ChatFormatting.GOLD)
                                ))
                        .append(Component.literal(DynamicHUD.MC.getGameProfile().name())
                                .withStyle(Style.EMPTY
                                        .withColor(ChatFormatting.AQUA)
                                        .withBold(true)
                                        .withUnderlined(true)))
                        .append(Component.literal("!")
                                .withStyle(Style.EMPTY
                                        .withColor(ChatFormatting.GOLD)))
        );

        //"Dynamic" in red + "HUD" in green (backdrop of modrinth)
        DynamicValueRegistry.register(
                Identifier.fromNamespaceAndPath(DynamicHUD.MOD_ID, "dynamichud"),
                () -> Component.literal("")
                        .append(Component.literal("Dynamic")
                                .withStyle(Style.EMPTY
                                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))
                                ))
                        .append(Component.literal("HUD")
                                .withStyle(Style.EMPTY
                                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN))
                                )
                        )
        );

        DynamicValueRegistry.register(
                Identifier.fromNamespaceAndPath(DynamicHUD.MOD_ID, "fps_graph"),
                () -> DynamicHUD.MC.getFps()
        );

        FPSWidget = new TextWidget.Builder()
                .setX(250)
                .setY(150)
                .setLocked(false)
                .rainbow(false)
                .valueId(Identifier.fromNamespaceAndPath("dynamichud", "fps"))
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(false)
                .build();

        HelloWidget = new TextWidget.Builder()
                .setX(200)
                .setY(100)
                .setLocked(false)
                .rainbow(false)
                .valueId(Identifier.fromNamespaceAndPath(DynamicHUD.MOD_ID, "hello"))
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .shadow(true)
                .build();

        DynamicHUDWidget = new TextWidget.Builder()
                .setX(0)
                .setY(0)
                .setLocked(true)
                .rainbow(true)
                .valueId(Identifier.fromNamespaceAndPath(DynamicHUD.MOD_ID, "dynamichud"))
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .build();

        graphWidget = new GraphWidget.GraphWidgetBuilder()
                .setX(250)
                .setY(100)
                .label("FPS Chart")
                .graphColor(Color.CYAN)
                .anchor(Widget.Anchor._default())
                .gWidth(100)
                .gHeight(60)
                .gridLines(10)
                .backgroundColor(Color.BLACK)
                .lineThickness(0.6f)
                .maxDataPoints(100)
                .maxValue(120)
                .minValue(30)
                .setModID(DynamicHUD.MOD_ID)
                .setLocked(false)
                .setIsVisible(true)
                .showGrid(true)
                .shouldScale(true)
                .valueId(Identifier.fromNamespaceAndPath(DynamicHUD.MOD_ID, "fps_graph"))
                .build()
                .setSampleInterval(120)
                .autoUpdateRange();
    }

    @Override
    public DynamicHudConfigurator configure(DynamicHudConfigurator configurator) {
        configurator.addWidget(FPSWidget)
                .addWidget(HelloWidget)
                .addWidget(DynamicHUDWidget)
                .addWidget(graphWidget)
                .configureRenderer(renderer -> {
                    //Already true by default
                    //renderer.shouldRenderInGameHud(true);
                    renderer.addScreen(TitleScreen.class);
                })
                .withMoveableScreen(config -> new AbstractMoveableScreen(Component.literal("Editor Screen"), config.getRenderer()) {
                });

        return configurator;
    }

    @Override
    public void registerCustomWidgets() {
        //WidgetManager.addWidgetData(MyWidget.DATA);
    }

    @Override
    public KeyMapping getKeyBind() {
        return DynamicHUD.EDITOR_SCREEN_KEYBIND;
    }
}
