package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.integration.DynamicHudConfigurator;
import com.tanishisherewith.dynamichud.integration.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

public class IntegrationTest implements DynamicHudIntegration {
    TextWidget FPSWidget;
    TextWidget HelloWidget;
    TextWidget DynamicHUDWidget;
    DynamicValueRegistry registry;

    @Override
    public void init() {
        //Global registry
        DynamicValueRegistry.registerGlobal("FPS", () -> "FPS: " + DynamicHUD.MC.getCurrentFps());

        //Local registry
        registry = new DynamicValueRegistry(DynamicHUD.MOD_ID);
        registry.registerLocal("Hello", () -> "Hello " + DynamicHUD.MC.getSession().getUsername() + "!");
        registry.registerLocal("DynamicHUD", () -> "DynamicHUD");

        FPSWidget = new TextWidget.Builder()
                .setX(250)
                .setY(150)
                .setDraggable(true)
                .rainbow(false)
                .withRegistryKey("FPS")
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(false)
                .build();

        HelloWidget = new TextWidget.Builder()
                .setX(200)
                .setY(100)
                .setDraggable(true)
                .rainbow(false)
                .withRegistryKey("Hello")
                .withValueRegistry(registry)
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .shadow(true)
                .build();

        DynamicHUDWidget = new TextWidget.Builder()
                .setX(0)
                .setY(0)
                .setDraggable(false)
                .rainbow(true)
                .withRegistryKey("DynamicHUD")
                .withValueRegistry(registry)
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .build();

    }

    @Override
    public DynamicHudConfigurator configure(DynamicHudConfigurator configurator) {
        configurator.addWidget(FPSWidget)
                .addWidget(HelloWidget)
                .addWidget(DynamicHUDWidget)
                .configureRenderer(renderer -> {
                    renderer.shouldRenderInGameHud(true);
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
