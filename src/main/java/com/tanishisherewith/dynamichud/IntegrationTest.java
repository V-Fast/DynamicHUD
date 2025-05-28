package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.integration.DynamicHudConfigurator;
import com.tanishisherewith.dynamichud.integration.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widgets.GraphWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

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
        DynamicValueRegistry.registerGlobal("dynamichud:FPS", () -> "FPS: " + DynamicHUD.MC.getCurrentFps());

        //Local registry
        registry = new DynamicValueRegistry(DynamicHUD.MOD_ID);
        registry.registerLocal("Hello", () -> "Hello " + DynamicHUD.MC.getSession().getUsername() + "!");
        registry.registerLocal("DynamicHUD", () -> "DynamicHUD");
        registry.registerLocal("FPS", () -> DynamicHUD.MC.getCurrentFps());

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
                .anchor(Widget.Anchor.CENTER)
                .height(100)
                .width(150)
                .gridLines(10)
                .backgroundColor(Color.DARK_GRAY)
                .lineThickness(1f)
                .maxDataPoints(100)
                .maxValue(120)
                .minValue(30)
                .setModID(DynamicHUD.MOD_ID)
                .setDraggable(true)
                .setDisplay(true)
                .showGrid(true)
                .registryKey("FPS")
                .registryID(registry.getId())
                .build()
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
                .withMoveableScreen(config -> new AbstractMoveableScreen(Text.literal("Editor Screen"), config.getRenderer()) {});

        return configurator;
    }

    @Override
    public void registerCustomWidgets() {
        //WidgetManager.addWidgetData(MyWidget.DATA);
    }
}
