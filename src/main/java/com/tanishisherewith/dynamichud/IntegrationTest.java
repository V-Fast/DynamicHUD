package com.tanishisherewith.dynamichud;

import com.mojang.blaze3d.platform.InputConstants;
import com.tanishisherewith.dynamichud.integration.DynamicHudConfigurator;
import com.tanishisherewith.dynamichud.integration.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widgets.GraphWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class IntegrationTest implements DynamicHudIntegration {
    TextWidget FPSWidget;
    TextWidget HelloWidget;
    TextWidget DynamicHUDWidget;
    GraphWidget graphWidget;
    DynamicValueRegistry registry;

    @Override
    public void init() {
        //Global registry
        // We recommend using the syntax "modid:key_name" for easier debugging and to prevent data conflicts in global registries.
        DynamicValueRegistry.registerGlobal("dynamichud:FPS", () -> "FPS: " + DynamicHUD.MC.getFps());

        //Local registry
        registry = new DynamicValueRegistry(DynamicHUD.MOD_ID);
        registry.registerLocal("Hello", () -> "Hello " + DynamicHUD.MC.getGameProfile().name() + "!");
        registry.registerLocal("DynamicHUD", () -> "DynamicHUD");
        registry.registerLocal("FPS", () -> DynamicHUD.MC.getFps());

        FPSWidget = new TextWidget.Builder()
                .setX(250)
                .setY(150)
                .setDraggable(true)
                .rainbow(false)
                .registryKey("dynamichud:FPS")
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(false)
                .build();

        HelloWidget = new TextWidget.Builder()
                .setX(200)
                .setY(100)
                .setDraggable(true)
                .rainbow(false)
                .registryKey("Hello")
                .registryID(registry.getId())
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .shadow(true)
                .build();

        DynamicHUDWidget = new TextWidget.Builder()
                .setX(0)
                .setY(0)
                .setDraggable(false)
                .rainbow(true)
                .registryKey("DynamicHUD")
                .registryID(registry.getId())
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
                .gHeight(75)
                .gridLines(10)
                .backgroundColor(Color.DARK_GRAY)
                .lineThickness(1f)
                .maxDataPoints(100)
                .maxValue(120)
                .minValue(30)
                .setModID(DynamicHUD.MOD_ID)
                .setDraggable(true)
                .setIsVisible(true)
                .showGrid(true)
                .shouldScale(true)
                .registryKey("FPS")
                .registryID(registry.getId())
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
                .withMoveableScreen(config -> new AbstractMoveableScreen(Component.literal("Editor Screen"), config.getRenderer()) {});

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
